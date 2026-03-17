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

import br.com.his.access.dto.TipoUnidadeForm;
import br.com.his.access.service.TipoUnidadeAdminService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/ui/admin/tipos-unidade")
public class TipoUnidadeAdminController {

    private final TipoUnidadeAdminService service;

    public TipoUnidadeAdminController(TipoUnidadeAdminService service) {
        this.service = service;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) String q, Model model) {
        model.addAttribute("items", service.listar(q));
        model.addAttribute("q", q);
        return "pages/access/admin/tipos-unidade/list";
    }

    @GetMapping("/cancelados")
    public String listarCancelados(@RequestParam(required = false) String q, Model model) {
        model.addAttribute("items", service.listarCancelados(q));
        model.addAttribute("q", q);
        return "pages/access/admin/tipos-unidade/cancel_list";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new TipoUnidadeForm());
        }
        model.addAttribute("modoEdicao", false);
        return "pages/access/admin/tipos-unidade/form";
    }

    @PostMapping
    public String criar(@Valid @ModelAttribute("form") TipoUnidadeForm form,
                        BindingResult bindingResult,
                        Model model,
                        RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("modoEdicao", false);
            return "pages/access/admin/tipos-unidade/form";
        }
        try {
            service.criar(form);
            redirectAttributes.addFlashAttribute("successMessage", "Tipo de unidade cadastrado com sucesso");
            return "redirect:/ui/admin/tipos-unidade";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("modoEdicao", false);
            model.addAttribute("errorMessage", ex.getMessage());
            return "pages/access/admin/tipos-unidade/form";
        }
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("form", service.toForm(service.buscarAtivo(id)));
        model.addAttribute("modoEdicao", true);
        model.addAttribute("itemId", id);
        return "pages/access/admin/tipos-unidade/form";
    }

    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id,
                            @Valid @ModelAttribute("form") TipoUnidadeForm form,
                            BindingResult bindingResult,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("modoEdicao", true);
            model.addAttribute("itemId", id);
            return "pages/access/admin/tipos-unidade/form";
        }
        try {
            service.atualizar(id, form);
            redirectAttributes.addFlashAttribute("successMessage", "Tipo de unidade atualizado com sucesso");
            return "redirect:/ui/admin/tipos-unidade";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("modoEdicao", true);
            model.addAttribute("itemId", id);
            model.addAttribute("errorMessage", ex.getMessage());
            return "pages/access/admin/tipos-unidade/form";
        }
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id,
                          RedirectAttributes redirectAttributes) {
        try {
            service.excluir(id);
            redirectAttributes.addFlashAttribute("successMessage", "Tipo de unidade cancelado com sucesso");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/ui/admin/tipos-unidade";
    }

    @PostMapping("/{id}/restaurar")
    public String restaurar(@PathVariable Long id,
                            RedirectAttributes redirectAttributes) {
        try {
            service.restaurar(id);
            redirectAttributes.addFlashAttribute("successMessage", "Tipo de unidade restaurado com sucesso");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/ui/admin/tipos-unidade/cancelados";
    }

    @PostMapping("/{id}/excluir-permanente")
    public String excluirPermanente(@PathVariable Long id,
                                    RedirectAttributes redirectAttributes) {
        try {
            service.excluirPermanente(id);
            redirectAttributes.addFlashAttribute("successMessage", "Tipo de unidade excluido permanentemente com sucesso");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/ui/admin/tipos-unidade/cancelados";
    }
}
