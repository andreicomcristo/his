package br.com.his.care.attendance.ui;

import br.com.his.care.attendance.api.dto.*;
import br.com.his.care.attendance.dto.*;
import br.com.his.care.attendance.model.*;
import br.com.his.care.attendance.repository.*;
import br.com.his.care.attendance.service.*;
import br.com.his.care.admission.dto.*;
import br.com.his.care.admission.model.*;
import br.com.his.care.admission.repository.*;
import br.com.his.care.triage.dto.*;
import br.com.his.care.triage.model.*;
import br.com.his.care.triage.repository.*;
import br.com.his.care.inpatient.dto.*;
import br.com.his.care.inpatient.model.*;
import br.com.his.care.inpatient.repository.*;
import br.com.his.care.inpatient.service.*;
import br.com.his.care.episode.model.*;
import br.com.his.care.episode.repository.*;
import br.com.his.care.timeline.dto.*;
import br.com.his.care.timeline.model.*;
import br.com.his.care.timeline.repository.*;

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
import br.com.his.care.attendance.dto.TransferenciaExternaForm;
import br.com.his.care.attendance.service.AssistencialFlowService;
import br.com.his.care.attendance.service.TransferenciaExternaService;
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
        return "pages/care/attendance/transferencias-externas/nova";
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
        List<br.com.his.care.attendance.model.TransferenciaExterna> transferencias = transferenciaExternaService
                .listarRecebidasPendentes(unidadeAtual());
        model.addAttribute("transferencias", transferencias);
        return "pages/care/attendance/transferencias-externas/recebidas";
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
