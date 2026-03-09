package br.com.his.admin.ui;

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

import br.com.his.admin.dto.GrauParentescoForm;
import br.com.his.admin.service.GrauParentescoAdminService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/ui/admin/graus-parentesco")
public class GrauParentescoAdminController {

    private final GrauParentescoAdminService service;

    public GrauParentescoAdminController(GrauParentescoAdminService service) {
        this.service = service;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) String q, Model model) {
        model.addAttribute("items", service.listar(q));
        model.addAttribute("q", q);
        return "pages/admin/graus-parentesco/list";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new GrauParentescoForm());
        }
        model.addAttribute("modoEdicao", false);
        return "pages/admin/graus-parentesco/form";
    }

    @PostMapping
    public String criar(@Valid @ModelAttribute("form") GrauParentescoForm form,
                        BindingResult bindingResult,
                        Model model,
                        RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("modoEdicao", false);
            return "pages/admin/graus-parentesco/form";
        }
        service.criar(form);
        redirectAttributes.addFlashAttribute("successMessage", "Grau de parentesco cadastrado com sucesso");
        return "redirect:/ui/admin/graus-parentesco";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("form", service.toForm(service.buscar(id)));
        model.addAttribute("modoEdicao", true);
        model.addAttribute("itemId", id);
        return "pages/admin/graus-parentesco/form";
    }

    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id,
                            @Valid @ModelAttribute("form") GrauParentescoForm form,
                            BindingResult bindingResult,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("modoEdicao", true);
            model.addAttribute("itemId", id);
            return "pages/admin/graus-parentesco/form";
        }
        service.atualizar(id, form);
        redirectAttributes.addFlashAttribute("successMessage", "Grau de parentesco atualizado com sucesso");
        return "redirect:/ui/admin/graus-parentesco";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        service.excluir(id);
        redirectAttributes.addFlashAttribute("successMessage", "Grau de parentesco excluido com sucesso");
        return "redirect:/ui/admin/graus-parentesco";
    }
}
