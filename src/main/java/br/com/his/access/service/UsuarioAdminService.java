package br.com.his.access.service;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.his.access.model.Colaborador;
import br.com.his.access.model.ColaboradorUnidadeAtuacao;
import br.com.his.access.model.ColaboradorUnidadeVinculo;
import br.com.his.access.model.Usuario;
import br.com.his.access.model.UsuarioColaborador;
import br.com.his.access.repository.ColaboradorRepository;
import br.com.his.access.repository.ColaboradorUnidadeAtuacaoRepository;
import br.com.his.access.repository.ColaboradorUnidadeVinculoRepository;
import br.com.his.access.repository.UnidadeRepository;
import br.com.his.access.repository.UsuarioRepository;
import br.com.his.access.repository.UsuarioColaboradorRepository;
import br.com.his.access.dto.ColaboradorUnidadeAtuacaoForm;
import br.com.his.access.dto.UsuarioAtuacaoForm;
import br.com.his.access.dto.UsuarioEdicaoForm;
import br.com.his.access.dto.UsuarioNovoForm;
import br.com.his.access.keycloak.KeycloakAdminClient;
import br.com.his.access.service.ColaboradorUnidadeAtuacaoAdminService;

@Service
public class UsuarioAdminService {

    private final UsuarioRepository usuarioRepository;
    private final UnidadeRepository unidadeRepository;
    private final ColaboradorRepository colaboradorRepository;
    private final UsuarioColaboradorRepository usuarioColaboradorRepository;
    private final ColaboradorUnidadeVinculoRepository colaboradorUnidadeVinculoRepository;
    private final ColaboradorUnidadeAtuacaoRepository colaboradorUnidadeAtuacaoRepository;
    private final ColaboradorUnidadeAtuacaoAdminService colaboradorUnidadeAtuacaoAdminService;
    private final KeycloakAdminClient keycloakAdminClient;

    public UsuarioAdminService(UsuarioRepository usuarioRepository,
                               UnidadeRepository unidadeRepository,
                               ColaboradorRepository colaboradorRepository,
                               UsuarioColaboradorRepository usuarioColaboradorRepository,
                               ColaboradorUnidadeVinculoRepository colaboradorUnidadeVinculoRepository,
                               ColaboradorUnidadeAtuacaoRepository colaboradorUnidadeAtuacaoRepository,
                               ColaboradorUnidadeAtuacaoAdminService colaboradorUnidadeAtuacaoAdminService,
                               KeycloakAdminClient keycloakAdminClient) {
        this.usuarioRepository = usuarioRepository;
        this.unidadeRepository = unidadeRepository;
        this.colaboradorRepository = colaboradorRepository;
        this.usuarioColaboradorRepository = usuarioColaboradorRepository;
        this.colaboradorUnidadeVinculoRepository = colaboradorUnidadeVinculoRepository;
        this.colaboradorUnidadeAtuacaoRepository = colaboradorUnidadeAtuacaoRepository;
        this.colaboradorUnidadeAtuacaoAdminService = colaboradorUnidadeAtuacaoAdminService;
        this.keycloakAdminClient = keycloakAdminClient;
    }

    @Transactional(readOnly = true)
    public List<Usuario> listar(String filtro) {
        String normalized = normalize(filtro);
        if (normalized == null) {
            return usuarioRepository.findAllByOrderByUsernameAsc();
        }
        return usuarioRepository.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrderByUsernameAsc(
                normalized, normalized);
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario nao encontrado: " + id));
    }

    @Transactional(readOnly = true)
    public Optional<UsuarioColaborador> buscarVinculoColaborador(Long usuarioId) {
        buscarPorId(usuarioId);
        return usuarioColaboradorRepository.findByUsuarioIdComColaborador(usuarioId);
    }

    @Transactional(readOnly = true)
    public List<Colaborador> listarColaboradoresParaVinculo(Long usuarioId) {
        List<Colaborador> ativos = new ArrayList<>(colaboradorRepository.findAtivosOrderByNomeAsc());
        Optional<UsuarioColaborador> atual = usuarioColaboradorRepository.findByUsuarioIdComColaborador(usuarioId);
        if (atual.isPresent()) {
            Colaborador colaboradorAtual = atual.get().getColaborador();
            boolean jaEstaNaLista = ativos.stream()
                    .anyMatch(colaborador -> colaborador.getId().equals(colaboradorAtual.getId()));
            if (!jaEstaNaLista) {
                ativos.add(colaboradorAtual);
            }
        }
        return ativos;
    }

    @Transactional
    public Usuario criarNoKeycloakERegistrarEspelho(UsuarioNovoForm form) {
        if (!keycloakAdminClient.isEnabled()) {
            String username = normalize(form.getUsername());
            if (isBlank(username)) {
                throw new IllegalArgumentException("Username e obrigatorio");
            }
            String provisionalKeycloakId = "pending:" + username.toLowerCase();
            return upsertEspelho(provisionalKeycloakId, username, form.getEmail());
        }

        String keycloakId = keycloakAdminClient.criarUsuario(
                normalize(form.getUsername()),
                normalize(form.getEmail()),
                normalize(form.getNome()),
                normalize(form.getSobrenome()),
                normalize(form.getSenhaTemporaria()),
                form.isExigirTrocaSenha());
        return upsertEspelho(keycloakId, form.getUsername(), form.getEmail());
    }

    @Transactional
    public String provisionarNoKeycloak(Long usuarioId) {
        Usuario usuario = buscarPorId(usuarioId);
        if (!keycloakAdminClient.isEnabled()) {
            throw new IllegalArgumentException("Integracao Keycloak Admin desabilitada");
        }
        if (isBlank(usuario.getUsername())) {
            throw new IllegalArgumentException("Usuario sem username nao pode ser provisionado");
        }

        String temporaryPassword = gerarSenhaTemporaria(usuario.getUsername());
        String keycloakId = keycloakAdminClient.provisionarOuRecriarUsuario(
                usuario.getUsername(),
                usuario.getEmail(),
                null,
                null,
                temporaryPassword,
                true);

        usuarioRepository.findByKeycloakId(keycloakId)
                .filter(existing -> !existing.getId().equals(usuario.getId()))
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("Keycloak ID retornado ja vinculado a outro usuario no HIS: " + existing.getUsername());
                });

        usuario.setKeycloakId(keycloakId);
        usuario.setAtivo(true);
        salvar(usuario);
        return temporaryPassword;
    }

    @Transactional
    public Usuario upsertEspelho(String keycloakId, String username, String email) {
        if (isBlank(keycloakId)) {
            throw new IllegalArgumentException("keycloak_id e obrigatorio");
        }
        Optional<Usuario> existing = usuarioRepository.findByKeycloakId(keycloakId);
        if (existing.isPresent()) {
            Usuario usuario = existing.get();
            usuario.setUsername(normalize(username));
            usuario.setEmail(normalize(email));
            usuario.setAtivo(true);
            return usuarioRepository.save(usuario);
        }

        Usuario usuario = new Usuario();
        usuario.setKeycloakId(keycloakId);
        usuario.setUsername(normalize(username));
        usuario.setEmail(normalize(email));
        usuario.setAtivo(true);
        return salvar(usuario);
    }

    @Transactional(readOnly = true)
    public UsuarioEdicaoForm toEdicaoForm(Long usuarioId) {
        Usuario usuario = buscarPorId(usuarioId);
        UsuarioEdicaoForm form = new UsuarioEdicaoForm();
        form.setUsername(usuario.getUsername());
        form.setEmail(usuario.getEmail());
        form.setAtivo(usuario.isAtivo());
        return form;
    }

    @Transactional
    public Usuario atualizarDados(Long usuarioId, UsuarioEdicaoForm form) {
        Usuario usuario = buscarPorId(usuarioId);
        usuario.setUsername(normalize(form.getUsername()));
        usuario.setEmail(normalize(form.getEmail()));
        usuario.setAtivo(form.isAtivo());
        return salvar(usuario);
    }

    @Transactional
    public void atualizarVinculoColaborador(Long usuarioId, Long colaboradorId) {
        Usuario usuario = buscarPorId(usuarioId);
        Optional<UsuarioColaborador> vinculoAtualOpt = usuarioColaboradorRepository.findByUsuarioId(usuarioId);

        if (colaboradorId == null) {
            vinculoAtualOpt.ifPresent(vinculoAtual -> {
                vinculoAtual.setAtivo(false);
                usuarioColaboradorRepository.save(vinculoAtual);
            });
            return;
        }

        Colaborador colaborador = colaboradorRepository.findById(colaboradorId)
                .orElseThrow(() -> new IllegalArgumentException("Colaborador nao encontrado: " + colaboradorId));
        if (!colaborador.isAtivo()) {
            throw new IllegalArgumentException("Nao e permitido vincular colaborador inativo");
        }

        Optional<UsuarioColaborador> vinculoPorColaboradorOpt =
                usuarioColaboradorRepository.findByColaboradorIdComUsuario(colaboradorId);
        if (vinculoPorColaboradorOpt.isPresent()) {
            UsuarioColaborador vinculoPorColaborador = vinculoPorColaboradorOpt.get();
            if (!vinculoPorColaborador.getUsuario().getId().equals(usuarioId)) {
                throw new IllegalArgumentException("Este colaborador ja esta vinculado a outro usuario");
            }
        }

        UsuarioColaborador vinculo = vinculoAtualOpt.orElseGet(UsuarioColaborador::new);
        vinculo.setUsuario(usuario);
        vinculo.setColaborador(colaborador);
        vinculo.setAtivo(true);
        try {
            usuarioColaboradorRepository.save(vinculo);
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("Nao foi possivel salvar o vinculo usuario-colaborador");
        }
    }

    @Transactional(readOnly = true)
    public List<ColaboradorUnidadeAtuacao> listarAtuacoesDoUsuario(Long usuarioId) {
        Optional<UsuarioColaborador> vinculoColaborador = usuarioColaboradorRepository.findByUsuarioIdComColaborador(usuarioId);
        if (vinculoColaborador.isEmpty() || !vinculoColaborador.get().isAtivo()) {
            return List.of();
        }
        return colaboradorUnidadeAtuacaoRepository.findByColaboradorIdComDetalhesOrderByContextoAsc(
                vinculoColaborador.get().getColaborador().getId());
    }

    @Transactional
    public ColaboradorUnidadeAtuacao adicionarAtuacaoDoUsuario(Long usuarioId, UsuarioAtuacaoForm form) {
        UsuarioColaborador vinculoUsuarioColaborador = usuarioColaboradorRepository.findByUsuarioIdComColaborador(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario sem colaborador vinculado"));
        if (!vinculoUsuarioColaborador.isAtivo()) {
            throw new IllegalArgumentException("Vinculo usuario-colaborador esta inativo");
        }

        Colaborador colaborador = vinculoUsuarioColaborador.getColaborador();
        ColaboradorUnidadeVinculo vinculoUnidade = colaboradorUnidadeVinculoRepository
                .findByColaboradorIdAndUnidadeId(colaborador.getId(), form.getUnidadeId())
                .orElseGet(() -> {
                    ColaboradorUnidadeVinculo novo = new ColaboradorUnidadeVinculo();
                    novo.setColaborador(colaborador);
                    novo.setUnidade(unidadeRepository.findById(form.getUnidadeId())
                            .orElseThrow(() -> new IllegalArgumentException("Unidade nao encontrada")));
                    novo.setAtivo(true);
                    return colaboradorUnidadeVinculoRepository.save(novo);
                });

        if (!vinculoUnidade.isAtivo()) {
            vinculoUnidade.setAtivo(true);
            colaboradorUnidadeVinculoRepository.save(vinculoUnidade);
        }

        ColaboradorUnidadeAtuacaoForm atuacaoForm = new ColaboradorUnidadeAtuacaoForm();
        atuacaoForm.setColaboradorUnidadeVinculoId(vinculoUnidade.getId());
        atuacaoForm.setFuncaoUnidadeId(form.getFuncaoUnidadeId());
        atuacaoForm.setPerfilId(form.getPerfilId());
        atuacaoForm.setEspecialidadeId(form.getEspecialidadeId());
        atuacaoForm.setAtivo(true);
        return colaboradorUnidadeAtuacaoAdminService.criar(atuacaoForm);
    }

    @Transactional
    public void removerAtuacaoDoUsuario(Long usuarioId, Long atuacaoId) {
        UsuarioColaborador vinculoUsuarioColaborador = usuarioColaboradorRepository.findByUsuarioIdComColaborador(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario sem colaborador vinculado"));
        Long colaboradorId = vinculoUsuarioColaborador.getColaborador().getId();

        ColaboradorUnidadeAtuacao atuacao = colaboradorUnidadeAtuacaoRepository
                .findByIdAndColaboradorIdComDetalhes(atuacaoId, colaboradorId)
                .orElseThrow(() -> new IllegalArgumentException("Atuacao nao encontrada para o usuario"));

        colaboradorUnidadeAtuacaoAdminService.excluir(atuacao.getId());
    }

    private Usuario salvar(Usuario usuario) {
        try {
            return usuarioRepository.save(usuario);
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("Usuario ja cadastrado com username ou keycloak_id");
        }
    }

    private static String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private static String gerarSenhaTemporaria(String username) {
        String base = username == null ? "usuario" : username.replaceAll("[^A-Za-z0-9]", "");
        if (base.isBlank()) {
            base = "usuario";
        }
        if (base.length() > 10) {
            base = base.substring(0, 10);
        }
        return "His@" + base + "123!";
    }
}
