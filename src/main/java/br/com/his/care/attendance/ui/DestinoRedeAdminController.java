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

import br.com.his.care.attendance.dto.DestinoRedeForm;
import br.com.his.care.attendance.service.DestinoRedeAdminService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/ui/admin/destinos-rede")
public class DestinoRedeAdminController {

    private final DestinoRedeAdminService service;

    public DestinoRedeAdminController(DestinoRedeAdminService service) {
        this.service = service;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) String q, Model model) {
        model.addAttribute("items", service.listar(q));
        model.addAttribute("q", q);
        return "pages/care/attendance/admin/destinos-rede/list";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new DestinoRedeForm());
        }
        model.addAttribute("modoEdicao", false);
        return "pages/care/attendance/admin/destinos-rede/form";
    }

    @PostMapping
    public String criar(@Valid @ModelAttribute("form") DestinoRedeForm form,
                        BindingResult bindingResult,
                        Model model,
                        RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("modoEdicao", false);
            return "pages/care/attendance/admin/destinos-rede/form";
        }
        service.criar(form);
        redirectAttributes.addFlashAttribute("successMessage", "Destino de rede cadastrado com sucesso");
        return "redirect:/ui/admin/destinos-rede";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("form", service.toForm(service.buscar(id)));
        model.addAttribute("modoEdicao", true);
        model.addAttribute("itemId", id);
        return "pages/care/attendance/admin/destinos-rede/form";
    }

    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id,
                            @Valid @ModelAttribute("form") DestinoRedeForm form,
                            BindingResult bindingResult,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("modoEdicao", true);
            model.addAttribute("itemId", id);
            return "pages/care/attendance/admin/destinos-rede/form";
        }
        service.atualizar(id, form);
        redirectAttributes.addFlashAttribute("successMessage", "Destino de rede atualizado com sucesso");
        return "redirect:/ui/admin/destinos-rede";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        service.excluir(id);
        redirectAttributes.addFlashAttribute("successMessage", "Destino de rede excluido com sucesso");
        return "redirect:/ui/admin/destinos-rede";
    }
}
