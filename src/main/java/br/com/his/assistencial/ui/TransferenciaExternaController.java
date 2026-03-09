package br.com.his.assistencial.ui;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.his.access.context.UnidadeContext;
import br.com.his.access.repository.UnidadeRepository;
import br.com.his.access.service.OperationalPermissionService;
import br.com.his.assistencial.dto.TransferenciaExternaForm;
import br.com.his.assistencial.service.AssistencialFlowService;
import br.com.his.assistencial.service.TransferenciaExternaService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/ui/transferencias-externas")
public class TransferenciaExternaController {

    private final TransferenciaExternaService transferenciaExternaService;
    private final AssistencialFlowService assistencialFlowService;
    private final UnidadeRepository unidadeRepository;
    private final UnidadeContext unidadeContext;
    private final OperationalPermissionService operationalPermissionService;

    public TransferenciaExternaController(TransferenciaExternaService transferenciaExternaService,
                                          AssistencialFlowService assistencialFlowService,
                                          UnidadeRepository unidadeRepository,
                                          UnidadeContext unidadeContext,
                                          OperationalPermissionService operationalPermissionService) {
        this.transferenciaExternaService = transferenciaExternaService;
        this.assistencialFlowService = assistencialFlowService;
        this.unidadeRepository = unidadeRepository;
        this.unidadeContext = unidadeContext;
        this.operationalPermissionService = operationalPermissionService;
    }

    @GetMapping("/nova/{atendimentoId}")
    public String nova(@PathVariable Long atendimentoId, Model model) {
        requirePermission();
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new TransferenciaExternaForm());
        }
        Long unidadeAtual = unidadeAtual();
        model.addAttribute("atendimento", assistencialFlowService.buscarAtendimento(atendimentoId));
        model.addAttribute("unidadesDestino", unidadeRepository.findAllByOrderByNomeAsc().stream()
                .filter(u -> u.isAtivo() && !u.getId().equals(unidadeAtual))
                .toList());
        return "pages/transferencias-externas/nova";
    }

    @PostMapping
    public String solicitar(@RequestParam Long atendimentoOrigemId,
                            @Valid @ModelAttribute("form") TransferenciaExternaForm form,
                            BindingResult bindingResult,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        requirePermission();
        if (bindingResult.hasErrors()) {
            return nova(atendimentoOrigemId, model);
        }
        try {
            transferenciaExternaService.solicitar(unidadeAtual(), atendimentoOrigemId, form);
            redirectAttributes.addFlashAttribute("successMessage", "Transferencia externa solicitada com sucesso");
            return "redirect:/ui/atendimentos";
        } catch (IllegalArgumentException ex) {
            bindingResult.reject("transferenciaExterna", ex.getMessage());
            return nova(atendimentoOrigemId, model);
        }
    }

    @GetMapping("/recebidas")
    public String recebidas(Model model) {
        requirePermission();
        List<br.com.his.assistencial.model.TransferenciaExterna> transferencias = transferenciaExternaService
                .listarRecebidasPendentes(unidadeAtual());
        model.addAttribute("transferencias", transferencias);
        return "pages/transferencias-externas/recebidas";
    }

    @PostMapping("/{id}/acolher")
    public String acolher(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        requirePermission();
        try {
            var transferencia = transferenciaExternaService.acolher(unidadeAtual(), id);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Paciente acolhido. Preencha a entrada do novo atendimento.");
            return "redirect:/ui/entradas/atendimento/" + transferencia.getAtendimentoDestino().getId();
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/ui/transferencias-externas/recebidas";
    }

    private void requirePermission() {
        if (!operationalPermissionService.has(
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication(),
                OperationalPermissionService.PERM_RECEPCAO_EXECUTAR)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Sem permissao para transferencia externa");
        }
    }

    private Long unidadeAtual() {
        return unidadeContext.getUnidadeAtual()
                .orElseThrow(() -> new IllegalArgumentException("Unidade atual nao definida"));
    }
}
