package br.com.his.admin.service;

import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.his.access.model.Perfil;
import br.com.his.access.model.Unidade;
import br.com.his.access.model.Usuario;
import br.com.his.access.model.UsuarioUnidadePerfil;
import br.com.his.access.repository.PerfilRepository;
import br.com.his.access.repository.UnidadeRepository;
import br.com.his.access.repository.UsuarioRepository;
import br.com.his.access.repository.UsuarioUnidadePerfilRepository;
import br.com.his.admin.dto.UsuarioNovoForm;
import br.com.his.admin.dto.UsuarioVinculoForm;
import br.com.his.admin.keycloak.KeycloakAdminClient;

@Service
public class UsuarioAdminService {

    private final UsuarioRepository usuarioRepository;
    private final UnidadeRepository unidadeRepository;
    private final PerfilRepository perfilRepository;
    private final UsuarioUnidadePerfilRepository usuarioUnidadePerfilRepository;
    private final KeycloakAdminClient keycloakAdminClient;

    public UsuarioAdminService(UsuarioRepository usuarioRepository,
                               UnidadeRepository unidadeRepository,
                               PerfilRepository perfilRepository,
                               UsuarioUnidadePerfilRepository usuarioUnidadePerfilRepository,
                               KeycloakAdminClient keycloakAdminClient) {
        this.usuarioRepository = usuarioRepository;
        this.unidadeRepository = unidadeRepository;
        this.perfilRepository = perfilRepository;
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
    public List<UsuarioUnidadePerfil> listarVinculos(Long usuarioId) {
        buscarPorId(usuarioId);
        return usuarioUnidadePerfilRepository.findByUsuarioIdComDetalhes(usuarioId);
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

    @Transactional
    public UsuarioUnidadePerfil adicionarVinculo(Long usuarioId, UsuarioVinculoForm form) {
        Usuario usuario = buscarPorId(usuarioId);
        Unidade unidade = unidadeRepository.findById(form.getUnidadeId())
                .orElseThrow(() -> new IllegalArgumentException("Unidade nao encontrada: " + form.getUnidadeId()));
        Perfil perfil = perfilRepository.findById(form.getPerfilId())
                .orElseThrow(() -> new IllegalArgumentException("Perfil nao encontrado: " + form.getPerfilId()));

        Optional<UsuarioUnidadePerfil> existing = usuarioUnidadePerfilRepository
                .findByUsuarioIdAndUnidadeIdAndPerfilId(usuarioId, unidade.getId(), perfil.getId());
        if (existing.isPresent()) {
            UsuarioUnidadePerfil vinculo = existing.get();
            vinculo.setAtivo(true);
            return usuarioUnidadePerfilRepository.save(vinculo);
        }

        UsuarioUnidadePerfil vinculo = new UsuarioUnidadePerfil();
        vinculo.setUsuario(usuario);
        vinculo.setUnidade(unidade);
        vinculo.setPerfil(perfil);
        vinculo.setAtivo(true);
        return salvar(vinculo);
    }

    @Transactional
    public UsuarioUnidadePerfil atualizarPerfilVinculo(Long vinculoId, Long perfilId) {
        UsuarioUnidadePerfil vinculo = usuarioUnidadePerfilRepository.findById(vinculoId)
                .orElseThrow(() -> new IllegalArgumentException("Vinculo nao encontrado: " + vinculoId));
        Perfil perfil = perfilRepository.findById(perfilId)
                .orElseThrow(() -> new IllegalArgumentException("Perfil nao encontrado: " + perfilId));

        boolean jaExiste = usuarioUnidadePerfilRepository.existsByTripla(
                vinculo.getUsuario().getId(), vinculo.getUnidade().getId(), perfilId, vinculo.getId());
        if (jaExiste) {
            throw new IllegalArgumentException("Ja existe vinculo para essa unidade e perfil");
        }

        vinculo.setPerfil(perfil);
        vinculo.setAtivo(true);
        return salvar(vinculo);
    }

    @Transactional
    public void removerVinculo(Long vinculoId) {
        UsuarioUnidadePerfil vinculo = usuarioUnidadePerfilRepository.findById(vinculoId)
                .orElseThrow(() -> new IllegalArgumentException("Vinculo nao encontrado: " + vinculoId));
        vinculo.setAtivo(false);
        usuarioUnidadePerfilRepository.save(vinculo);
    }

    private Usuario salvar(Usuario usuario) {
        try {
            return usuarioRepository.save(usuario);
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("Usuario ja cadastrado com username ou keycloak_id");
        }
    }

    private UsuarioUnidadePerfil salvar(UsuarioUnidadePerfil vinculo) {
        try {
            return usuarioUnidadePerfilRepository.save(vinculo);
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("Ja existe vinculo para esse usuario, unidade e perfil");
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
