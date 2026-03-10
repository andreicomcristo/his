package br.com.his.reference.location.ui;

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

import br.com.his.reference.location.dto.UnidadeFederativaForm;
import br.com.his.reference.location.service.UnidadeFederativaAdminService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/ui/admin/unidades-federativas")
public class UnidadeFederativaAdminController {

    private final UnidadeFederativaAdminService service;

    public UnidadeFederativaAdminController(UnidadeFederativaAdminService service) {
        this.service = service;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) String q, Model model) {
        model.addAttribute("items", service.listar(q));
        model.addAttribute("q", q);
        return "pages/reference/location/admin/unidades-federativas/list";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new UnidadeFederativaForm());
        }
        model.addAttribute("modoEdicao", false);
        return "pages/reference/location/admin/unidades-federativas/form";
    }

    @PostMapping
    public String criar(@Valid @ModelAttribute("form") UnidadeFederativaForm form,
                        BindingResult bindingResult,
                        Model model,
                        RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("modoEdicao", false);
            return "pages/reference/location/admin/unidades-federativas/form";
        }
        service.criar(form);
        redirectAttributes.addFlashAttribute("successMessage", "UF cadastrada com sucesso");
        return "redirect:/ui/admin/unidades-federativas";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("form", service.toForm(service.buscar(id)));
        model.addAttribute("modoEdicao", true);
        model.addAttribute("itemId", id);
        return "pages/reference/location/admin/unidades-federativas/form";
    }

    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id,
                            @Valid @ModelAttribute("form") UnidadeFederativaForm form,
                            BindingResult bindingResult,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("modoEdicao", true);
            model.addAttribute("itemId", id);
            return "pages/reference/location/admin/unidades-federativas/form";
        }
        service.atualizar(id, form);
        redirectAttributes.addFlashAttribute("successMessage", "UF atualizada com sucesso");
        return "redirect:/ui/admin/unidades-federativas";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        service.excluir(id);
        redirectAttributes.addFlashAttribute("successMessage", "UF excluida com sucesso");
        return "redirect:/ui/admin/unidades-federativas";
    }
}
