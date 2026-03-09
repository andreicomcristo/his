package br.com.his.access.ui;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.his.access.context.UnidadeContext;
import br.com.his.access.model.Unidade;
import br.com.his.access.service.AccessContextService;
import br.com.his.access.service.UserIdentity;

@Controller
@RequestMapping("/ui")
public class UnidadeSelectionController {

    private final AccessContextService accessContextService;
    private final UnidadeContext unidadeContext;

    public UnidadeSelectionController(AccessContextService accessContextService,
                                      UnidadeContext unidadeContext) {
        this.accessContextService = accessContextService;
        this.unidadeContext = unidadeContext;
    }

    @GetMapping("/escolher-unidade")
    public String escolherUnidade(Authentication authentication, Model model) {
        Optional<UserIdentity> userOpt = accessContextService.resolveAuthenticatedUser(authentication);
        if (userOpt.isEmpty()) {
            return "redirect:/login";
        }

        List<Unidade> unidades = accessContextService.listUnidadesAtivasDoUsuario(userOpt.get().keycloakId());
        if (unidades.isEmpty()) {
            model.addAttribute("mensagem", "Usuario sem unidade vinculada ativa.");
            return "pages/error/sem-unidade";
        }

        model.addAttribute("unidades", unidades);
        model.addAttribute("unidadeAtualId", unidadeContext.getUnidadeAtual().orElse(null));
        return "pages/unidade/escolher";
    }

    @PostMapping("/escolher-unidade")
    public String confirmarUnidade(@RequestParam Long unidadeId,
                                   Authentication authentication,
                                   RedirectAttributes redirectAttributes) {
        Optional<UserIdentity> userOpt = accessContextService.resolveAuthenticatedUser(authentication);
        if (userOpt.isEmpty()) {
            return "redirect:/login";
        }

        boolean podeUsar = accessContextService.usuarioPossuiVinculoAtivo(userOpt.get().keycloakId(), unidadeId);
        if (!podeUsar) {
            redirectAttributes.addFlashAttribute("errorMessage", "Unidade invalida para o usuario logado.");
            return "redirect:/ui/escolher-unidade";
        }

        unidadeContext.setUnidadeAtual(unidadeId);
        return "redirect:/ui/home";
    }

    @GetMapping("/sem-unidade")
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String semUnidade(Model model) {
        if (!model.containsAttribute("mensagem")) {
            model.addAttribute("mensagem", "Usuario sem unidade vinculada.");
        }
        return "pages/error/sem-unidade";
    }
}
