package br.com.his.access.ui;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import br.com.his.access.service.AdminAuthorizationService;

@ControllerAdvice
public class CurrentAuthorizationViewAdvice {

    private final AdminAuthorizationService adminAuthorizationService;

    public CurrentAuthorizationViewAdvice(AdminAuthorizationService adminAuthorizationService) {
        this.adminAuthorizationService = adminAuthorizationService;
    }

    @ModelAttribute("canAdmin")
    public boolean canAdmin(Authentication authentication) {
        return adminAuthorizationService.hasAdminPermission(authentication);
    }
}
