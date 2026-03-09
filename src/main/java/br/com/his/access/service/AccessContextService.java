package br.com.his.access.service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.his.access.model.Unidade;
import br.com.his.access.model.Usuario;
import br.com.his.access.repository.UsuarioRepository;
import br.com.his.access.repository.UsuarioUnidadePerfilRepository;

@Service
public class AccessContextService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioUnidadePerfilRepository usuarioUnidadePerfilRepository;

    public AccessContextService(UsuarioRepository usuarioRepository,
                                UsuarioUnidadePerfilRepository usuarioUnidadePerfilRepository) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioUnidadePerfilRepository = usuarioUnidadePerfilRepository;
    }

    @Transactional
    public Optional<UserIdentity> resolveAuthenticatedUser(Authentication authentication) {
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return Optional.empty();
        }

        UserIdentity identity = extractIdentity(authentication);
        if (identity == null || isBlank(identity.keycloakId())) {
            return Optional.empty();
        }

        ensureUsuario(identity);
        return Optional.of(identity);
    }

    @Transactional(readOnly = true)
    public List<Unidade> listUnidadesAtivasDoUsuario(String keycloakId) {
        if (isBlank(keycloakId)) {
            return List.of();
        }
        return usuarioUnidadePerfilRepository.findUnidadesAtivasByKeycloakId(keycloakId);
    }

    @Transactional(readOnly = true)
    public boolean usuarioPossuiVinculoAtivo(String keycloakId, Long unidadeId) {
        if (isBlank(keycloakId) || unidadeId == null) {
            return false;
        }
        return usuarioUnidadePerfilRepository.existsVinculoAtivo(keycloakId, unidadeId);
    }

    private void ensureUsuario(UserIdentity identity) {
        Optional<Usuario> existingOpt = usuarioRepository.findByKeycloakId(identity.keycloakId());
        if (existingOpt.isEmpty()) {
            Optional<Usuario> byUsername = usuarioRepository.findByUsername(safeUsername(identity));
            if (byUsername.isPresent()) {
                Usuario existingByUsername = byUsername.get();
                existingByUsername.setKeycloakId(identity.keycloakId());
                if (isBlank(existingByUsername.getEmail()) && !isBlank(identity.email())) {
                    existingByUsername.setEmail(identity.email());
                }
                existingByUsername.setAtivo(true);
                usuarioRepository.save(existingByUsername);
                return;
            }

            Usuario novo = new Usuario();
            novo.setKeycloakId(identity.keycloakId());
            novo.setUsername(safeUsername(identity));
            novo.setEmail(identity.email());
            novo.setAtivo(true);
            try {
                usuarioRepository.save(novo);
            } catch (DataIntegrityViolationException ex) {
                Optional<Usuario> retryByUsername = usuarioRepository.findByUsername(safeUsername(identity));
                if (retryByUsername.isPresent()) {
                    Usuario existingByUsername = retryByUsername.get();
                    existingByUsername.setKeycloakId(identity.keycloakId());
                    if (isBlank(existingByUsername.getEmail()) && !isBlank(identity.email())) {
                        existingByUsername.setEmail(identity.email());
                    }
                    existingByUsername.setAtivo(true);
                    usuarioRepository.save(existingByUsername);
                    return;
                }
                throw ex;
            }
            return;
        }

        Usuario existing = existingOpt.get();
        boolean changed = false;

        if (isBlank(existing.getUsername()) && !isBlank(identity.username())) {
            existing.setUsername(identity.username());
            changed = true;
        }
        if (isBlank(existing.getEmail()) && !isBlank(identity.email())) {
            existing.setEmail(identity.email());
            changed = true;
        }
        if (!existing.isAtivo()) {
            existing.setAtivo(true);
            changed = true;
        }

        if (changed) {
            usuarioRepository.save(existing);
        }
    }

    private UserIdentity extractIdentity(Authentication authentication) {
        Object principal = authentication.getPrincipal();

        if (principal instanceof OidcUser oidcUser) {
            return new UserIdentity(
                    oidcUser.getSubject(),
                    firstNonBlank(oidcUser.getPreferredUsername(), oidcUser.getName(), authentication.getName()),
                    oidcUser.getEmail());
        }

        if (principal instanceof OAuth2User oauth2User) {
            Map<String, Object> attrs = oauth2User.getAttributes();
            return new UserIdentity(
                    str(attrs.get("sub")),
                    firstNonBlank(str(attrs.get("preferred_username")), str(attrs.get("name")), authentication.getName()),
                    str(attrs.get("email")));
        }

        return new UserIdentity(authentication.getName(), authentication.getName(), null);
    }

    private String safeUsername(UserIdentity identity) {
        if (!isBlank(identity.username())) {
            return identity.username();
        }
        return "user-" + identity.keycloakId();
    }

    private static String str(Object value) {
        return value == null ? null : Objects.toString(value);
    }

    private static String firstNonBlank(String... values) {
        for (String value : values) {
            if (!isBlank(value)) {
                return value;
            }
        }
        return null;
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
