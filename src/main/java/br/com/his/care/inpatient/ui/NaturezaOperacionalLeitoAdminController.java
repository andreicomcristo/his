package br.com.his.care.inpatient.ui;

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

import br.com.his.care.inpatient.dto.NaturezaOperacionalLeitoForm;
import br.com.his.care.inpatient.service.NaturezaOperacionalLeitoAdminService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/ui/admin/naturezas-operacionais-leito")
public class NaturezaOperacionalLeitoAdminController {

    private final NaturezaOperacionalLeitoAdminService service;

    public NaturezaOperacionalLeitoAdminController(NaturezaOperacionalLeitoAdminService service) {
        this.service = service;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) String q, Model model) {
        model.addAttribute("items", service.listar(q));
        model.addAttribute("q", q);
        return "pages/admin/naturezas-operacionais-leito/list";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new NaturezaOperacionalLeitoForm());
        }
        model.addAttribute("modoEdicao", false);
        return "pages/admin/naturezas-operacionais-leito/form";
    }

    @PostMapping
    public String criar(@Valid @ModelAttribute("form") NaturezaOperacionalLeitoForm form,
                        BindingResult bindingResult,
                        Model model,
                        RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("modoEdicao", false);
            return "pages/admin/naturezas-operacionais-leito/form";
        }
        try {
            service.criar(form);
            redirectAttributes.addFlashAttribute("successMessage", "Natureza operacional cadastrada com sucesso");
            return "redirect:/ui/admin/naturezas-operacionais-leito";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("modoEdicao", false);
            model.addAttribute("errorMessage", ex.getMessage());
            return "pages/admin/naturezas-operacionais-leito/form";
        }
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("form", service.toForm(service.buscar(id)));
        model.addAttribute("modoEdicao", true);
        model.addAttribute("itemId", id);
        return "pages/admin/naturezas-operacionais-leito/form";
    }

    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id,
                            @Valid @ModelAttribute("form") NaturezaOperacionalLeitoForm form,
                            BindingResult bindingResult,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("modoEdicao", true);
            model.addAttribute("itemId", id);
            return "pages/admin/naturezas-operacionais-leito/form";
        }
        try {
            service.atualizar(id, form);
            redirectAttributes.addFlashAttribute("successMessage", "Natureza operacional atualizada com sucesso");
            return "redirect:/ui/admin/naturezas-operacionais-leito";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("modoEdicao", true);
            model.addAttribute("itemId", id);
            model.addAttribute("errorMessage", ex.getMessage());
            return "pages/admin/naturezas-operacionais-leito/form";
        }
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id,
                          RedirectAttributes redirectAttributes) {
        try {
            service.excluir(id);
            redirectAttributes.addFlashAttribute("successMessage", "Natureza operacional excluida com sucesso");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/ui/admin/naturezas-operacionais-leito";
    }
}
