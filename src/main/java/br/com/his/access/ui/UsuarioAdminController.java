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

import br.com.his.access.dto.UsuarioAtuacaoForm;
import br.com.his.access.dto.UsuarioEdicaoForm;
import br.com.his.access.dto.UsuarioNovoForm;
import br.com.his.access.repository.FuncaoUnidadeRepository;
import br.com.his.access.repository.PerfilRepository;
import br.com.his.access.repository.UnidadeRepository;
import br.com.his.care.maintenance.service.AssistencialMaintenanceService;
import br.com.his.care.scheduling.service.EspecialidadeAdminService;
import br.com.his.access.service.UsuarioAdminService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/ui/admin/usuarios")
public class UsuarioAdminController {

    private final UsuarioAdminService usuarioAdminService;
    private final AssistencialMaintenanceService assistencialMaintenanceService;
    private final UnidadeRepository unidadeRepository;
    private final FuncaoUnidadeRepository funcaoUnidadeRepository;
    private final PerfilRepository perfilRepository;
    private final EspecialidadeAdminService especialidadeAdminService;

    public UsuarioAdminController(UsuarioAdminService usuarioAdminService,
                                  AssistencialMaintenanceService assistencialMaintenanceService,
                                  UnidadeRepository unidadeRepository,
                                  FuncaoUnidadeRepository funcaoUnidadeRepository,
                                  PerfilRepository perfilRepository,
                                  EspecialidadeAdminService especialidadeAdminService) {
        this.usuarioAdminService = usuarioAdminService;
        this.assistencialMaintenanceService = assistencialMaintenanceService;
        this.unidadeRepository = unidadeRepository;
        this.funcaoUnidadeRepository = funcaoUnidadeRepository;
        this.perfilRepository = perfilRepository;
        this.especialidadeAdminService = especialidadeAdminService;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) String q, Model model) {
        model.addAttribute("usuarios", usuarioAdminService.listar(q));
        model.addAttribute("q", q);
        return "pages/access/admin/usuarios/list";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new UsuarioNovoForm());
        }
        return "pages/access/admin/usuarios/novo";
    }

    @PostMapping
    public String criar(@Valid @ModelAttribute("form") UsuarioNovoForm form,
                        BindingResult bindingResult,
                        Model model,
                        RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "pages/access/admin/usuarios/novo";
        }
        try {
            var usuario = usuarioAdminService.criarNoKeycloakERegistrarEspelho(form);
            redirectAttributes.addFlashAttribute("successMessage", "Usuario criado com sucesso");
            return "redirect:/ui/admin/usuarios/" + usuario.getId();
        } catch (IllegalArgumentException ex) {
            bindingResult.reject("usuario", ex.getMessage());
            model.addAttribute("form", form);
            return "pages/access/admin/usuarios/novo";
        }
    }

    @GetMapping("/{id}")
    public String detalhe(@PathVariable Long id, Model model) {
        popularTelaDetalhe(id, model);
        return "pages/access/admin/usuarios/detail";
    }

    @PostMapping("/{id}/dados")
    public String atualizarDados(@PathVariable Long id,
                                 @Valid @ModelAttribute("usuarioForm") UsuarioEdicaoForm usuarioForm,
                                 BindingResult bindingResult,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            popularTelaDetalhe(id, model);
            if (!model.containsAttribute("atuacaoForm")) {
                model.addAttribute("atuacaoForm", new UsuarioAtuacaoForm());
            }
            return "pages/access/admin/usuarios/detail";
        }
        try {
            usuarioAdminService.atualizarDados(id, usuarioForm);
            redirectAttributes.addFlashAttribute("successMessage", "Dados do usuario atualizados");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/ui/admin/usuarios/" + id;
    }

    @PostMapping("/{id}/colaborador")
    public String atualizarVinculoColaborador(@PathVariable Long id,
                                              @RequestParam(required = false) Long colaboradorId,
                                              RedirectAttributes redirectAttributes) {
        try {
            usuarioAdminService.atualizarVinculoColaborador(id, colaboradorId);
            redirectAttributes.addFlashAttribute("successMessage", "Vinculo usuario-colaborador atualizado");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/ui/admin/usuarios/" + id;
    }

    @PostMapping("/{id}/atuacoes")
    public String adicionarAtuacao(@PathVariable Long id,
                                   @Valid @ModelAttribute("atuacaoForm") UsuarioAtuacaoForm atuacaoForm,
                                   BindingResult bindingResult,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            popularTelaDetalhe(id, model);
            if (!model.containsAttribute("usuarioForm")) {
                model.addAttribute("usuarioForm", usuarioAdminService.toEdicaoForm(id));
            }
            return "pages/access/admin/usuarios/detail";
        }
        try {
            usuarioAdminService.adicionarAtuacaoDoUsuario(id, atuacaoForm);
            redirectAttributes.addFlashAttribute("successMessage", "Atuacao adicionada com sucesso");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/ui/admin/usuarios/" + id;
    }

    @PostMapping("/{id}/atuacoes/{atuacaoId}/remover")
    public String removerAtuacao(@PathVariable Long id,
                                 @PathVariable Long atuacaoId,
                                 RedirectAttributes redirectAttributes) {
        try {
            usuarioAdminService.removerAtuacaoDoUsuario(id, atuacaoId);
            redirectAttributes.addFlashAttribute("successMessage", "Atuacao removida");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/ui/admin/usuarios/" + id;
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

    @PostMapping("/reset-fluxo-agendamento")
    public String resetFluxoAgendamento(RedirectAttributes redirectAttributes) {
        try {
            assistencialMaintenanceService.resetFluxoAgendamento();
            redirectAttributes.addFlashAttribute("successMessage",
                    "Fluxo de agendamento resetado com sucesso (especialidades preservadas).");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Falha ao resetar fluxo de agendamento: " + ex.getMessage());
        }
        return "redirect:/ui/admin/usuarios";
    }

    private void popularTelaDetalhe(Long usuarioId, Model model) {
        model.addAttribute("usuario", usuarioAdminService.buscarPorId(usuarioId));
        model.addAttribute("vinculoColaborador", usuarioAdminService.buscarVinculoColaborador(usuarioId).orElse(null));
        model.addAttribute("colaboradores", usuarioAdminService.listarColaboradoresParaVinculo(usuarioId));
        model.addAttribute("atuacoes", usuarioAdminService.listarAtuacoesDoUsuario(usuarioId));
        model.addAttribute("unidades", unidadeRepository.findByAtivoTrueOrderByNomeAsc());
        model.addAttribute("funcoes", funcaoUnidadeRepository.findByAtivoOrderByDescricaoAsc(true));
        model.addAttribute("perfis", perfilRepository.findAllByOrderByNomeAsc());
        model.addAttribute("especialidades", especialidadeAdminService.listarAtivas());
        if (!model.containsAttribute("usuarioForm")) {
            model.addAttribute("usuarioForm", usuarioAdminService.toEdicaoForm(usuarioId));
        }
        if (!model.containsAttribute("atuacaoForm")) {
            model.addAttribute("atuacaoForm", new UsuarioAtuacaoForm());
        }
    }
}
