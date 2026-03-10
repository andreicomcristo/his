package br.com.his.care.triage.ui;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.his.care.triage.dto.ReguaDorForm;
import br.com.his.care.triage.service.ReguaDorAdminService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/ui/admin/reguas-dor")
public class ReguaDorAdminController {

    private final ReguaDorAdminService service;

    public ReguaDorAdminController(ReguaDorAdminService service) {
        this.service = service;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) String q, Model model) {
        model.addAttribute("items", service.listar(q));
        model.addAttribute("q", q);
        return "pages/admin/reguas-dor/list";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new ReguaDorForm());
        }
        model.addAttribute("modoEdicao", false);
        return "pages/admin/reguas-dor/form";
    }

    @PostMapping
    public String criar(@Valid @ModelAttribute("form") ReguaDorForm form,
                        BindingResult bindingResult,
                        Model model,
                        RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("modoEdicao", false);
            return "pages/admin/reguas-dor/form";
        }
        service.criar(form);
        redirectAttributes.addFlashAttribute("successMessage", "Item da escala de dor cadastrado com sucesso");
        return "redirect:/ui/admin/reguas-dor";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("form", service.toForm(service.buscar(id)));
        model.addAttribute("modoEdicao", true);
        model.addAttribute("itemId", id);
        return "pages/admin/reguas-dor/form";
    }

    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id,
                            @Valid @ModelAttribute("form") ReguaDorForm form,
                            BindingResult bindingResult,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("modoEdicao", true);
            model.addAttribute("itemId", id);
            return "pages/admin/reguas-dor/form";
        }
        service.atualizar(id, form);
        redirectAttributes.addFlashAttribute("successMessage", "Item da escala de dor atualizado com sucesso");
        return "redirect:/ui/admin/reguas-dor";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        service.excluir(id);
        redirectAttributes.addFlashAttribute("successMessage", "Item da escala de dor excluido com sucesso");
        return "redirect:/ui/admin/reguas-dor";
    }
}
