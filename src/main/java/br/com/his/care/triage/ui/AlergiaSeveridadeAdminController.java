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

import br.com.his.care.triage.dto.AlergiaSeveridadeForm;
import br.com.his.care.triage.service.AlergiaSeveridadeAdminService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/ui/admin/alergias-severidades")
public class AlergiaSeveridadeAdminController {

    private final AlergiaSeveridadeAdminService service;

    public AlergiaSeveridadeAdminController(AlergiaSeveridadeAdminService service) {
        this.service = service;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) String q, Model model) {
        model.addAttribute("items", service.listar(q));
        model.addAttribute("q", q);
        return "pages/admin/alergias-severidades/list";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new AlergiaSeveridadeForm());
        }
        model.addAttribute("modoEdicao", false);
        return "pages/admin/alergias-severidades/form";
    }

    @PostMapping
    public String criar(@Valid @ModelAttribute("form") AlergiaSeveridadeForm form,
                        BindingResult bindingResult,
                        Model model,
                        RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("modoEdicao", false);
            return "pages/admin/alergias-severidades/form";
        }
        service.criar(form);
        redirectAttributes.addFlashAttribute("successMessage", "Severidade de alergia cadastrada com sucesso");
        return "redirect:/ui/admin/alergias-severidades";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("form", service.toForm(service.buscar(id)));
        model.addAttribute("modoEdicao", true);
        model.addAttribute("itemId", id);
        return "pages/admin/alergias-severidades/form";
    }

    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id,
                            @Valid @ModelAttribute("form") AlergiaSeveridadeForm form,
                            BindingResult bindingResult,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("modoEdicao", true);
            model.addAttribute("itemId", id);
            return "pages/admin/alergias-severidades/form";
        }
        service.atualizar(id, form);
        redirectAttributes.addFlashAttribute("successMessage", "Severidade de alergia atualizada com sucesso");
        return "redirect:/ui/admin/alergias-severidades";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        service.excluir(id);
        redirectAttributes.addFlashAttribute("successMessage", "Severidade de alergia excluida com sucesso");
        return "redirect:/ui/admin/alergias-severidades";
    }
}
