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

import br.com.his.patient.dto.TipoProcedenciaForm;
import br.com.his.patient.service.TipoProcedenciaAdminService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/ui/admin/tipos-procedencia")
public class TipoProcedenciaAdminController {

    private final TipoProcedenciaAdminService service;

    public TipoProcedenciaAdminController(TipoProcedenciaAdminService service) {
        this.service = service;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) String q, Model model) {
        model.addAttribute("items", service.listar(q));
        model.addAttribute("q", q);
        return "pages/patient/admin/tipos-procedencia/list";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new TipoProcedenciaForm());
        }
        model.addAttribute("modoEdicao", false);
        return "pages/patient/admin/tipos-procedencia/form";
    }

    @PostMapping
    public String criar(@Valid @ModelAttribute("form") TipoProcedenciaForm form,
                        BindingResult bindingResult,
                        Model model,
                        RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("modoEdicao", false);
            return "pages/patient/admin/tipos-procedencia/form";
        }
        try {
            service.criar(form);
            redirectAttributes.addFlashAttribute("successMessage", "Tipo de procedencia cadastrado com sucesso");
            return "redirect:/ui/admin/tipos-procedencia";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("modoEdicao", false);
            model.addAttribute("errorMessage", ex.getMessage());
            return "pages/patient/admin/tipos-procedencia/form";
        }
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("form", service.toForm(service.buscar(id)));
        model.addAttribute("modoEdicao", true);
        model.addAttribute("itemId", id);
        return "pages/patient/admin/tipos-procedencia/form";
    }

    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id,
                            @Valid @ModelAttribute("form") TipoProcedenciaForm form,
                            BindingResult bindingResult,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("modoEdicao", true);
            model.addAttribute("itemId", id);
            return "pages/patient/admin/tipos-procedencia/form";
        }
        try {
            service.atualizar(id, form);
            redirectAttributes.addFlashAttribute("successMessage", "Tipo de procedencia atualizado com sucesso");
            return "redirect:/ui/admin/tipos-procedencia";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("modoEdicao", true);
            model.addAttribute("itemId", id);
            model.addAttribute("errorMessage", ex.getMessage());
            return "pages/patient/admin/tipos-procedencia/form";
        }
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            service.excluir(id);
            redirectAttributes.addFlashAttribute("successMessage", "Tipo de procedencia excluido com sucesso");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/ui/admin/tipos-procedencia";
    }
}
