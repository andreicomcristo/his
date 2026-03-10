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

import br.com.his.access.repository.PerfilRepository;
import br.com.his.access.repository.UnidadeRepository;
import br.com.his.access.dto.UsuarioNovoForm;
import br.com.his.access.dto.UsuarioVinculoForm;
import br.com.his.care.maintenance.service.AssistencialMaintenanceService;
import br.com.his.access.service.UsuarioAdminService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/ui/admin/usuarios")
public class UsuarioAdminController {

    private final UsuarioAdminService usuarioAdminService;
    private final AssistencialMaintenanceService assistencialMaintenanceService;
    private final UnidadeRepository unidadeRepository;
    private final PerfilRepository perfilRepository;

    public UsuarioAdminController(UsuarioAdminService usuarioAdminService,
                                  AssistencialMaintenanceService assistencialMaintenanceService,
                                  UnidadeRepository unidadeRepository,
                                  PerfilRepository perfilRepository) {
        this.usuarioAdminService = usuarioAdminService;
        this.assistencialMaintenanceService = assistencialMaintenanceService;
        this.unidadeRepository = unidadeRepository;
        this.perfilRepository = perfilRepository;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) String q, Model model) {
        model.addAttribute("usuarios", usuarioAdminService.listar(q));
        model.addAttribute("q", q);
        return "pages/admin/usuarios/list";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new UsuarioNovoForm());
        }
        return "pages/admin/usuarios/novo";
    }

    @PostMapping
    public String criar(@Valid @ModelAttribute("form") UsuarioNovoForm form,
                        BindingResult bindingResult,
                        Model model,
                        RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "pages/admin/usuarios/novo";
        }
        try {
            var usuario = usuarioAdminService.criarNoKeycloakERegistrarEspelho(form);
            redirectAttributes.addFlashAttribute("successMessage", "Usuario criado com sucesso");
            return "redirect:/ui/admin/usuarios/" + usuario.getId();
        } catch (IllegalArgumentException ex) {
            bindingResult.reject("usuario", ex.getMessage());
            model.addAttribute("form", form);
            return "pages/admin/usuarios/novo";
        }
    }

    @GetMapping("/{id}")
    public String detalhe(@PathVariable Long id, Model model) {
        model.addAttribute("usuario", usuarioAdminService.buscarPorId(id));
        model.addAttribute("vinculos", usuarioAdminService.listarVinculos(id));
        if (!model.containsAttribute("vinculoForm")) {
            model.addAttribute("vinculoForm", new UsuarioVinculoForm());
        }
        model.addAttribute("unidades", unidadeRepository.findAll());
        model.addAttribute("perfis", perfilRepository.findAll());
        return "pages/admin/usuarios/detail";
    }

    @PostMapping("/{id}/vinculos")
    public String adicionarVinculo(@PathVariable Long id,
                                   @Valid @ModelAttribute("vinculoForm") UsuarioVinculoForm vinculoForm,
                                   BindingResult bindingResult,
                                   RedirectAttributes redirectAttributes,
                                   Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("usuario", usuarioAdminService.buscarPorId(id));
            model.addAttribute("vinculos", usuarioAdminService.listarVinculos(id));
            model.addAttribute("unidades", unidadeRepository.findAll());
            model.addAttribute("perfis", perfilRepository.findAll());
            return "pages/admin/usuarios/detail";
        }
        try {
            usuarioAdminService.adicionarVinculo(id, vinculoForm);
            redirectAttributes.addFlashAttribute("successMessage", "Vinculo salvo com sucesso");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/ui/admin/usuarios/" + id;
    }

    @PostMapping("/{usuarioId}/vinculos/{vinculoId}/remover")
    public String removerVinculo(@PathVariable Long usuarioId,
                                 @PathVariable Long vinculoId,
                                 RedirectAttributes redirectAttributes) {
        try {
            usuarioAdminService.removerVinculo(vinculoId);
            redirectAttributes.addFlashAttribute("successMessage", "Vinculo removido");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/ui/admin/usuarios/" + usuarioId;
    }

    @PostMapping("/{usuarioId}/vinculos/{vinculoId}/perfil")
    public String trocarPerfil(@PathVariable Long usuarioId,
                               @PathVariable Long vinculoId,
                               @RequestParam Long perfilId,
                               RedirectAttributes redirectAttributes) {
        try {
            usuarioAdminService.atualizarPerfilVinculo(vinculoId, perfilId);
            redirectAttributes.addFlashAttribute("successMessage", "Perfil do vinculo atualizado");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/ui/admin/usuarios/" + usuarioId;
    }

    @PostMapping("/{id}/provisionar-keycloak")
    public String provisionarKeycloak(@PathVariable Long id,
                                      RedirectAttributes redirectAttributes) {
        try {
            String senhaTemporaria = usuarioAdminService.provisionarNoKeycloak(id);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Usuario provisionado no Keycloak. Senha temporaria: " + senhaTemporaria);
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/ui/admin/usuarios";
    }

    @PostMapping("/reset-fluxo-assistencial")
    public String resetFluxoAssistencial(RedirectAttributes redirectAttributes) {
        try {
            assistencialMaintenanceService.resetFluxoAssistencialSemPacientes();
            redirectAttributes.addFlashAttribute("successMessage",
                    "Fluxo assistencial resetado com sucesso (pacientes preservados).");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Falha ao resetar fluxo assistencial: " + ex.getMessage());
        }
        return "redirect:/ui/admin/usuarios";
    }
}
