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

import br.com.his.access.dto.ColaboradorUnidadeAtuacaoForm;
import br.com.his.access.repository.FuncaoUnidadeRepository;
import br.com.his.access.repository.PerfilRepository;
import br.com.his.access.service.ColaboradorUnidadeAtuacaoAdminService;
import br.com.his.access.service.ColaboradorUnidadeVinculoAdminService;
import br.com.his.care.scheduling.service.EspecialidadeAdminService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/ui/admin/colaboradores-atuacoes")
public class ColaboradorUnidadeAtuacaoAdminController {

    private final ColaboradorUnidadeAtuacaoAdminService service;
    private final ColaboradorUnidadeVinculoAdminService vinculoAdminService;
    private final FuncaoUnidadeRepository funcaoUnidadeRepository;
    private final PerfilRepository perfilRepository;
    private final EspecialidadeAdminService especialidadeAdminService;

    public ColaboradorUnidadeAtuacaoAdminController(ColaboradorUnidadeAtuacaoAdminService service,
                                                    ColaboradorUnidadeVinculoAdminService vinculoAdminService,
                                                    FuncaoUnidadeRepository funcaoUnidadeRepository,
                                                    PerfilRepository perfilRepository,
                                                    EspecialidadeAdminService especialidadeAdminService) {
        this.service = service;
        this.vinculoAdminService = vinculoAdminService;
        this.funcaoUnidadeRepository = funcaoUnidadeRepository;
        this.perfilRepository = perfilRepository;
        this.especialidadeAdminService = especialidadeAdminService;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) String q,
                         @RequestParam(required = false) Boolean ativo,
                         Model model) {
        model.addAttribute("items", service.listar(q, ativo));
        model.addAttribute("q", q);
        model.addAttribute("ativo", ativo);
        return "pages/access/admin/colaboradores-atuacoes/list";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new ColaboradorUnidadeAtuacaoForm());
        }
        popularCombos(model);
        model.addAttribute("modoEdicao", false);
        return "pages/access/admin/colaboradores-atuacoes/form";
    }

    @PostMapping
    public String criar(@Valid @ModelAttribute("form") ColaboradorUnidadeAtuacaoForm form,
                        BindingResult bindingResult,
                        Model model,
                        RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            popularCombos(model);
            model.addAttribute("modoEdicao", false);
            return "pages/access/admin/colaboradores-atuacoes/form";
        }
        try {
            service.criar(form);
            redirectAttributes.addFlashAttribute("successMessage", "Atuacao cadastrada com sucesso");
            return "redirect:/ui/admin/colaboradores-atuacoes";
        } catch (IllegalArgumentException ex) {
            popularCombos(model);
            model.addAttribute("modoEdicao", false);
            model.addAttribute("errorMessage", ex.getMessage());
            return "pages/access/admin/colaboradores-atuacoes/form";
        }
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("form", service.toForm(service.buscar(id)));
        popularCombos(model);
        model.addAttribute("modoEdicao", true);
        model.addAttribute("itemId", id);
        return "pages/access/admin/colaboradores-atuacoes/form";
    }

    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id,
                            @Valid @ModelAttribute("form") ColaboradorUnidadeAtuacaoForm form,
                            BindingResult bindingResult,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            popularCombos(model);
            model.addAttribute("modoEdicao", true);
            model.addAttribute("itemId", id);
            return "pages/access/admin/colaboradores-atuacoes/form";
        }
        try {
            service.atualizar(id, form);
            redirectAttributes.addFlashAttribute("successMessage", "Atuacao atualizada com sucesso");
            return "redirect:/ui/admin/colaboradores-atuacoes";
        } catch (IllegalArgumentException ex) {
            popularCombos(model);
            model.addAttribute("modoEdicao", true);
            model.addAttribute("itemId", id);
            model.addAttribute("errorMessage", ex.getMessage());
            return "pages/access/admin/colaboradores-atuacoes/form";
        }
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id,
                          RedirectAttributes redirectAttributes) {
        try {
            service.excluir(id);
            redirectAttributes.addFlashAttribute("successMessage", "Atuacao excluida com sucesso");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/ui/admin/colaboradores-atuacoes";
    }

    private void popularCombos(Model model) {
        model.addAttribute("vinculos", vinculoAdminService.listar(null, null));
        model.addAttribute("funcoes", funcaoUnidadeRepository.findAllByOrderByDescricaoAsc());
        model.addAttribute("especialidades", especialidadeAdminService.listar(null, null));
        model.addAttribute("perfis", perfilRepository.findAllByOrderByNomeAsc());
    }
}
