package br.com.his.patient.ui;

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

import br.com.his.patient.dto.SexoForm;
import br.com.his.patient.service.SexoAdminService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/ui/admin/sexos")
public class SexoAdminController {

    private final SexoAdminService service;

    public SexoAdminController(SexoAdminService service) {
        this.service = service;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) String q, Model model) {
        model.addAttribute("items", service.listar(q));
        model.addAttribute("q", q);
        return "pages/patient/admin/sexos/list";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new SexoForm());
        }
        model.addAttribute("modoEdicao", false);
        return "pages/patient/admin/sexos/form";
    }

    @PostMapping
    public String criar(@Valid @ModelAttribute("form") SexoForm form,
                        BindingResult bindingResult,
                        Model model,
                        RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("modoEdicao", false);
            return "pages/patient/admin/sexos/form";
        }
        try {
            service.criar(form);
            redirectAttributes.addFlashAttribute("successMessage", "Sexo cadastrado com sucesso");
            return "redirect:/ui/admin/sexos";
        } catch (IllegalArgumentException ex) {
            bindingResult.reject("form", ex.getMessage());
            model.addAttribute("modoEdicao", false);
            return "pages/patient/admin/sexos/form";
        }
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("form", service.toForm(service.buscar(id)));
        model.addAttribute("modoEdicao", true);
        model.addAttribute("itemId", id);
        return "pages/patient/admin/sexos/form";
    }

    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id,
                            @Valid @ModelAttribute("form") SexoForm form,
                            BindingResult bindingResult,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("modoEdicao", true);
            model.addAttribute("itemId", id);
            return "pages/patient/admin/sexos/form";
        }
        try {
            service.atualizar(id, form);
            redirectAttributes.addFlashAttribute("successMessage", "Sexo atualizado com sucesso");
            return "redirect:/ui/admin/sexos";
        } catch (IllegalArgumentException ex) {
            bindingResult.reject("form", ex.getMessage());
            model.addAttribute("modoEdicao", true);
            model.addAttribute("itemId", id);
            return "pages/patient/admin/sexos/form";
        }
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            service.excluir(id);
            redirectAttributes.addFlashAttribute("successMessage", "Sexo excluido com sucesso");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/ui/admin/sexos";
    }
}
