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

import br.com.his.care.triage.dto.GlasgowRespostaPupilarForm;
import br.com.his.care.triage.service.GlasgowRespostaPupilarAdminService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/ui/admin/glasgow-resposta-pupilar")
public class GlasgowRespostaPupilarAdminController {

    private final GlasgowRespostaPupilarAdminService service;

    public GlasgowRespostaPupilarAdminController(GlasgowRespostaPupilarAdminService service) {
        this.service = service;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) String q, Model model) {
        model.addAttribute("items", service.listar(q));
        model.addAttribute("q", q);
        return "pages/admin/glasgow-resposta-pupilar/list";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new GlasgowRespostaPupilarForm());
        }
        model.addAttribute("modoEdicao", false);
        return "pages/admin/glasgow-resposta-pupilar/form";
    }

    @PostMapping
    public String criar(@Valid @ModelAttribute("form") GlasgowRespostaPupilarForm form,
                        BindingResult bindingResult,
                        Model model,
                        RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("modoEdicao", false);
            return "pages/admin/glasgow-resposta-pupilar/form";
        }
        service.criar(form);
        redirectAttributes.addFlashAttribute("successMessage", "Item da escala de Glasgow (resposta pupilar) cadastrado com sucesso");
        return "redirect:/ui/admin/glasgow-resposta-pupilar";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("form", service.toForm(service.buscar(id)));
        model.addAttribute("modoEdicao", true);
        model.addAttribute("itemId", id);
        return "pages/admin/glasgow-resposta-pupilar/form";
    }

    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id,
                            @Valid @ModelAttribute("form") GlasgowRespostaPupilarForm form,
                            BindingResult bindingResult,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("modoEdicao", true);
            model.addAttribute("itemId", id);
            return "pages/admin/glasgow-resposta-pupilar/form";
        }
        service.atualizar(id, form);
        redirectAttributes.addFlashAttribute("successMessage", "Item da escala de Glasgow (resposta pupilar) atualizado com sucesso");
        return "redirect:/ui/admin/glasgow-resposta-pupilar";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        service.excluir(id);
        redirectAttributes.addFlashAttribute("successMessage", "Item da escala de Glasgow (resposta pupilar) excluido com sucesso");
        return "redirect:/ui/admin/glasgow-resposta-pupilar";
    }
}
