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

import br.com.his.care.inpatient.dto.AreaForm;
import br.com.his.care.inpatient.service.AreaAdminService;
import br.com.his.access.service.UnidadeAdminService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/ui/admin/areas")
public class AreaAdminController {

    private final AreaAdminService service;
    private final UnidadeAdminService unidadeAdminService;

    public AreaAdminController(AreaAdminService service, UnidadeAdminService unidadeAdminService) {
        this.service = service;
        this.unidadeAdminService = unidadeAdminService;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) String q, Model model) {
        model.addAttribute("items", service.listar(q));
        model.addAttribute("q", q);
        return "pages/care/inpatient/admin/areas/list";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new AreaForm());
        }
        populateModel(model);
        model.addAttribute("modoEdicao", false);
        return "pages/care/inpatient/admin/areas/form";
    }

    @PostMapping
    public String criar(@Valid @ModelAttribute("form") AreaForm form,
                        BindingResult bindingResult,
                        Model model,
                        RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            populateModel(model);
            model.addAttribute("modoEdicao", false);
            return "pages/care/inpatient/admin/areas/form";
        }
        service.criar(form);
        redirectAttributes.addFlashAttribute("successMessage", "Area cadastrada com sucesso");
        return "redirect:/ui/admin/areas";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("form", service.toForm(service.buscar(id)));
        populateModel(model);
        model.addAttribute("modoEdicao", true);
        model.addAttribute("itemId", id);
        return "pages/care/inpatient/admin/areas/form";
    }

    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id,
                            @Valid @ModelAttribute("form") AreaForm form,
                            BindingResult bindingResult,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            populateModel(model);
            model.addAttribute("modoEdicao", true);
            model.addAttribute("itemId", id);
            return "pages/care/inpatient/admin/areas/form";
        }
        service.atualizar(id, form);
        redirectAttributes.addFlashAttribute("successMessage", "Area atualizada com sucesso");
        return "redirect:/ui/admin/areas";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        service.excluir(id);
        redirectAttributes.addFlashAttribute("successMessage", "Area excluida com sucesso");
        return "redirect:/ui/admin/areas";
    }

    private void populateModel(Model model) {
        model.addAttribute("unidades", unidadeAdminService.listar(null));
    }
}
