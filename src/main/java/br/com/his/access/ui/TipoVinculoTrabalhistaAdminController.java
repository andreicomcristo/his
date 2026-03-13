package br.com.his.access.ui;

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

import br.com.his.access.dto.TipoVinculoTrabalhistaForm;
import br.com.his.access.service.TipoVinculoTrabalhistaAdminService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/ui/admin/tipos-vinculo-trabalhista")
public class TipoVinculoTrabalhistaAdminController {

    private final TipoVinculoTrabalhistaAdminService service;

    public TipoVinculoTrabalhistaAdminController(TipoVinculoTrabalhistaAdminService service) {
        this.service = service;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) String q,
                         @RequestParam(required = false) Boolean ativo,
                         Model model) {
        model.addAttribute("items", service.listar(q, ativo));
        model.addAttribute("q", q);
        model.addAttribute("ativo", ativo);
        return "pages/access/admin/tipos-vinculo-trabalhista/list";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new TipoVinculoTrabalhistaForm());
        }
        model.addAttribute("modoEdicao", false);
        return "pages/access/admin/tipos-vinculo-trabalhista/form";
    }

    @PostMapping
    public String criar(@Valid @ModelAttribute("form") TipoVinculoTrabalhistaForm form,
                        BindingResult bindingResult,
                        Model model,
                        RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("modoEdicao", false);
            return "pages/access/admin/tipos-vinculo-trabalhista/form";
        }
        try {
            service.criar(form);
            redirectAttributes.addFlashAttribute("successMessage", "Tipo de vinculo cadastrado com sucesso");
            return "redirect:/ui/admin/tipos-vinculo-trabalhista";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("modoEdicao", false);
            model.addAttribute("errorMessage", ex.getMessage());
            return "pages/access/admin/tipos-vinculo-trabalhista/form";
        }
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("form", service.toForm(service.buscar(id)));
        model.addAttribute("modoEdicao", true);
        model.addAttribute("itemId", id);
        return "pages/access/admin/tipos-vinculo-trabalhista/form";
    }

    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id,
                            @Valid @ModelAttribute("form") TipoVinculoTrabalhistaForm form,
                            BindingResult bindingResult,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("modoEdicao", true);
            model.addAttribute("itemId", id);
            return "pages/access/admin/tipos-vinculo-trabalhista/form";
        }
        try {
            service.atualizar(id, form);
            redirectAttributes.addFlashAttribute("successMessage", "Tipo de vinculo atualizado com sucesso");
            return "redirect:/ui/admin/tipos-vinculo-trabalhista";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("modoEdicao", true);
            model.addAttribute("itemId", id);
            model.addAttribute("errorMessage", ex.getMessage());
            return "pages/access/admin/tipos-vinculo-trabalhista/form";
        }
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id,
                          RedirectAttributes redirectAttributes) {
        try {
            service.excluir(id);
            redirectAttributes.addFlashAttribute("successMessage", "Tipo de vinculo excluido com sucesso");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/ui/admin/tipos-vinculo-trabalhista";
    }
}
