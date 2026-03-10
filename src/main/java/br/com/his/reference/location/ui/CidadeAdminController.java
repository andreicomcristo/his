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

import br.com.his.reference.location.dto.CidadeForm;
import br.com.his.reference.location.service.CidadeAdminService;
import br.com.his.reference.location.service.UnidadeFederativaAdminService;
import br.com.his.reference.location.model.Cidade;
import br.com.his.patient.dto.PacienteLookupOption;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/ui/admin/cidades")
public class CidadeAdminController {

    private final CidadeAdminService service;
    private final UnidadeFederativaAdminService unidadeFederativaAdminService;

    public CidadeAdminController(CidadeAdminService service, UnidadeFederativaAdminService unidadeFederativaAdminService) {
        this.service = service;
        this.unidadeFederativaAdminService = unidadeFederativaAdminService;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) String q, Model model) {
        model.addAttribute("items", service.listar(q));
        model.addAttribute("q", q);
        return "pages/reference/location/admin/cidades/list";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new CidadeForm());
        }
        populateModel(model);
        model.addAttribute("modoEdicao", false);
        return "pages/reference/location/admin/cidades/form";
    }

    @PostMapping
    public String criar(@Valid @ModelAttribute("form") CidadeForm form,
                        BindingResult bindingResult,
                        Model model,
                        RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            populateModel(model);
            model.addAttribute("modoEdicao", false);
            return "pages/reference/location/admin/cidades/form";
        }
        service.criar(form);
        redirectAttributes.addFlashAttribute("successMessage", "Cidade cadastrada com sucesso");
        return "redirect:/ui/admin/cidades";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("form", service.toForm(service.buscar(id)));
        populateModel(model);
        model.addAttribute("modoEdicao", true);
        model.addAttribute("itemId", id);
        return "pages/reference/location/admin/cidades/form";
    }

    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id,
                            @Valid @ModelAttribute("form") CidadeForm form,
                            BindingResult bindingResult,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            populateModel(model);
            model.addAttribute("modoEdicao", true);
            model.addAttribute("itemId", id);
            return "pages/reference/location/admin/cidades/form";
        }
        service.atualizar(id, form);
        redirectAttributes.addFlashAttribute("successMessage", "Cidade atualizada com sucesso");
        return "redirect:/ui/admin/cidades";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        service.excluir(id);
        redirectAttributes.addFlashAttribute("successMessage", "Cidade excluida com sucesso");
        return "redirect:/ui/admin/cidades";
    }

    @GetMapping("/por-uf/{unidadeFederativaId}")
    @ResponseBody
    public List<PacienteLookupOption> listarPorUf(@PathVariable Long unidadeFederativaId) {
        return service.listarPorUf(unidadeFederativaId)
                .stream()
                .map(this::toOption)
                .toList();
    }

    private PacienteLookupOption toOption(Cidade cidade) {
        return new PacienteLookupOption(cidade.getId(), cidade.getNome());
    }

    private void populateModel(Model model) {
        model.addAttribute("ufs", unidadeFederativaAdminService.listarTodas());
    }
}
