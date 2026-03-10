package br.com.his.care.attendance.ui;

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

import br.com.his.care.attendance.dto.StatusAtendimentoForm;
import br.com.his.care.attendance.service.StatusAtendimentoAdminService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/ui/admin/status-atendimento")
public class StatusAtendimentoAdminController {

    private final StatusAtendimentoAdminService service;

    public StatusAtendimentoAdminController(StatusAtendimentoAdminService service) {
        this.service = service;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) String q, Model model) {
        model.addAttribute("items", service.listar(q));
        model.addAttribute("q", q);
        return "pages/admin/status-atendimento/list";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new StatusAtendimentoForm());
        }
        model.addAttribute("modoEdicao", false);
        return "pages/admin/status-atendimento/form";
    }

    @PostMapping
    public String criar(@Valid @ModelAttribute("form") StatusAtendimentoForm form,
                        BindingResult bindingResult,
                        Model model,
                        RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("modoEdicao", false);
            return "pages/admin/status-atendimento/form";
        }
        service.criar(form);
        redirectAttributes.addFlashAttribute("successMessage", "Status de atendimento cadastrado com sucesso");
        return "redirect:/ui/admin/status-atendimento";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("form", service.toForm(service.buscar(id)));
        model.addAttribute("modoEdicao", true);
        model.addAttribute("itemId", id);
        return "pages/admin/status-atendimento/form";
    }

    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id,
                            @Valid @ModelAttribute("form") StatusAtendimentoForm form,
                            BindingResult bindingResult,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("modoEdicao", true);
            model.addAttribute("itemId", id);
            return "pages/admin/status-atendimento/form";
        }
        service.atualizar(id, form);
        redirectAttributes.addFlashAttribute("successMessage", "Status de atendimento atualizado com sucesso");
        return "redirect:/ui/admin/status-atendimento";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        service.excluir(id);
        redirectAttributes.addFlashAttribute("successMessage", "Status de atendimento excluido com sucesso");
        return "redirect:/ui/admin/status-atendimento";
    }
}
