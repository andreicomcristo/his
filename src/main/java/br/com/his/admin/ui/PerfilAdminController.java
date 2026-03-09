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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.his.admin.dto.PerfilForm;
import br.com.his.admin.service.PerfilAdminService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/ui/admin/perfis")
public class PerfilAdminController {

    private final PerfilAdminService perfilAdminService;

    public PerfilAdminController(PerfilAdminService perfilAdminService) {
        this.perfilAdminService = perfilAdminService;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) String q, Model model) {
        model.addAttribute("perfis", perfilAdminService.listar(q));
        model.addAttribute("q", q);
        return "pages/admin/perfis/list";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new PerfilForm());
        }
        model.addAttribute("modoEdicao", false);
        return "pages/admin/perfis/form";
    }

    @PostMapping
    public String criar(@Valid @ModelAttribute("form") PerfilForm form,
                        BindingResult bindingResult,
                        Model model,
                        RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("modoEdicao", false);
            return "pages/admin/perfis/form";
        }
        try {
            perfilAdminService.criar(form);
            redirectAttributes.addFlashAttribute("successMessage", "Perfil criado");
            return "redirect:/ui/admin/perfis";
        } catch (IllegalArgumentException ex) {
            bindingResult.reject("perfil", ex.getMessage());
            model.addAttribute("modoEdicao", false);
            return "pages/admin/perfis/form";
        }
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        var perfil = perfilAdminService.buscarPorId(id);
        PerfilForm form = new PerfilForm();
        form.setNome(perfil.getNome());
        model.addAttribute("form", form);
        model.addAttribute("modoEdicao", true);
        model.addAttribute("perfilId", id);
        return "pages/admin/perfis/form";
    }

    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id,
                            @Valid @ModelAttribute("form") PerfilForm form,
                            BindingResult bindingResult,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("modoEdicao", true);
            model.addAttribute("perfilId", id);
            return "pages/admin/perfis/form";
        }
        try {
            perfilAdminService.atualizar(id, form);
            redirectAttributes.addFlashAttribute("successMessage", "Perfil atualizado");
            return "redirect:/ui/admin/perfis";
        } catch (IllegalArgumentException ex) {
            bindingResult.reject("perfil", ex.getMessage());
            model.addAttribute("modoEdicao", true);
            model.addAttribute("perfilId", id);
            return "pages/admin/perfis/form";
        }
    }

    @PostMapping("/{id}/remover")
    public String remover(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            perfilAdminService.remover(id);
            redirectAttributes.addFlashAttribute("successMessage", "Perfil removido");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/ui/admin/perfis";
    }

    @GetMapping("/{id}/permissoes")
    public String editarPermissoes(@PathVariable Long id, Model model) {
        model.addAttribute("perfil", perfilAdminService.buscarPorId(id));
        model.addAttribute("permissoes", perfilAdminService.listarPermissoes());
        model.addAttribute("selectedIds", perfilAdminService.listarIdsPermissoesPerfil(id));
        return "pages/admin/perfis/permissoes";
    }

    @PostMapping("/{id}/permissoes")
    public String atualizarPermissoes(@PathVariable Long id,
                                      @RequestParam(required = false) List<Long> permissaoIds,
                                      RedirectAttributes redirectAttributes) {
        try {
            perfilAdminService.atualizarPermissoes(id, permissaoIds);
            redirectAttributes.addFlashAttribute("successMessage", "Permissoes atualizadas");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/ui/admin/perfis/" + id + "/permissoes";
    }
}
