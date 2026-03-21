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

import br.com.his.care.attendance.dto.TipoDestinoAssistencialForm;
import br.com.his.care.attendance.service.TipoDestinoAssistencialAdminService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/ui/admin/tipos-destino-assistencial")
public class TipoDestinoAssistencialAdminController {

    private final TipoDestinoAssistencialAdminService service;

    public TipoDestinoAssistencialAdminController(TipoDestinoAssistencialAdminService service) {
        this.service = service;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) String q, Model model) {
        model.addAttribute("items", service.listar(q));
        model.addAttribute("q", q);
        return "pages/care/attendance/admin/tipos-destino-assistencial/list";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new TipoDestinoAssistencialForm());
        }
        model.addAttribute("modoEdicao", false);
        return "pages/care/attendance/admin/tipos-destino-assistencial/form";
    }

    @PostMapping
    public String criar(@Valid @ModelAttribute("form") TipoDestinoAssistencialForm form,
                        BindingResult bindingResult,
                        Model model,
                        RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("modoEdicao", false);
            return "pages/care/attendance/admin/tipos-destino-assistencial/form";
        }
        try {
            service.criar(form);
            redirectAttributes.addFlashAttribute("successMessage", "Tipo de destino assistencial cadastrado com sucesso");
            return "redirect:/ui/admin/tipos-destino-assistencial";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("modoEdicao", false);
            model.addAttribute("errorMessage", ex.getMessage());
            return "pages/care/attendance/admin/tipos-destino-assistencial/form";
        }
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("form", service.toForm(service.buscar(id)));
        model.addAttribute("modoEdicao", true);
        model.addAttribute("itemId", id);
        return "pages/care/attendance/admin/tipos-destino-assistencial/form";
    }

    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id,
                            @Valid @ModelAttribute("form") TipoDestinoAssistencialForm form,
                            BindingResult bindingResult,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("modoEdicao", true);
            model.addAttribute("itemId", id);
            return "pages/care/attendance/admin/tipos-destino-assistencial/form";
        }
        try {
            service.atualizar(id, form);
            redirectAttributes.addFlashAttribute("successMessage", "Tipo de destino assistencial atualizado com sucesso");
            return "redirect:/ui/admin/tipos-destino-assistencial";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("modoEdicao", true);
            model.addAttribute("itemId", id);
            model.addAttribute("errorMessage", ex.getMessage());
            return "pages/care/attendance/admin/tipos-destino-assistencial/form";
        }
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            service.excluir(id);
            redirectAttributes.addFlashAttribute("successMessage", "Tipo de destino assistencial excluido com sucesso");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/ui/admin/tipos-destino-assistencial";
    }
}
