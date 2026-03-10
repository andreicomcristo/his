package br.com.his.care.inpatient.ui;

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
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.his.access.context.UnidadeContext;
import br.com.his.access.service.OperationalPermissionService;
import br.com.his.care.inpatient.dto.TransferenciaInternaLeitoForm;
import br.com.his.care.attendance.model.Atendimento;
import br.com.his.care.inpatient.model.Leito;
import br.com.his.care.inpatient.model.LeitoOcupacao;
import br.com.his.care.inpatient.service.LeitoMapaService;
import br.com.his.care.inpatient.service.LeitoOcupacaoService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/ui/leitos/mapa")
public class LeitoMapaController {

    private final UnidadeContext unidadeContext;
    private final OperationalPermissionService operationalPermissionService;
    private final LeitoMapaService leitoMapaService;
    private final LeitoOcupacaoService leitoOcupacaoService;

    public LeitoMapaController(UnidadeContext unidadeContext,
                               OperationalPermissionService operationalPermissionService,
                               LeitoMapaService leitoMapaService,
                               LeitoOcupacaoService leitoOcupacaoService) {
        this.unidadeContext = unidadeContext;
        this.operationalPermissionService = operationalPermissionService;
        this.leitoMapaService = leitoMapaService;
        this.leitoOcupacaoService = leitoOcupacaoService;
    }

    @GetMapping
    public String mapa(Model model) {
        requirePermission();
        model.addAttribute("areas", leitoMapaService.montarMapaPorUnidade(unidadeAtual()));
        return "pages/care/inpatient/leitos/mapa";
    }

    @GetMapping("/transferencia/{ocupacaoId}")
    public String transferencia(@PathVariable Long ocupacaoId, Model model) {
        requireRecepcaoPermission();
        LeitoOcupacao ocupacao = buscarOcupacaoAbertaDaUnidade(ocupacaoId);
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new TransferenciaInternaLeitoForm());
        }
        String modalidade = resolveModalidade(ocupacao);
        List<Leito> leitosDestino = leitoOcupacaoService
                .listarLeitosDisponiveis(unidadeAtual(), modalidade, null)
                .stream()
                .filter(item -> !Objects.equals(item.getId(), ocupacao.getLeito().getId()))
                .toList();
        model.addAttribute("ocupacao", ocupacao);
        model.addAttribute("atendimento", resolveAtendimento(ocupacao));
        model.addAttribute("modalidadeTransferencia", modalidade);
        model.addAttribute("leitosDestino", leitosDestino);
        return "pages/care/inpatient/leitos/transferencia";
    }

    @PostMapping("/transferencia/{ocupacaoId}")
    public String transferir(@PathVariable Long ocupacaoId,
                             @Valid @ModelAttribute("form") TransferenciaInternaLeitoForm form,
                             BindingResult bindingResult,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        requireRecepcaoPermission();
        LeitoOcupacao ocupacao = buscarOcupacaoAbertaDaUnidade(ocupacaoId);
        if (bindingResult.hasErrors()) {
            model.addAttribute("ocupacao", ocupacao);
            model.addAttribute("atendimento", resolveAtendimento(ocupacao));
            String modalidade = resolveModalidade(ocupacao);
            model.addAttribute("modalidadeTransferencia", modalidade);
            model.addAttribute("leitosDestino", leitoOcupacaoService
                    .listarLeitosDisponiveis(unidadeAtual(), modalidade, null)
                    .stream()
                    .filter(item -> !Objects.equals(item.getId(), ocupacao.getLeito().getId()))
                    .toList());
            return "pages/care/inpatient/leitos/transferencia";
        }
        try {
            leitoOcupacaoService.transferirInterno(ocupacaoId, form.getLeitoDestinoId(), form.getObservacao());
            redirectAttributes.addFlashAttribute("successMessage", "Transferencia interna realizada com sucesso");
            return "redirect:/ui/leitos/mapa";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("ocupacao", ocupacao);
            model.addAttribute("atendimento", resolveAtendimento(ocupacao));
            String modalidade = resolveModalidade(ocupacao);
            model.addAttribute("modalidadeTransferencia", modalidade);
            model.addAttribute("leitosDestino", leitoOcupacaoService
                    .listarLeitosDisponiveis(unidadeAtual(), modalidade, null)
                    .stream()
                    .filter(item -> !Objects.equals(item.getId(), ocupacao.getLeito().getId()))
                    .toList());
            return "pages/care/inpatient/leitos/transferencia";
        }
    }

    private LeitoOcupacao buscarOcupacaoAbertaDaUnidade(Long ocupacaoId) {
        LeitoOcupacao ocupacao = leitoOcupacaoService.buscarOcupacaoDetalhada(ocupacaoId)
                .orElseThrow(() -> new IllegalArgumentException("Ocupacao nao encontrada"));
        if (ocupacao.getDataHoraSaida() != null) {
            throw new IllegalArgumentException("Ocupacao ja encerrada");
        }
        if (!Objects.equals(ocupacao.getLeito().getUnidade().getId(), unidadeAtual())) {
            throw new IllegalArgumentException("Ocupacao nao pertence a unidade atual");
        }
        return ocupacao;
    }

    private Atendimento resolveAtendimento(LeitoOcupacao ocupacao) {
        if (ocupacao.getObservacaoAtendimento() != null) {
            return ocupacao.getObservacaoAtendimento().getAtendimento();
        }
        if (ocupacao.getInternacao() != null) {
            return ocupacao.getInternacao().getAtendimento();
        }
        throw new IllegalArgumentException("Ocupacao sem atendimento associado");
    }

    private String resolveModalidade(LeitoOcupacao ocupacao) {
        if (ocupacao.getObservacaoAtendimento() != null) {
            return LeitoOcupacaoService.MODALIDADE_OBSERVACAO;
        }
        if (ocupacao.getInternacao() != null) {
            return LeitoOcupacaoService.MODALIDADE_INTERNACAO;
        }
        throw new IllegalArgumentException("Ocupacao sem modalidade assistencial");
    }

    private void requirePermission() {
        if (!operationalPermissionService.canGerirPermanencia(
                SecurityContextHolder.getContext().getAuthentication())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Sem permissao para mapa de leitos");
        }
    }

    private void requireRecepcaoPermission() {
        if (!operationalPermissionService.has(
                SecurityContextHolder.getContext().getAuthentication(),
                OperationalPermissionService.PERM_RECEPCAO_EXECUTAR)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Sem permissao para transferencia interna");
        }
    }

    private Long unidadeAtual() {
        return unidadeContext.getUnidadeAtual()
                .orElseThrow(() -> new IllegalArgumentException("Unidade atual nao definida"));
    }
}
