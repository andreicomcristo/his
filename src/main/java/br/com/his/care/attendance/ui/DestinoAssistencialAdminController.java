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

import br.com.his.access.service.UnidadeAdminService;
import br.com.his.care.attendance.dto.DestinoAssistencialAreaForm;
import br.com.his.care.attendance.dto.DestinoAssistencialForm;
import br.com.his.care.attendance.service.DestinoAssistencialAdminService;
import br.com.his.care.attendance.service.TipoDestinoAssistencialAdminService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/ui/admin/destinos-assistenciais")
public class DestinoAssistencialAdminController {

    private final DestinoAssistencialAdminService service;
    private final UnidadeAdminService unidadeAdminService;
    private final TipoDestinoAssistencialAdminService tipoDestinoAssistencialAdminService;

    public DestinoAssistencialAdminController(DestinoAssistencialAdminService service,
                                              UnidadeAdminService unidadeAdminService,
                                              TipoDestinoAssistencialAdminService tipoDestinoAssistencialAdminService) {
        this.service = service;
        this.unidadeAdminService = unidadeAdminService;
        this.tipoDestinoAssistencialAdminService = tipoDestinoAssistencialAdminService;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) String q, Model model) {
        model.addAttribute("items", service.listar(q));
        model.addAttribute("q", q);
        return "pages/care/attendance/admin/destinos-assistenciais/list";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new DestinoAssistencialForm());
        }
        populateModel(model);
        model.addAttribute("modoEdicao", false);
        return "pages/care/attendance/admin/destinos-assistenciais/form";
    }

    @PostMapping
    public String criar(@Valid @ModelAttribute("form") DestinoAssistencialForm form,
                        BindingResult bindingResult,
                        Model model,
                        RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            populateModel(model);
            model.addAttribute("modoEdicao", false);
            return "pages/care/attendance/admin/destinos-assistenciais/form";
        }
        try {
            service.criar(form);
            redirectAttributes.addFlashAttribute("successMessage", "Destino assistencial cadastrado com sucesso");
            return "redirect:/ui/admin/destinos-assistenciais";
        } catch (IllegalArgumentException ex) {
            populateModel(model);
            model.addAttribute("modoEdicao", false);
            model.addAttribute("errorMessage", ex.getMessage());
            return "pages/care/attendance/admin/destinos-assistenciais/form";
        }
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("form", service.toForm(service.buscar(id)));
        populateModel(model);
        model.addAttribute("modoEdicao", true);
        model.addAttribute("itemId", id);
        return "pages/care/attendance/admin/destinos-assistenciais/form";
    }

    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id,
                            @Valid @ModelAttribute("form") DestinoAssistencialForm form,
                            BindingResult bindingResult,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            populateModel(model);
            model.addAttribute("modoEdicao", true);
            model.addAttribute("itemId", id);
            return "pages/care/attendance/admin/destinos-assistenciais/form";
        }
        try {
            service.atualizar(id, form);
            redirectAttributes.addFlashAttribute("successMessage", "Destino assistencial atualizado com sucesso");
            return "redirect:/ui/admin/destinos-assistenciais";
        } catch (IllegalArgumentException ex) {
            populateModel(model);
            model.addAttribute("modoEdicao", true);
            model.addAttribute("itemId", id);
            model.addAttribute("errorMessage", ex.getMessage());
            return "pages/care/attendance/admin/destinos-assistenciais/form";
        }
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            service.excluir(id);
            redirectAttributes.addFlashAttribute("successMessage", "Destino assistencial inativado com sucesso");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/ui/admin/destinos-assistenciais";
    }

    @GetMapping("/{id}/areas")
    public String areas(@PathVariable Long id, Model model) {
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", service.toAreaForm(id));
        }
        model.addAttribute("destino", service.buscar(id));
        model.addAttribute("areasDisponiveis", service.listarAreasDisponiveis(id));
        model.addAttribute("areasVinculadas", service.listarAreasVinculadas(id));
        return "pages/care/attendance/admin/destinos-assistenciais/areas";
    }

    @PostMapping("/{id}/areas")
    public String salvarAreas(@PathVariable Long id,
                              @ModelAttribute("form") DestinoAssistencialAreaForm form,
                              RedirectAttributes redirectAttributes) {
        try {
            service.salvarAreas(id, form);
            redirectAttributes.addFlashAttribute("successMessage", "Mapeamento de areas atualizado com sucesso");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/ui/admin/destinos-assistenciais/" + id + "/areas";
    }

    private void populateModel(Model model) {
        model.addAttribute("unidades", unidadeAdminService.listar(null));
        model.addAttribute("tiposDestinoAssistencial", tipoDestinoAssistencialAdminService.listarAtivos());
    }
}
