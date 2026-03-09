package br.com.his.access.web;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import br.com.his.access.context.UnidadeContext;
import br.com.his.access.model.Unidade;
import br.com.his.access.service.AccessContextService;
import br.com.his.access.service.UserIdentity;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OperationalUnitInterceptor implements HandlerInterceptor {

    private final AccessContextService accessContextService;
    private final UnidadeContext unidadeContext;

    public OperationalUnitInterceptor(AccessContextService accessContextService,
                                      UnidadeContext unidadeContext) {
        this.accessContextService = accessContextService;
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

        if (unidadeContext.getUnidadeAtual().isPresent()) {
            return true;
        }

        List<Unidade> unidades = accessContextService.listUnidadesAtivasDoUsuario(userOpt.get().keycloakId());

        if (unidades.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            request.getRequestDispatcher("/ui/sem-unidade").forward(request, response);
            return false;
        }

        if (unidades.size() == 1) {
            unidadeContext.setUnidadeAtual(unidades.getFirst().getId());
            return true;
        }

        response.sendRedirect(request.getContextPath() + "/ui/escolher-unidade");
        return false;
    }
}
