package br.com.his.care.admission.ui;

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

import br.com.his.care.admission.dto.FormaChegadaForm;
import br.com.his.care.admission.service.FormaChegadaAdminService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/ui/admin/formas-chegada")
public class FormaChegadaAdminController {

    private final FormaChegadaAdminService service;

    public FormaChegadaAdminController(FormaChegadaAdminService service) {
        this.service = service;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) String q, Model model) {
        model.addAttribute("items", service.listar(q));
        model.addAttribute("q", q);
        return "pages/care/admission/admin/formas-chegada/list";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new FormaChegadaForm());
        }
        model.addAttribute("modoEdicao", false);
        return "pages/care/admission/admin/formas-chegada/form";
    }

    @PostMapping
    public String criar(@Valid @ModelAttribute("form") FormaChegadaForm form,
                        BindingResult bindingResult,
                        Model model,
                        RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("modoEdicao", false);
            return "pages/care/admission/admin/formas-chegada/form";
        }
        service.criar(form);
        redirectAttributes.addFlashAttribute("successMessage", "Forma de chegada cadastrada com sucesso");
        return "redirect:/ui/admin/formas-chegada";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("form", service.toForm(service.buscar(id)));
        model.addAttribute("modoEdicao", true);
        model.addAttribute("itemId", id);
        return "pages/care/admission/admin/formas-chegada/form";
    }

    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id,
                            @Valid @ModelAttribute("form") FormaChegadaForm form,
                            BindingResult bindingResult,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("modoEdicao", true);
            model.addAttribute("itemId", id);
            return "pages/care/admission/admin/formas-chegada/form";
        }
        service.atualizar(id, form);
        redirectAttributes.addFlashAttribute("successMessage", "Forma de chegada atualizada com sucesso");
        return "redirect:/ui/admin/formas-chegada";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        service.excluir(id);
        redirectAttributes.addFlashAttribute("successMessage", "Forma de chegada excluida com sucesso");
        return "redirect:/ui/admin/formas-chegada";
    }
}
