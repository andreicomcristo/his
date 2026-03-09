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

import br.com.his.admin.dto.SituacaoOcupacionalForm;
import br.com.his.admin.service.SituacaoOcupacionalAdminService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/ui/admin/situacoes-ocupacionais")
public class SituacaoOcupacionalAdminController {

    private final SituacaoOcupacionalAdminService service;

    public SituacaoOcupacionalAdminController(SituacaoOcupacionalAdminService service) {
        this.service = service;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("items", service.listarTodas());
        return "pages/admin/situacoes-ocupacionais/list";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new SituacaoOcupacionalForm());
        }
        model.addAttribute("modoEdicao", false);
        return "pages/admin/situacoes-ocupacionais/form";
    }

    @PostMapping
    public String criar(@Valid @ModelAttribute("form") SituacaoOcupacionalForm form,
                        BindingResult bindingResult,
                        Model model,
                        RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("modoEdicao", false);
            return "pages/admin/situacoes-ocupacionais/form";
        }
        service.criar(form);
        redirectAttributes.addFlashAttribute("successMessage", "Situacao ocupacional criada com sucesso");
        return "redirect:/ui/admin/situacoes-ocupacionais";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("form", service.toForm(service.buscarPorId(id)));
        model.addAttribute("itemId", id);
        model.addAttribute("modoEdicao", true);
        return "pages/admin/situacoes-ocupacionais/form";
    }

    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id,
                            @Valid @ModelAttribute("form") SituacaoOcupacionalForm form,
                            BindingResult bindingResult,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("itemId", id);
            model.addAttribute("modoEdicao", true);
            return "pages/admin/situacoes-ocupacionais/form";
        }
        service.atualizar(id, form);
        redirectAttributes.addFlashAttribute("successMessage", "Situacao ocupacional atualizada com sucesso");
        return "redirect:/ui/admin/situacoes-ocupacionais";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        service.excluir(id);
        redirectAttributes.addFlashAttribute("successMessage", "Situacao ocupacional excluida com sucesso");
        return "redirect:/ui/admin/situacoes-ocupacionais";
    }
}
