package br.com.his.patient.ui;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.his.patient.dto.PacienteLookupOption;
import br.com.his.patient.dto.ProcedenciaAdminForm;
import br.com.his.patient.model.lookup.Procedencia;
import br.com.his.patient.service.ProcedenciaAdminService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/ui/admin/procedencias")
public class ProcedenciaAdminController {

    private final ProcedenciaAdminService service;

    public ProcedenciaAdminController(ProcedenciaAdminService service) {
        this.service = service;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) String q,
                         @RequestParam(required = false) Boolean ativo,
                         Model model) {
        model.addAttribute("items", service.listar(q, ativo));
        model.addAttribute("q", q);
        model.addAttribute("ativo", ativo);
        return "pages/patient/admin/procedencias/list";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new ProcedenciaAdminForm());
        }
        ProcedenciaAdminForm form = (ProcedenciaAdminForm) model.getAttribute("form");
        popularCombos(model, form);
        model.addAttribute("modoEdicao", false);
        return "pages/patient/admin/procedencias/form";
    }

    @PostMapping
    public String criar(@Valid @ModelAttribute("form") ProcedenciaAdminForm form,
                        BindingResult bindingResult,
                        Model model,
                        RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            popularCombos(model, form);
            model.addAttribute("modoEdicao", false);
            return "pages/patient/admin/procedencias/form";
        }
        try {
            service.criar(form);
            redirectAttributes.addFlashAttribute("successMessage", "Procedencia cadastrada com sucesso");
            return "redirect:/ui/admin/procedencias";
        } catch (IllegalArgumentException ex) {
            popularCombos(model, form);
            model.addAttribute("modoEdicao", false);
            model.addAttribute("errorMessage", ex.getMessage());
            return "pages/patient/admin/procedencias/form";
        }
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        Procedencia item = service.buscar(id);
        ProcedenciaAdminForm form = service.toForm(item);
        model.addAttribute("form", form);
        popularCombos(model, form);
        model.addAttribute("modoEdicao", true);
        model.addAttribute("itemId", id);
        return "pages/patient/admin/procedencias/form";
    }

    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id,
                            @Valid @ModelAttribute("form") ProcedenciaAdminForm form,
                            BindingResult bindingResult,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            popularCombos(model, form);
            model.addAttribute("modoEdicao", true);
            model.addAttribute("itemId", id);
            return "pages/patient/admin/procedencias/form";
        }
        try {
            service.atualizar(id, form);
            redirectAttributes.addFlashAttribute("successMessage", "Procedencia atualizada com sucesso");
            return "redirect:/ui/admin/procedencias";
        } catch (IllegalArgumentException ex) {
            popularCombos(model, form);
            model.addAttribute("modoEdicao", true);
            model.addAttribute("itemId", id);
            model.addAttribute("errorMessage", ex.getMessage());
            return "pages/patient/admin/procedencias/form";
        }
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            service.excluir(id);
            redirectAttributes.addFlashAttribute("successMessage", "Procedencia excluida com sucesso");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/ui/admin/procedencias";
    }

    @GetMapping("/municipios-por-uf/{unidadeFederativaId}")
    @ResponseBody
    public List<PacienteLookupOption> listarMunicipiosPorUf(@PathVariable Long unidadeFederativaId) {
        return service.listarMunicipiosPorUf(unidadeFederativaId).stream()
                .map(item -> new PacienteLookupOption(item.getId(), item.getNome()))
                .toList();
    }

    @GetMapping("/bairros-por-municipio/{municipioId}")
    @ResponseBody
    public List<PacienteLookupOption> listarBairrosPorMunicipio(@PathVariable Long municipioId) {
        return service.listarBairrosPorMunicipio(municipioId).stream()
                .map(item -> new PacienteLookupOption(item.getId(), item.getNome()))
                .toList();
    }

    private void popularCombos(Model model, ProcedenciaAdminForm form) {
        model.addAttribute("tiposProcedencia", service.listarTiposProcedencia());
        model.addAttribute("camposPorTipoId", service.mapearCampoPorTipo());
        model.addAttribute("unidades", service.listarUnidadesAtivas());
        model.addAttribute("ufs", service.listarUfs());
        model.addAttribute("municipios", service.listarMunicipiosPorUf(form == null ? null : form.getUnidadeFederativaId()));
        model.addAttribute("bairros", service.listarBairrosPorMunicipio(form == null ? null : form.getMunicipioId()));
    }
}
