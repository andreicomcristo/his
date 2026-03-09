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

import br.com.his.admin.dto.MotivoDesfechoForm;
import br.com.his.admin.service.MotivoDesfechoAdminService;
import jakarta.validation.Valid;

@Controller
@RequestMapping({"/ui/admin/motivos-desfecho", "/ui/admin/motivos-alta"})
public class MotivoDesfechoAdminController {

    private final MotivoDesfechoAdminService service;

    public MotivoDesfechoAdminController(MotivoDesfechoAdminService service) {
        this.service = service;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) String q, Model model) {
        model.addAttribute("items", service.listar(q));
        model.addAttribute("q", q);
        return "pages/admin/motivos-desfecho/list";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new MotivoDesfechoForm());
        }
        model.addAttribute("modoEdicao", false);
        return "pages/admin/motivos-desfecho/form";
    }

    @PostMapping
    public String criar(@Valid @ModelAttribute("form") MotivoDesfechoForm form,
                        BindingResult bindingResult,
                        Model model,
                        RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("modoEdicao", false);
            return "pages/admin/motivos-desfecho/form";
        }
        service.criar(form);
        redirectAttributes.addFlashAttribute("successMessage", "Motivo de desfecho cadastrado com sucesso");
        return "redirect:/ui/admin/motivos-desfecho";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("form", service.toForm(service.buscar(id)));
        model.addAttribute("modoEdicao", true);
        model.addAttribute("itemId", id);
        return "pages/admin/motivos-desfecho/form";
    }

    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id,
                            @Valid @ModelAttribute("form") MotivoDesfechoForm form,
                            BindingResult bindingResult,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("modoEdicao", true);
            model.addAttribute("itemId", id);
            return "pages/admin/motivos-desfecho/form";
        }
        service.atualizar(id, form);
        redirectAttributes.addFlashAttribute("successMessage", "Motivo de desfecho atualizado com sucesso");
        return "redirect:/ui/admin/motivos-desfecho";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        service.excluir(id);
        redirectAttributes.addFlashAttribute("successMessage", "Motivo de desfecho excluido com sucesso");
        return "redirect:/ui/admin/motivos-desfecho";
    }
}
