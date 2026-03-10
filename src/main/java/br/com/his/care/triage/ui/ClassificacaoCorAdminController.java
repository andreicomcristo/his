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

import br.com.his.care.triage.dto.ClassificacaoCorForm;
import br.com.his.care.triage.service.ClassificacaoCorAdminService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/ui/admin/classificacao-cores")
public class ClassificacaoCorAdminController {

    private final ClassificacaoCorAdminService service;

    public ClassificacaoCorAdminController(ClassificacaoCorAdminService service) {
        this.service = service;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) String q, Model model) {
        model.addAttribute("items", service.listar(q));
        model.addAttribute("q", q);
        return "pages/care/triage/admin/classificacao-cores/list";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new ClassificacaoCorForm());
        }
        model.addAttribute("modoEdicao", false);
        return "pages/care/triage/admin/classificacao-cores/form";
    }

    @PostMapping
    public String criar(@Valid @ModelAttribute("form") ClassificacaoCorForm form,
                        BindingResult bindingResult,
                        Model model,
                        RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("modoEdicao", false);
            return "pages/care/triage/admin/classificacao-cores/form";
        }
        service.criar(form);
        redirectAttributes.addFlashAttribute("successMessage", "Classificacao de cor cadastrada com sucesso");
        return "redirect:/ui/admin/classificacao-cores";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("form", service.toForm(service.buscar(id)));
        model.addAttribute("modoEdicao", true);
        model.addAttribute("itemId", id);
        return "pages/care/triage/admin/classificacao-cores/form";
    }

    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id,
                            @Valid @ModelAttribute("form") ClassificacaoCorForm form,
                            BindingResult bindingResult,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("modoEdicao", true);
            model.addAttribute("itemId", id);
            return "pages/care/triage/admin/classificacao-cores/form";
        }
        service.atualizar(id, form);
        redirectAttributes.addFlashAttribute("successMessage", "Classificacao de cor atualizada com sucesso");
        return "redirect:/ui/admin/classificacao-cores";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        service.excluir(id);
        redirectAttributes.addFlashAttribute("successMessage", "Classificacao de cor excluida com sucesso");
        return "redirect:/ui/admin/classificacao-cores";
    }
}
