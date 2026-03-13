package br.com.his.access.service;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.his.access.model.CargoColaborador;
import br.com.his.access.model.Colaborador;
import br.com.his.access.model.ColaboradorUnidadeAtuacao;
import br.com.his.access.model.ColaboradorUnidadeVinculo;
import br.com.his.access.model.Usuario;
import br.com.his.access.model.UsuarioColaborador;
import br.com.his.access.repository.CargoColaboradorRepository;
import br.com.his.access.repository.ColaboradorRepository;
import br.com.his.access.repository.ColaboradorUnidadeAtuacaoRepository;
import br.com.his.access.repository.ColaboradorUnidadeVinculoRepository;
import br.com.his.access.repository.UnidadeRepository;
import br.com.his.access.repository.UsuarioRepository;
import br.com.his.access.repository.UsuarioColaboradorRepository;
import br.com.his.access.repository.UsuarioUnidadePerfilRepository;
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
    private final CargoColaboradorRepository cargoColaboradorRepository;
    private final ColaboradorRepository colaboradorRepository;
    private final UsuarioColaboradorRepository usuarioColaboradorRepository;
    private final ColaboradorUnidadeVinculoRepository colaboradorUnidadeVinculoRepository;
    private final ColaboradorUnidadeAtuacaoRepository colaboradorUnidadeAtuacaoRepository;
    private final ColaboradorUnidadeAtuacaoAdminService colaboradorUnidadeAtuacaoAdminService;
    private final UsuarioUnidadePerfilRepository usuarioUnidadePerfilRepository;
    private final KeycloakAdminClient keycloakAdminClient;

    public UsuarioAdminService(UsuarioRepository usuarioRepository,
                               UnidadeRepository unidadeRepository,
                               CargoColaboradorRepository cargoColaboradorRepository,
                               ColaboradorRepository colaboradorRepository,
                               UsuarioColaboradorRepository usuarioColaboradorRepository,
                               ColaboradorUnidadeVinculoRepository colaboradorUnidadeVinculoRepository,
                               ColaboradorUnidadeAtuacaoRepository colaboradorUnidadeAtuacaoRepository,
                               ColaboradorUnidadeAtuacaoAdminService colaboradorUnidadeAtuacaoAdminService,
                               UsuarioUnidadePerfilRepository usuarioUnidadePerfilRepository,
                               KeycloakAdminClient keycloakAdminClient) {
        this.usuarioRepository = usuarioRepository;
        this.unidadeRepository = unidadeRepository;
        this.cargoColaboradorRepository = cargoColaboradorRepository;
        this.colaboradorRepository = colaboradorRepository;
        this.usuarioColaboradorRepository = usuarioColaboradorRepository;
        this.colaboradorUnidadeVinculoRepository = colaboradorUnidadeVinculoRepository;
        this.colaboradorUnidadeAtuacaoRepository = colaboradorUnidadeAtuacaoRepository;
        this.colaboradorUnidadeAtuacaoAdminService = colaboradorUnidadeAtuacaoAdminService;
        this.usuarioUnidadePerfilRepository = usuarioUnidadePerfilRepository;
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
        Usuario usuarioCriado;
        if (!keycloakAdminClient.isEnabled()) {
            String username = normalize(form.getUsername());
            if (isBlank(username)) {
                throw new IllegalArgumentException("Username e obrigatorio");
            }
            String provisionalKeycloakId = "pending:" + username.toLowerCase();
            usuarioCriado = upsertEspelho(provisionalKeycloakId, username, form.getEmail());
        } else {
            String keycloakId = keycloakAdminClient.criarUsuario(
                    normalize(form.getUsername()),
                    normalize(form.getEmail()),
                    normalize(form.getNome()),
                    normalize(form.getSobrenome()),
                    normalize(form.getSenhaTemporaria()),
                    form.isExigirTrocaSenha());
            usuarioCriado = upsertEspelho(keycloakId, form.getUsername(), form.getEmail());
        }

        processarColaboradorNoCadastro(usuarioCriado, form);
        return usuarioCriado;
    }

    @Transactional(readOnly = true)
    public ColaboradorCpfLookup consultarColaboradorPorCpf(String cpfInformado) {
        String cpf = normalizeCpf(cpfInformado);
        if (isBlank(cpf)) {
            return ColaboradorCpfLookup.naoEncontrado();
        }

        Optional<Colaborador> colaboradorOpt = colaboradorRepository.findByCpfIgnoreCase(cpf);
        if (colaboradorOpt.isEmpty()) {
            return ColaboradorCpfLookup.naoEncontrado();
        }

        Colaborador colaborador = colaboradorOpt.get();
        Optional<UsuarioColaborador> usuarioColaboradorOpt =
                usuarioColaboradorRepository.findByColaboradorIdComUsuario(colaborador.getId());
        if (usuarioColaboradorOpt.isEmpty()) {
            return ColaboradorCpfLookup.encontradoSemVinculo(colaborador);
        }

        UsuarioColaborador usuarioColaborador = usuarioColaboradorOpt.get();
        Usuario usuario = usuarioColaborador.getUsuario();
        return ColaboradorCpfLookup.encontradoComVinculo(
                colaborador,
                usuarioColaborador.isAtivo(),
                usuario.getId(),
                usuario.getUsername());
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
    public void alterarStatus(Long usuarioId, boolean ativo, String keycloakIdUsuarioLogado) {
        Usuario usuario = buscarPorId(usuarioId);
        validarNaoAutoGestao(usuario, keycloakIdUsuarioLogado);
        if (usuario.isAtivo() == ativo) {
            return;
        }
        if (!ativo) {
            validarUltimoAdministradorAtivo(usuario.getId());
        }

        usuario.setAtivo(ativo);
        salvar(usuario);
        if (!isPendingKeycloakId(usuario.getKeycloakId())) {
            keycloakAdminClient.atualizarStatusUsuario(
                    usuario.getKeycloakId(),
                    usuario.getUsername(),
                    usuario.getEmail(),
                    ativo);
        }
    }

    @Transactional
    public void excluirDefinitivamente(Long usuarioId, String keycloakIdUsuarioLogado) {
        Usuario usuario = buscarPorId(usuarioId);
        validarNaoAutoGestao(usuario, keycloakIdUsuarioLogado);
        validarUltimoAdministradorAtivo(usuario.getId());

        usuarioUnidadePerfilRepository.deleteByUsuarioId(usuario.getId());
        usuarioColaboradorRepository.deleteByUsuarioId(usuario.getId());
        try {
            usuarioRepository.delete(usuario);
            usuarioRepository.flush();
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException(
                    "Nao foi possivel excluir definitivamente: usuario possui historico em registros operacionais/auditoria.");
        }

        keycloakAdminClient.excluirUsuario(usuario.getKeycloakId(), usuario.getUsername());
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

    private void validarNaoAutoGestao(Usuario usuarioAlvo, String keycloakIdUsuarioLogado) {
        if (isBlank(keycloakIdUsuarioLogado)) {
            return;
        }
        if (keycloakIdUsuarioLogado.equalsIgnoreCase(usuarioAlvo.getKeycloakId())) {
            throw new IllegalArgumentException("Nao e permitido executar esta acao no proprio usuario logado.");
        }
    }

    private void validarUltimoAdministradorAtivo(Long usuarioIdAlvo) {
        boolean alvoEhAdminAtivo = colaboradorUnidadeAtuacaoRepository.usuarioPossuiAlgumaPermissaoAtiva(
                usuarioIdAlvo, AdminAuthorizationService.ADMIN_PERMISSIONS);
        if (!alvoEhAdminAtivo) {
            return;
        }
        long totalAdminsAtivos = colaboradorUnidadeAtuacaoRepository.contarUsuariosAtivosComAlgumaPermissao(
                AdminAuthorizationService.ADMIN_PERMISSIONS);
        if (totalAdminsAtivos <= 1) {
            throw new IllegalArgumentException("Nao e permitido remover/desativar o ultimo usuario administrador ativo.");
        }
    }

    private void processarColaboradorNoCadastro(Usuario usuario, UsuarioNovoForm form) {
        String cpf = normalizeCpf(form.getColaboradorCpf());
        String nome = normalizeUpper(form.getColaboradorNome());
        boolean informouDadosColaborador =
                !isBlank(cpf) || !isBlank(nome) || form.getColaboradorCargoId() != null || form.isAssociarColaboradorExistente();
        if (!informouDadosColaborador) {
            return;
        }
        if (isBlank(cpf)) {
            throw new IllegalArgumentException("Informe o CPF do colaborador para vincular ou criar.");
        }

        Optional<Colaborador> colaboradorExistenteOpt = colaboradorRepository.findByCpfIgnoreCase(cpf);
        if (colaboradorExistenteOpt.isPresent()) {
            Colaborador colaboradorExistente = colaboradorExistenteOpt.get();
            if (!colaboradorExistente.isAtivo()) {
                throw new IllegalArgumentException("O colaborador informado esta inativo e nao pode ser vinculado.");
            }
            Optional<UsuarioColaborador> vinculoExistenteOpt =
                    usuarioColaboradorRepository.findByColaboradorIdComUsuario(colaboradorExistente.getId());
            if (vinculoExistenteOpt.isPresent()
                    && !vinculoExistenteOpt.get().getUsuario().getId().equals(usuario.getId())) {
                String usernameVinculado = vinculoExistenteOpt.get().getUsuario().getUsername();
                throw new IllegalArgumentException(
                        "CPF informado ja vinculado ao usuario "
                                + (isBlank(usernameVinculado) ? "#" + vinculoExistenteOpt.get().getUsuario().getId() : usernameVinculado)
                                + ".");
            }
            if (!form.isAssociarColaboradorExistente()) {
                throw new IllegalArgumentException(
                        "Ja existe colaborador com este CPF. Marque a opcao para associar colaborador existente.");
            }
            atualizarVinculoColaborador(usuario.getId(), colaboradorExistente.getId());
            return;
        }

        if (form.isAssociarColaboradorExistente()) {
            throw new IllegalArgumentException("Nao existe colaborador com este CPF para associar.");
        }
        if (isBlank(nome)) {
            throw new IllegalArgumentException("Informe o nome do colaborador para criar um novo cadastro.");
        }

        Colaborador novoColaborador = new Colaborador();
        novoColaborador.setNome(nome);
        novoColaborador.setCpf(cpf);
        novoColaborador.setCargoColaborador(resolveCargo(form.getColaboradorCargoId()));
        novoColaborador.setAtivo(true);
        Colaborador colaboradorSalvo = colaboradorRepository.save(novoColaborador);
        atualizarVinculoColaborador(usuario.getId(), colaboradorSalvo.getId());
    }

    private CargoColaborador resolveCargo(Long cargoColaboradorId) {
        if (cargoColaboradorId == null) {
            return null;
        }
        return cargoColaboradorRepository.findById(cargoColaboradorId)
                .orElseThrow(() -> new IllegalArgumentException("Cargo de colaborador nao encontrado"));
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

    private static String normalizeUpper(String value) {
        String normalized = normalize(value);
        return normalized == null ? null : normalized.toUpperCase();
    }

    private static String normalizeCpf(String cpf) {
        String normalized = normalize(cpf);
        if (normalized == null) {
            return null;
        }
        String digits = normalized.replaceAll("\\D", "");
        return digits.isBlank() ? null : digits;
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private static boolean isPendingKeycloakId(String keycloakId) {
        return keycloakId != null && keycloakId.startsWith("pending:");
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

    public record ColaboradorCpfLookup(boolean encontrado,
                                       Long colaboradorId,
                                       String colaboradorNome,
                                       String colaboradorCpf,
                                       Long colaboradorCargoId,
                                       String colaboradorCargoDescricao,
                                       boolean colaboradorAtivo,
                                       boolean vinculadoUsuario,
                                       boolean vinculoAtivo,
                                       Long usuarioId,
                                       String usuarioUsername) {

        public static ColaboradorCpfLookup naoEncontrado() {
            return new ColaboradorCpfLookup(false, null, null, null, null, null, false, false, false, null, null);
        }

        public static ColaboradorCpfLookup encontradoSemVinculo(Colaborador colaborador) {
            return new ColaboradorCpfLookup(
                    true,
                    colaborador.getId(),
                    colaborador.getNome(),
                    colaborador.getCpf(),
                    colaborador.getCargoColaborador() == null ? null : colaborador.getCargoColaborador().getId(),
                    colaborador.getCargoColaborador() == null ? null : colaborador.getCargoColaborador().getDescricao(),
                    colaborador.isAtivo(),
                    false,
                    false,
                    null,
                    null);
        }

        public static ColaboradorCpfLookup encontradoComVinculo(Colaborador colaborador,
                                                                 boolean vinculoAtivo,
                                                                 Long usuarioId,
                                                                 String usuarioUsername) {
            return new ColaboradorCpfLookup(
                    true,
                    colaborador.getId(),
                    colaborador.getNome(),
                    colaborador.getCpf(),
                    colaborador.getCargoColaborador() == null ? null : colaborador.getCargoColaborador().getId(),
                    colaborador.getCargoColaborador() == null ? null : colaborador.getCargoColaborador().getDescricao(),
                    colaborador.isAtivo(),
                    true,
                    vinculoAtivo,
                    usuarioId,
                    usuarioUsername);
        }
    }
}
