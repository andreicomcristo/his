package br.com.his.care.inpatient.ui;

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

import br.com.his.care.inpatient.dto.LeitoForm;
import br.com.his.care.inpatient.service.LeitoAdminService;
import br.com.his.access.service.UnidadeAdminService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/ui/admin/leitos")
public class LeitoAdminController {

    private final LeitoAdminService service;
    private final UnidadeAdminService unidadeAdminService;

    public LeitoAdminController(LeitoAdminService service,
                                UnidadeAdminService unidadeAdminService) {
        this.service = service;
        this.unidadeAdminService = unidadeAdminService;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) String q, Model model) {
        var items = service.listar(q);
        model.addAttribute("items", items);
        model.addAttribute("mapaModalidades", service.mapaModalidadesDescricao(items));
        model.addAttribute("q", q);
        return "pages/care/inpatient/admin/leitos/list";
    }

    @GetMapping("/cancelados")
    public String listarCancelados(@RequestParam(required = false) String q, Model model) {
        var items = service.listarCancelados(q);
        model.addAttribute("items", items);
        model.addAttribute("mapaModalidades", service.mapaModalidadesDescricao(items));
        model.addAttribute("q", q);
        return "pages/care/inpatient/admin/leitos/cancel_list";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new LeitoForm());
        }
        model.addAttribute("modoEdicao", false);
        populateModel(model, (LeitoForm) model.getAttribute("form"));
        return "pages/care/inpatient/admin/leitos/form";
    }

    @PostMapping
    public String criar(@Valid @ModelAttribute("form") LeitoForm form,
                        BindingResult bindingResult,
                        Model model,
                        RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("modoEdicao", false);
            populateModel(model, form);
            return "pages/care/inpatient/admin/leitos/form";
        }
        try {
            service.criar(form);
            redirectAttributes.addFlashAttribute("successMessage", "Leito cadastrado com sucesso");
            return "redirect:/ui/admin/leitos";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("modoEdicao", false);
            model.addAttribute("errorMessage", ex.getMessage());
            populateModel(model, form);
            return "pages/care/inpatient/admin/leitos/form";
        }
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        LeitoForm form = service.toForm(service.buscar(id));
        model.addAttribute("form", form);
        model.addAttribute("modoEdicao", true);
        model.addAttribute("itemId", id);
        populateModel(model, form);
        return "pages/care/inpatient/admin/leitos/form";
    }

    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id,
                            @Valid @ModelAttribute("form") LeitoForm form,
                            BindingResult bindingResult,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("modoEdicao", true);
            model.addAttribute("itemId", id);
            populateModel(model, form);
            return "pages/care/inpatient/admin/leitos/form";
        }
        try {
            service.atualizar(id, form);
            redirectAttributes.addFlashAttribute("successMessage", "Leito atualizado com sucesso");
            return "redirect:/ui/admin/leitos";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("modoEdicao", true);
            model.addAttribute("itemId", id);
            model.addAttribute("errorMessage", ex.getMessage());
            populateModel(model, form);
            return "pages/care/inpatient/admin/leitos/form";
        }
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        service.excluir(id);
        redirectAttributes.addFlashAttribute("successMessage", "Leito excluido com sucesso");
        return "redirect:/ui/admin/leitos";
    }

    @PostMapping("/{id}/restaurar")
    public String restaurar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            service.restaurar(id);
            redirectAttributes.addFlashAttribute("successMessage", "Leito restaurado com sucesso");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/ui/admin/leitos/cancelados";
    }

    @PostMapping("/{id}/excluir-permanente")
    public String excluirPermanente(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            service.excluirPermanente(id);
            redirectAttributes.addFlashAttribute("successMessage", "Leito excluido permanentemente com sucesso");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/ui/admin/leitos/cancelados";
    }

    private void populateModel(Model model, LeitoForm form) {
        model.addAttribute("unidades", unidadeAdminService.listar(null));
        var areas = service.listarAreasComLeito();
        model.addAttribute("areas", areas);
        var areaLegado = (form == null || form.getAreaId() == null)
                ? null
                : areas.stream().anyMatch(item -> item.getId().equals(form.getAreaId()))
                        ? null
                        : service.buscarAreaCanceladaOpcional(form.getAreaId()).orElse(null);
        model.addAttribute("areaLegado", areaLegado);
        model.addAttribute("tiposLeito", service.listarTiposAtivos());
        model.addAttribute("perfisLeito", service.listarPerfisAtivos());
        model.addAttribute("naturezasOperacionais", service.listarNaturezasOperacionaisAtivas());
        model.addAttribute("modalidadesLeito", service.listarModalidadesAtivas());
    }
}
