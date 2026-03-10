package br.com.his.admin.ui;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.his.admin.dto.UnidadeFluxoForm;
import br.com.his.admin.service.UnidadeAdminService;
import br.com.his.admin.service.UnidadeFluxoAdminService;
import br.com.his.care.attendance.model.PrimeiroPassoFluxo;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/ui/admin/unidades/{unidadeId}/fluxo")
public class UnidadeFluxoAdminController {

    private final UnidadeAdminService unidadeAdminService;
    private final UnidadeFluxoAdminService unidadeFluxoAdminService;

    public UnidadeFluxoAdminController(UnidadeAdminService unidadeAdminService,
                                       UnidadeFluxoAdminService unidadeFluxoAdminService) {
        this.unidadeAdminService = unidadeAdminService;
        this.unidadeFluxoAdminService = unidadeFluxoAdminService;
    }

    @GetMapping
    public String editar(@PathVariable Long unidadeId, Model model) {
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", unidadeFluxoAdminService.carregarFormulario(unidadeId));
        }
        model.addAttribute("unidade", unidadeAdminService.buscarPorId(unidadeId));
        model.addAttribute("primeirosPassos", PrimeiroPassoFluxo.values());
        return "pages/admin/unidades/fluxo";
    }

    @PostMapping
    public String salvar(@PathVariable Long unidadeId,
                         @Valid @ModelAttribute("form") UnidadeFluxoForm form,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("unidade", unidadeAdminService.buscarPorId(unidadeId));
            model.addAttribute("primeirosPassos", PrimeiroPassoFluxo.values());
            return "pages/admin/unidades/fluxo";
        }
        try {
            unidadeFluxoAdminService.salvar(unidadeId, form);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Configuracao de fluxo atualizada para a unidade.");
            return "redirect:/ui/admin/unidades";
        } catch (IllegalArgumentException ex) {
            bindingResult.reject("fluxo", ex.getMessage());
            model.addAttribute("unidade", unidadeAdminService.buscarPorId(unidadeId));
            model.addAttribute("primeirosPassos", PrimeiroPassoFluxo.values());
            return "pages/admin/unidades/fluxo";
        }
    }
}

