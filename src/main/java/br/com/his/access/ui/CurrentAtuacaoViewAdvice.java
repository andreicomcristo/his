package br.com.his.access.ui;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import br.com.his.access.context.UnidadeContext;
import br.com.his.access.service.AccessContextService;
import br.com.his.access.service.ColaboradorAtuacaoService;
import br.com.his.access.service.UserIdentity;

@ControllerAdvice
public class CurrentAtuacaoViewAdvice {

    private final AccessContextService accessContextService;
    private final ColaboradorAtuacaoService colaboradorAtuacaoService;
    private final UnidadeContext unidadeContext;

    public CurrentAtuacaoViewAdvice(AccessContextService accessContextService,
                                    ColaboradorAtuacaoService colaboradorAtuacaoService,
                                    UnidadeContext unidadeContext) {
        this.accessContextService = accessContextService;
        this.colaboradorAtuacaoService = colaboradorAtuacaoService;
        this.unidadeContext = unidadeContext;
    }

    @ModelAttribute("currentAtuacao")
    public ColaboradorAtuacaoService.AtuacaoResumo currentAtuacao(Authentication authentication) {
        Optional<UserIdentity> userOpt = accessContextService.resolveAuthenticatedUser(authentication);
        if (userOpt.isEmpty()) {
            return null;
        }

        Long unidadeId = unidadeContext.getUnidadeAtual().orElse(null);
        Long colaboradorUnidadeAtuacaoId = unidadeContext.getAtuacaoAtual().orElse(null);
        if (unidadeId == null || colaboradorUnidadeAtuacaoId == null) {
            return null;
        }

        return colaboradorAtuacaoService.buscarAtuacaoAtivaDoUsuarioNaUnidade(
                        userOpt.get().keycloakId(), unidadeId, colaboradorUnidadeAtuacaoId)
                .orElse(null);
    }
}
