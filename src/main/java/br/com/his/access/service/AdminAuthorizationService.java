package br.com.his.access.service;

import java.util.Set;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import br.com.his.access.context.UnidadeContext;
import br.com.his.access.repository.ColaboradorUnidadeAtuacaoRepository;

@Service
public class AdminAuthorizationService {

    public static final Set<String> ADMIN_PERMISSIONS = Set.of("ADMIN_USUARIOS", "ADMIN_SISTEMA");

    private final AccessContextService accessContextService;
    private final UnidadeContext unidadeContext;
    private final ColaboradorUnidadeAtuacaoRepository colaboradorUnidadeAtuacaoRepository;

    public AdminAuthorizationService(AccessContextService accessContextService,
                                     UnidadeContext unidadeContext,
                                     ColaboradorUnidadeAtuacaoRepository colaboradorUnidadeAtuacaoRepository) {
        this.accessContextService = accessContextService;
        this.unidadeContext = unidadeContext;
        this.colaboradorUnidadeAtuacaoRepository = colaboradorUnidadeAtuacaoRepository;
    }

    public boolean hasAdminPermission(Authentication authentication) {
        return accessContextService.resolveAuthenticatedUser(authentication)
                .flatMap(identity -> unidadeContext.getUnidadeAtual()
                        .flatMap(unidadeId -> unidadeContext.getAtuacaoAtual()
                                .map(colaboradorUnidadeAtuacaoId ->
                                        colaboradorUnidadeAtuacaoRepository.hasAnyPermissionAtAtuacao(
                                                identity.keycloakId(),
                                                unidadeId,
                                                colaboradorUnidadeAtuacaoId,
                                                ADMIN_PERMISSIONS))))
                .orElse(false);
    }
}
