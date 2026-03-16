package br.com.his.patient.ui;

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

import br.com.his.patient.dto.PacienteCatalogoForm;
import br.com.his.patient.dto.PacienteCatalogoTipo;
import br.com.his.patient.service.PacienteCatalogoAdminService;
import br.com.his.access.service.UnidadeAdminService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/ui/admin/paciente-catalogos")
public class PacienteCatalogoAdminController {

    private final PacienteCatalogoAdminService pacienteCatalogoAdminService;
    private final UnidadeAdminService unidadeAdminService;

    public PacienteCatalogoAdminController(PacienteCatalogoAdminService pacienteCatalogoAdminService,
                                           UnidadeAdminService unidadeAdminService) {
        this.pacienteCatalogoAdminService = pacienteCatalogoAdminService;
        this.unidadeAdminService = unidadeAdminService;
    }

    @GetMapping
    public String index(Model model) {
        model.addAttribute("catalogos", PacienteCatalogoTipo.visiveis());
        return "pages/patient/admin/paciente-catalogos/index";
    }

    @GetMapping("/{tipo}")
    public String listar(@PathVariable String tipo,
                         @RequestParam(required = false) String q,
                         Model model) {
        PacienteCatalogoTipo catalogo = PacienteCatalogoTipo.fromSlug(tipo);
        if (catalogo == PacienteCatalogoTipo.PROCEDENCIA) {
            return "redirect:/ui/admin/procedencias";
        }
        model.addAttribute("catalogo", catalogo);
        model.addAttribute("items", pacienteCatalogoAdminService.listar(catalogo, q));
        model.addAttribute("q", q);
        return "pages/patient/admin/paciente-catalogos/list";
    }

    @GetMapping("/{tipo}/novo")
    public String novo(@PathVariable String tipo, Model model) {
        PacienteCatalogoTipo catalogo = PacienteCatalogoTipo.fromSlug(tipo);
        if (catalogo == PacienteCatalogoTipo.PROCEDENCIA) {
            return "redirect:/ui/admin/procedencias";
        }
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new PacienteCatalogoForm());
        }
        populateModel(model, catalogo);
        model.addAttribute("modoEdicao", false);
        return "pages/patient/admin/paciente-catalogos/form";
    }

    @PostMapping("/{tipo}")
    public String criar(@PathVariable String tipo,
                        @Valid @ModelAttribute("form") PacienteCatalogoForm form,
                        BindingResult bindingResult,
                        Model model,
                        RedirectAttributes redirectAttributes) {
        PacienteCatalogoTipo catalogo = PacienteCatalogoTipo.fromSlug(tipo);
        if (catalogo == PacienteCatalogoTipo.PROCEDENCIA) {
            return "redirect:/ui/admin/procedencias";
        }
        if (bindingResult.hasErrors()) {
            populateModel(model, catalogo);
            model.addAttribute("modoEdicao", false);
            return "pages/patient/admin/paciente-catalogos/form";
        }
        try {
            pacienteCatalogoAdminService.criar(catalogo, form);
            redirectAttributes.addFlashAttribute("successMessage", catalogo.getTitulo() + " cadastrado(a) com sucesso");
            return "redirect:/ui/admin/paciente-catalogos/" + catalogo.getSlug();
        } catch (IllegalArgumentException ex) {
            bindingResult.reject("catalogo", ex.getMessage());
            populateModel(model, catalogo);
            model.addAttribute("modoEdicao", false);
            return "pages/patient/admin/paciente-catalogos/form";
        }
    }

    @GetMapping("/{tipo}/{id}/editar")
    public String editar(@PathVariable String tipo, @PathVariable Long id, Model model) {
        PacienteCatalogoTipo catalogo = PacienteCatalogoTipo.fromSlug(tipo);
        if (catalogo == PacienteCatalogoTipo.PROCEDENCIA) {
            return "redirect:/ui/admin/procedencias/" + id + "/editar";
        }
        model.addAttribute("form", pacienteCatalogoAdminService.buscarFormulario(catalogo, id));
        populateModel(model, catalogo);
        model.addAttribute("modoEdicao", true);
        model.addAttribute("itemId", id);
        return "pages/patient/admin/paciente-catalogos/form";
    }

    @PostMapping("/{tipo}/{id}")
    public String atualizar(@PathVariable String tipo,
                            @PathVariable Long id,
                            @Valid @ModelAttribute("form") PacienteCatalogoForm form,
                            BindingResult bindingResult,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        PacienteCatalogoTipo catalogo = PacienteCatalogoTipo.fromSlug(tipo);
        if (catalogo == PacienteCatalogoTipo.PROCEDENCIA) {
            return "redirect:/ui/admin/procedencias/" + id + "/editar";
        }
        if (bindingResult.hasErrors()) {
            populateModel(model, catalogo);
            model.addAttribute("modoEdicao", true);
            model.addAttribute("itemId", id);
            return "pages/patient/admin/paciente-catalogos/form";
        }
        try {
            pacienteCatalogoAdminService.atualizar(catalogo, id, form);
            redirectAttributes.addFlashAttribute("successMessage", catalogo.getTitulo() + " atualizado(a) com sucesso");
            return "redirect:/ui/admin/paciente-catalogos/" + catalogo.getSlug();
        } catch (IllegalArgumentException ex) {
            bindingResult.reject("catalogo", ex.getMessage());
            populateModel(model, catalogo);
            model.addAttribute("modoEdicao", true);
            model.addAttribute("itemId", id);
            return "pages/patient/admin/paciente-catalogos/form";
        }
    }

    @PostMapping("/{tipo}/{id}/excluir")
    public String excluir(@PathVariable String tipo,
                          @PathVariable Long id,
                          RedirectAttributes redirectAttributes) {
        PacienteCatalogoTipo catalogo = PacienteCatalogoTipo.fromSlug(tipo);
        if (catalogo == PacienteCatalogoTipo.PROCEDENCIA) {
            return "redirect:/ui/admin/procedencias";
        }
        try {
            pacienteCatalogoAdminService.excluir(catalogo, id);
            redirectAttributes.addFlashAttribute("successMessage", catalogo.getTitulo() + " excluído(a) com sucesso");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/ui/admin/paciente-catalogos/" + catalogo.getSlug();
    }

    private void populateModel(Model model, PacienteCatalogoTipo catalogo) {
        model.addAttribute("catalogo", catalogo);
        if (catalogo.isUsaTipoProcedencia()) {
            model.addAttribute("tiposProcedencia", pacienteCatalogoAdminService.listarTiposProcedencia());
            model.addAttribute("unidades", unidadeAdminService.listar(null));
        }
    }
}
