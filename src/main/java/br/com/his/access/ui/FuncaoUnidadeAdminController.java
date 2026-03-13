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

import br.com.his.access.dto.FuncaoUnidadeForm;
import br.com.his.access.model.TipoNaturezaAtuacao;
import br.com.his.access.service.FuncaoUnidadeAdminService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/ui/admin/funcoes-unidade")
public class FuncaoUnidadeAdminController {

    private final FuncaoUnidadeAdminService service;

    public FuncaoUnidadeAdminController(FuncaoUnidadeAdminService service) {
        this.service = service;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) String q,
                         @RequestParam(required = false) Boolean ativo,
                         Model model) {
        model.addAttribute("items", service.listar(q, ativo));
        model.addAttribute("q", q);
        model.addAttribute("ativo", ativo);
        return "pages/access/admin/funcoes-unidade/list";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new FuncaoUnidadeForm());
        }
        model.addAttribute("tipos", TipoNaturezaAtuacao.values());
        model.addAttribute("modoEdicao", false);
        return "pages/access/admin/funcoes-unidade/form";
    }

    @PostMapping
    public String criar(@Valid @ModelAttribute("form") FuncaoUnidadeForm form,
                        BindingResult bindingResult,
                        Model model,
                        RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("tipos", TipoNaturezaAtuacao.values());
            model.addAttribute("modoEdicao", false);
            return "pages/access/admin/funcoes-unidade/form";
        }
        try {
            service.criar(form);
            redirectAttributes.addFlashAttribute("successMessage", "Funcao cadastrada com sucesso");
            return "redirect:/ui/admin/funcoes-unidade";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("tipos", TipoNaturezaAtuacao.values());
            model.addAttribute("modoEdicao", false);
            model.addAttribute("errorMessage", ex.getMessage());
            return "pages/access/admin/funcoes-unidade/form";
        }
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("form", service.toForm(service.buscar(id)));
        model.addAttribute("tipos", TipoNaturezaAtuacao.values());
        model.addAttribute("modoEdicao", true);
        model.addAttribute("itemId", id);
        return "pages/access/admin/funcoes-unidade/form";
    }

    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id,
                            @Valid @ModelAttribute("form") FuncaoUnidadeForm form,
                            BindingResult bindingResult,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("tipos", TipoNaturezaAtuacao.values());
            model.addAttribute("modoEdicao", true);
            model.addAttribute("itemId", id);
            return "pages/access/admin/funcoes-unidade/form";
        }
        try {
            service.atualizar(id, form);
            redirectAttributes.addFlashAttribute("successMessage", "Funcao atualizada com sucesso");
            return "redirect:/ui/admin/funcoes-unidade";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("tipos", TipoNaturezaAtuacao.values());
            model.addAttribute("modoEdicao", true);
            model.addAttribute("itemId", id);
            model.addAttribute("errorMessage", ex.getMessage());
            return "pages/access/admin/funcoes-unidade/form";
        }
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id,
                          RedirectAttributes redirectAttributes) {
        try {
            service.excluir(id);
            redirectAttributes.addFlashAttribute("successMessage", "Funcao excluida com sucesso");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/ui/admin/funcoes-unidade";
    }
}
