package br.com.his.access.ui;

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

import br.com.his.access.dto.ColaboradorUnidadeVinculoForm;
import br.com.his.access.repository.UnidadeRepository;
import br.com.his.access.service.ColaboradorAdminService;
import br.com.his.access.service.ColaboradorUnidadeVinculoAdminService;
import br.com.his.access.service.TipoVinculoTrabalhistaAdminService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/ui/admin/colaboradores-vinculos")
public class ColaboradorUnidadeVinculoAdminController {

    private final ColaboradorUnidadeVinculoAdminService service;
    private final ColaboradorAdminService colaboradorAdminService;
    private final UnidadeRepository unidadeRepository;
    private final TipoVinculoTrabalhistaAdminService tipoVinculoTrabalhistaAdminService;

    public ColaboradorUnidadeVinculoAdminController(ColaboradorUnidadeVinculoAdminService service,
                                                    ColaboradorAdminService colaboradorAdminService,
                                                    UnidadeRepository unidadeRepository,
                                                    TipoVinculoTrabalhistaAdminService tipoVinculoTrabalhistaAdminService) {
        this.service = service;
        this.colaboradorAdminService = colaboradorAdminService;
        this.unidadeRepository = unidadeRepository;
        this.tipoVinculoTrabalhistaAdminService = tipoVinculoTrabalhistaAdminService;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) String q,
                         @RequestParam(required = false) Boolean ativo,
                         Model model) {
        model.addAttribute("items", service.listar(q, ativo));
        model.addAttribute("q", q);
        model.addAttribute("ativo", ativo);
        return "pages/access/admin/colaboradores-vinculos/list";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new ColaboradorUnidadeVinculoForm());
        }
        popularCombos(model);
        model.addAttribute("modoEdicao", false);
        return "pages/access/admin/colaboradores-vinculos/form";
    }

    @PostMapping
    public String criar(@Valid @ModelAttribute("form") ColaboradorUnidadeVinculoForm form,
                        BindingResult bindingResult,
                        Model model,
                        RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            popularCombos(model);
            model.addAttribute("modoEdicao", false);
            return "pages/access/admin/colaboradores-vinculos/form";
        }
        try {
            service.criar(form);
            redirectAttributes.addFlashAttribute("successMessage", "Vinculo cadastrado com sucesso");
            return "redirect:/ui/admin/colaboradores-vinculos";
        } catch (IllegalArgumentException ex) {
            popularCombos(model);
            model.addAttribute("modoEdicao", false);
            model.addAttribute("errorMessage", ex.getMessage());
            return "pages/access/admin/colaboradores-vinculos/form";
        }
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("form", service.toForm(service.buscar(id)));
        popularCombos(model);
        model.addAttribute("modoEdicao", true);
        model.addAttribute("itemId", id);
        return "pages/access/admin/colaboradores-vinculos/form";
    }

    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id,
                            @Valid @ModelAttribute("form") ColaboradorUnidadeVinculoForm form,
                            BindingResult bindingResult,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            popularCombos(model);
            model.addAttribute("modoEdicao", true);
            model.addAttribute("itemId", id);
            return "pages/access/admin/colaboradores-vinculos/form";
        }
        try {
            service.atualizar(id, form);
            redirectAttributes.addFlashAttribute("successMessage", "Vinculo atualizado com sucesso");
            return "redirect:/ui/admin/colaboradores-vinculos";
        } catch (IllegalArgumentException ex) {
            popularCombos(model);
            model.addAttribute("modoEdicao", true);
            model.addAttribute("itemId", id);
            model.addAttribute("errorMessage", ex.getMessage());
            return "pages/access/admin/colaboradores-vinculos/form";
        }
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id,
                          RedirectAttributes redirectAttributes) {
        try {
            service.excluir(id);
            redirectAttributes.addFlashAttribute("successMessage", "Vinculo excluido com sucesso");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/ui/admin/colaboradores-vinculos";
    }

    private void popularCombos(Model model) {
        model.addAttribute("colaboradores", colaboradorAdminService.listar(null, null));
        model.addAttribute("unidades", unidadeRepository.findAllByOrderByNomeAsc());
        model.addAttribute("tiposVinculoTrabalhista", tipoVinculoTrabalhistaAdminService.listar(null, true));
    }
}
