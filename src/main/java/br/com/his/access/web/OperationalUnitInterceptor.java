package br.com.his.access.web;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import br.com.his.access.context.UnidadeContext;
import br.com.his.access.service.AccessContextService;
import br.com.his.access.service.ColaboradorAtuacaoService;
import br.com.his.access.service.UserIdentity;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OperationalUnitInterceptor implements HandlerInterceptor {

    private final AccessContextService accessContextService;
    private final ColaboradorAtuacaoService colaboradorAtuacaoService;
    private final UnidadeContext unidadeContext;

    public OperationalUnitInterceptor(AccessContextService accessContextService,
                                      ColaboradorAtuacaoService colaboradorAtuacaoService,
                                      UnidadeContext unidadeContext) {
        this.accessContextService = accessContextService;
        this.colaboradorAtuacaoService = colaboradorAtuacaoService;
        this.unidadeContext = unidadeContext;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<UserIdentity> userOpt = accessContextService.resolveAuthenticatedUser(authentication);
        if (userOpt.isEmpty()) {
            return true;
        }

        String keycloakId = userOpt.get().keycloakId();
        Long unidadeId = unidadeContext.getUnidadeAtual().orElse(null);
        Long atuacaoId = unidadeContext.getAtuacaoAtual().orElse(null);
        if (unidadeId != null
                && atuacaoId != null
                && colaboradorAtuacaoService.usuarioPossuiAtuacaoAtiva(keycloakId, unidadeId, atuacaoId)) {
            return true;
        }
        if (unidadeId != null || atuacaoId != null) {
            unidadeContext.clear();
        }

        List<ColaboradorAtuacaoService.AtuacaoResumo> contextos =
                colaboradorAtuacaoService.listarContextosAtivosDoUsuario(keycloakId);
        if (contextos.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            request.getRequestDispatcher("/ui/sem-atuacao").forward(request, response);
            return false;
        }

        if (contextos.size() == 1) {
            ColaboradorAtuacaoService.AtuacaoResumo unico = contextos.getFirst();
            unidadeContext.setUnidadeAtual(unico.unidadeId());
            unidadeContext.setAtuacaoAtual(unico.id());
            return true;
        }

        response.sendRedirect(request.getContextPath() + "/ui/escolher-unidade");
        return false;
    }
}
