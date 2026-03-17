package br.com.his.reference.location.ui;

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

import br.com.his.reference.location.dto.BairroForm;
import br.com.his.reference.location.model.Municipio;
import br.com.his.reference.location.service.BairroAdminService;
import br.com.his.reference.location.service.MunicipioAdminService;
import br.com.his.reference.location.service.UnidadeFederativaAdminService;
import br.com.his.reference.location.model.Bairro;
import br.com.his.patient.dto.PacienteLookupOption;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/ui/admin/bairros")
public class BairroAdminController {

    private final BairroAdminService service;
    private final MunicipioAdminService MunicipioAdminService;
    private final UnidadeFederativaAdminService unidadeFederativaAdminService;

    public BairroAdminController(BairroAdminService service,
                                 MunicipioAdminService MunicipioAdminService,
                                 UnidadeFederativaAdminService unidadeFederativaAdminService) {
        this.service = service;
        this.MunicipioAdminService = MunicipioAdminService;
        this.unidadeFederativaAdminService = unidadeFederativaAdminService;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) String q, Model model) {
        model.addAttribute("items", service.listar(q));
        model.addAttribute("q", q);
        return "pages/reference/location/admin/bairros/list";
    }

    @GetMapping("/cancelados")
    public String listarCancelados(@RequestParam(required = false) String q, Model model) {
        model.addAttribute("items", service.listarCancelados(q));
        model.addAttribute("q", q);
        return "pages/reference/location/admin/bairros/cancel_list";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new BairroForm());
        }
        populateModel(model, (BairroForm) model.getAttribute("form"));
        model.addAttribute("modoEdicao", false);
        return "pages/reference/location/admin/bairros/form";
    }

    @PostMapping
    public String criar(@Valid @ModelAttribute("form") BairroForm form,
                        BindingResult bindingResult,
                        Model model,
                        RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            populateModel(model, form);
            model.addAttribute("modoEdicao", false);
            return "pages/reference/location/admin/bairros/form";
        }
        service.criar(form);
        redirectAttributes.addFlashAttribute("successMessage", "Bairro cadastrado com sucesso");
        return "redirect:/ui/admin/bairros";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        BairroForm form = service.toForm(service.buscar(id));
        model.addAttribute("form", form);
        populateModel(model, form);
        model.addAttribute("modoEdicao", true);
        model.addAttribute("itemId", id);
        return "pages/reference/location/admin/bairros/form";
    }

    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id,
                            @Valid @ModelAttribute("form") BairroForm form,
                            BindingResult bindingResult,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            populateModel(model, form);
            model.addAttribute("modoEdicao", true);
            model.addAttribute("itemId", id);
            return "pages/reference/location/admin/bairros/form";
        }
        service.atualizar(id, form);
        redirectAttributes.addFlashAttribute("successMessage", "Bairro atualizado com sucesso");
        return "redirect:/ui/admin/bairros";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        service.excluir(id);
        redirectAttributes.addFlashAttribute("successMessage", "Bairro excluido com sucesso");
        return "redirect:/ui/admin/bairros";
    }

    @PostMapping("/{id}/restaurar")
    public String restaurar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            service.restaurar(id);
            redirectAttributes.addFlashAttribute("successMessage", "Bairro restaurado com sucesso");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/ui/admin/bairros/cancelados";
    }

    @PostMapping("/{id}/excluir-permanente")
    public String excluirPermanente(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            service.excluirPermanente(id);
            redirectAttributes.addFlashAttribute("successMessage", "Bairro excluido permanentemente com sucesso");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/ui/admin/bairros/cancelados";
    }

    @GetMapping("/por-municipio/{municipioId}")
    @ResponseBody
    public List<PacienteLookupOption> listarPorMunicipio(@PathVariable Long municipioId) {
        return service.listarPorMunicipio(municipioId)
                .stream()
                .map(this::toOption)
                .toList();
    }

    private PacienteLookupOption toOption(Bairro bairro) {
        return new PacienteLookupOption(bairro.getId(), bairro.getNome());
    }

    private void populateModel(Model model, BairroForm form) {
        var ufs = unidadeFederativaAdminService.listarTodas();
        model.addAttribute("ufs", ufs);
        var ufLegado = (form == null || form.getUnidadeFederativaId() == null)
                ? null
                : ufs.stream().anyMatch(uf -> uf.getId().equals(form.getUnidadeFederativaId()))
                        ? null
                        : unidadeFederativaAdminService.buscarCanceladaOpcional(form.getUnidadeFederativaId()).orElse(null);
        model.addAttribute("ufLegado", ufLegado);
        Long unidadeFederativaId = form == null ? null : form.getUnidadeFederativaId();
        java.util.List<Municipio> municipios = unidadeFederativaId == null
                ? java.util.List.of()
                : MunicipioAdminService.listarPorUf(unidadeFederativaId);
        model.addAttribute("municipios", municipios);
        var municipioLegado = (form == null || form.getMunicipioId() == null)
                ? null
                : municipios.stream().anyMatch(item -> item.getId().equals(form.getMunicipioId()))
                        ? null
                        : MunicipioAdminService.buscarCanceladoOpcional(form.getMunicipioId()).orElse(null);
        model.addAttribute("municipioLegado", municipioLegado);
    }
}

