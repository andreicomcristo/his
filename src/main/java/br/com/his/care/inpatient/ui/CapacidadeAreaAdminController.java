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

import br.com.his.care.inpatient.dto.CapacidadeAreaForm;
import br.com.his.care.inpatient.service.CapacidadeAreaAdminService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/ui/admin/capacidades-area")
public class CapacidadeAreaAdminController {

    private final CapacidadeAreaAdminService service;

    public CapacidadeAreaAdminController(CapacidadeAreaAdminService service) {
        this.service = service;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) String q, Model model) {
        model.addAttribute("items", service.listar(q));
        model.addAttribute("q", q);
        return "pages/care/inpatient/admin/capacidades-area/list";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new CapacidadeAreaForm());
        }
        model.addAttribute("modoEdicao", false);
        return "pages/care/inpatient/admin/capacidades-area/form";
    }

    @PostMapping
    public String criar(@Valid @ModelAttribute("form") CapacidadeAreaForm form,
                        BindingResult bindingResult,
                        Model model,
                        RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("modoEdicao", false);
            return "pages/care/inpatient/admin/capacidades-area/form";
        }
        service.criar(form);
        redirectAttributes.addFlashAttribute("successMessage", "Capacidade cadastrada com sucesso");
        return "redirect:/ui/admin/capacidades-area";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("form", service.toForm(service.buscar(id)));
        model.addAttribute("modoEdicao", true);
        model.addAttribute("itemId", id);
        return "pages/care/inpatient/admin/capacidades-area/form";
    }

    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id,
                            @Valid @ModelAttribute("form") CapacidadeAreaForm form,
                            BindingResult bindingResult,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("modoEdicao", true);
            model.addAttribute("itemId", id);
            return "pages/care/inpatient/admin/capacidades-area/form";
        }
        service.atualizar(id, form);
        redirectAttributes.addFlashAttribute("successMessage", "Capacidade atualizada com sucesso");
        return "redirect:/ui/admin/capacidades-area";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        service.excluir(id);
        redirectAttributes.addFlashAttribute("successMessage", "Capacidade excluida com sucesso");
        return "redirect:/ui/admin/capacidades-area";
    }
}


