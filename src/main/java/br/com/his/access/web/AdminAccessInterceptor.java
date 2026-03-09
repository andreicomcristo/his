package br.com.his.access.web;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import br.com.his.access.context.UnidadeContext;
import br.com.his.access.model.Unidade;
import br.com.his.access.service.AccessContextService;
import br.com.his.access.service.AdminAuthorizationService;
import br.com.his.access.service.UserIdentity;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AdminAccessInterceptor implements HandlerInterceptor {

    private final AccessContextService accessContextService;
    private final UnidadeContext unidadeContext;
    private final AdminAuthorizationService adminAuthorizationService;

    public AdminAccessInterceptor(AccessContextService accessContextService,
                                  UnidadeContext unidadeContext,
                                  AdminAuthorizationService adminAuthorizationService) {
        this.accessContextService = accessContextService;
        this.unidadeContext = unidadeContext;
        this.adminAuthorizationService = adminAuthorizationService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<UserIdentity> userOpt = accessContextService.resolveAuthenticatedUser(authentication);
        if (userOpt.isEmpty()) {
            return true;
        }

        if (unidadeContext.getUnidadeAtual().isEmpty()) {
            List<Unidade> unidades = accessContextService.listUnidadesAtivasDoUsuario(userOpt.get().keycloakId());
            if (unidades.isEmpty()) {
                return handleNoUnit(request, response);
            }
            if (unidades.size() == 1) {
                unidadeContext.setUnidadeAtual(unidades.getFirst().getId());
            } else {
                return redirectToChooseUnit(request, response);
            }
        }

        if (!adminAuthorizationService.hasAdminPermission(authentication)) {
            return handleForbidden(request, response);
        }

        return true;
    }

    private boolean handleNoUnit(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (isApi(request)) {
            writeApiForbidden(response, "Usuario sem unidade vinculada");
            return false;
        }
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        request.getRequestDispatcher("/ui/sem-unidade").forward(request, response);
        return false;
    }

    private boolean redirectToChooseUnit(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (isApi(request)) {
            writeApiForbidden(response, "Selecione a unidade atual antes de acessar administracao");
            return false;
        }
        response.sendRedirect(request.getContextPath() + "/ui/escolher-unidade");
        return false;
    }

    private boolean handleForbidden(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (isApi(request)) {
            writeApiForbidden(response, "Permissao insuficiente para administrar nesta unidade");
            return false;
        }
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        request.getRequestDispatcher("/ui/acesso-negado").forward(request, response);
        return false;
    }

    private static boolean isApi(HttpServletRequest request) {
        return request.getRequestURI().startsWith(request.getContextPath() + "/api/");
    }

    private void writeApiForbidden(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write("""
                {"status":403,"error":"Forbidden","message":"%s"}
                """.formatted(escapeJson(message)));
    }

    private static String escapeJson(String text) {
        return text.replace("\"", "\\\"");
    }
}
