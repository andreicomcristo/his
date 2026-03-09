package br.com.his.access.service;

import java.util.Set;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import br.com.his.access.context.UnidadeContext;
import br.com.his.access.repository.UsuarioUnidadePerfilRepository;

@Service
public class AdminAuthorizationService {

    public static final Set<String> ADMIN_PERMISSIONS = Set.of("ADMIN_USUARIOS", "ADMIN_SISTEMA");

    private final AccessContextService accessContextService;
    private final UnidadeContext unidadeContext;
    private final UsuarioUnidadePerfilRepository usuarioUnidadePerfilRepository;

    public AdminAuthorizationService(AccessContextService accessContextService,
                                     UnidadeContext unidadeContext,
                                     UsuarioUnidadePerfilRepository usuarioUnidadePerfilRepository) {
        this.accessContextService = accessContextService;
        this.unidadeContext = unidadeContext;
        this.usuarioUnidadePerfilRepository = usuarioUnidadePerfilRepository;
    }

    public boolean hasAdminPermission(Authentication authentication) {
        return accessContextService.resolveAuthenticatedUser(authentication)
                .flatMap(identity -> unidadeContext.getUnidadeAtual().map(unidadeId ->
                        usuarioUnidadePerfilRepository.hasAnyPermissionAtUnidade(
                                identity.keycloakId(), unidadeId, ADMIN_PERMISSIONS)))
                .orElse(false);
    }
}
