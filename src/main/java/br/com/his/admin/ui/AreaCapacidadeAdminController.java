package br.com.his.admin.ui;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.his.admin.dto.AreaCapacidadeForm;
import br.com.his.admin.service.AreaCapacidadeAdminService;
import br.com.his.admin.service.CapacidadeAreaAdminService;

@Controller
@RequestMapping("/ui/admin/areas/{areaId}/capacidades")
public class AreaCapacidadeAdminController {

    private final AreaCapacidadeAdminService service;
    private final CapacidadeAreaAdminService capacidadeAreaAdminService;

    public AreaCapacidadeAdminController(AreaCapacidadeAdminService service,
                                         CapacidadeAreaAdminService capacidadeAreaAdminService) {
        this.service = service;
        this.capacidadeAreaAdminService = capacidadeAreaAdminService;
    }

    @GetMapping
    public String editar(@PathVariable Long areaId, Model model) {
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", service.toForm(areaId));
        }
        model.addAttribute("area", service.buscarArea(areaId));
        model.addAttribute("capacidades", capacidadeAreaAdminService.listarTodas());
        return "pages/admin/areas/capacidades";
    }

    @PostMapping
    public String salvar(@PathVariable Long areaId,
                         @ModelAttribute("form") AreaCapacidadeForm form,
                         RedirectAttributes redirectAttributes) {
        service.salvar(areaId, form);
        redirectAttributes.addFlashAttribute("successMessage", "Capacidades da area atualizadas com sucesso");
        return "redirect:/ui/admin/areas";
    }
}
