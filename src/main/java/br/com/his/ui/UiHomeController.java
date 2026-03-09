package br.com.his.ui;

import java.time.LocalDate;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import br.com.his.access.context.UnidadeContext;
import br.com.his.ui.service.HomeDashboardService;

@Controller
public class UiHomeController {

    private final UnidadeContext unidadeContext;
    private final HomeDashboardService homeDashboardService;

    public UiHomeController(UnidadeContext unidadeContext,
                            HomeDashboardService homeDashboardService) {
        this.unidadeContext = unidadeContext;
        this.homeDashboardService = homeDashboardService;
    }

    @GetMapping("/")
    public String root(Authentication authentication) {
        if (authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:/ui/home";
        }
        return "redirect:/login";
    }

    @GetMapping("/ui/home")
    public String home(Model model) {
        unidadeContext.getUnidadeAtual().ifPresentOrElse(
                unidadeId -> model.addAttribute("dashboard", homeDashboardService.carregar(unidadeId, LocalDate.now())),
                () -> model.addAttribute("errorMessage", "Selecione uma unidade para visualizar o painel"));
        return "pages/home";
    }
}
