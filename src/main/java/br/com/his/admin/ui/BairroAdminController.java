package br.com.his.admin.ui;

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

import br.com.his.admin.dto.BairroForm;
import br.com.his.admin.service.BairroAdminService;
import br.com.his.admin.service.CidadeAdminService;
import br.com.his.admin.service.UnidadeFederativaAdminService;
import br.com.his.reference.location.model.Bairro;
import br.com.his.patient.dto.PacienteLookupOption;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/ui/admin/bairros")
public class BairroAdminController {

    private final BairroAdminService service;
    private final CidadeAdminService cidadeAdminService;
    private final UnidadeFederativaAdminService unidadeFederativaAdminService;

    public BairroAdminController(BairroAdminService service,
                                 CidadeAdminService cidadeAdminService,
                                 UnidadeFederativaAdminService unidadeFederativaAdminService) {
        this.service = service;
        this.cidadeAdminService = cidadeAdminService;
        this.unidadeFederativaAdminService = unidadeFederativaAdminService;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) String q, Model model) {
        model.addAttribute("items", service.listar(q));
        model.addAttribute("q", q);
        return "pages/admin/bairros/list";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new BairroForm());
        }
        populateModel(model, (BairroForm) model.getAttribute("form"));
        model.addAttribute("modoEdicao", false);
        return "pages/admin/bairros/form";
    }

    @PostMapping
    public String criar(@Valid @ModelAttribute("form") BairroForm form,
                        BindingResult bindingResult,
                        Model model,
                        RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            populateModel(model, form);
            model.addAttribute("modoEdicao", false);
            return "pages/admin/bairros/form";
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
        return "pages/admin/bairros/form";
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
            return "pages/admin/bairros/form";
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

    @GetMapping("/por-cidade/{cidadeId}")
    @ResponseBody
    public List<PacienteLookupOption> listarPorCidade(@PathVariable Long cidadeId) {
        return service.listarPorCidade(cidadeId)
                .stream()
                .filter(Bairro::isAtivo)
                .map(this::toOption)
                .toList();
    }

    private PacienteLookupOption toOption(Bairro bairro) {
        return new PacienteLookupOption(bairro.getId(), bairro.getNome());
    }

    private void populateModel(Model model, BairroForm form) {
        model.addAttribute("ufs", unidadeFederativaAdminService.listarTodas());
        Long unidadeFederativaId = form == null ? null : form.getUnidadeFederativaId();
        model.addAttribute("cidades", unidadeFederativaId == null
                ? java.util.List.of()
                : cidadeAdminService.listarPorUf(unidadeFederativaId));
    }
}
