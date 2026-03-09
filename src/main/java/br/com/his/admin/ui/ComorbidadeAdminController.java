package br.com.his.admin.ui;

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

import br.com.his.admin.dto.ComorbidadeForm;
import br.com.his.admin.service.ComorbidadeAdminService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/ui/admin/comorbidades")
public class ComorbidadeAdminController {

    private final ComorbidadeAdminService service;

    public ComorbidadeAdminController(ComorbidadeAdminService service) {
        this.service = service;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) String q, Model model) {
        model.addAttribute("items", service.listar(q));
        model.addAttribute("q", q);
        return "pages/admin/comorbidades/list";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new ComorbidadeForm());
        }
        model.addAttribute("modoEdicao", false);
        return "pages/admin/comorbidades/form";
    }

    @PostMapping
    public String criar(@Valid @ModelAttribute("form") ComorbidadeForm form,
                        BindingResult bindingResult,
                        Model model,
                        RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("modoEdicao", false);
            return "pages/admin/comorbidades/form";
        }
        service.criar(form);
        redirectAttributes.addFlashAttribute("successMessage", "Comorbidade cadastrada com sucesso");
        return "redirect:/ui/admin/comorbidades";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("form", service.toForm(service.buscar(id)));
        model.addAttribute("modoEdicao", true);
        model.addAttribute("itemId", id);
        return "pages/admin/comorbidades/form";
    }

    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id,
                            @Valid @ModelAttribute("form") ComorbidadeForm form,
                            BindingResult bindingResult,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("modoEdicao", true);
            model.addAttribute("itemId", id);
            return "pages/admin/comorbidades/form";
        }
        service.atualizar(id, form);
        redirectAttributes.addFlashAttribute("successMessage", "Comorbidade atualizada com sucesso");
        return "redirect:/ui/admin/comorbidades";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        service.excluir(id);
        redirectAttributes.addFlashAttribute("successMessage", "Comorbidade excluida com sucesso");
        return "redirect:/ui/admin/comorbidades";
    }
}
