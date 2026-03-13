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

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import br.com.his.access.context.UnidadeContext;
import br.com.his.access.service.OperationalPermissionService;
import br.com.his.care.attendance.api.dto.LeanConsultaResponse;
import br.com.his.care.attendance.api.dto.LeanPortaTriagemResponse;
import br.com.his.care.attendance.api.dto.LeanClassificacaoOperadorItemResponse;
import br.com.his.care.attendance.api.dto.LeanClassificacaoOperadoresResponse;
import br.com.his.care.attendance.api.dto.LeanRecepcaoOperadoresResponse;
import br.com.his.care.attendance.api.dto.TaxaOcupacaoLeanResponse;
import br.com.his.care.attendance.service.IndicadorLeanService;

@Controller
@RequestMapping("/ui/indicadores/lean")
public class IndicadorLeanController {

    private final IndicadorLeanService indicadorLeanService;
    private final UnidadeContext unidadeContext;
    private final OperationalPermissionService operationalPermissionService;

    public IndicadorLeanController(IndicadorLeanService indicadorLeanService,
                                   UnidadeContext unidadeContext,
                                   OperationalPermissionService operationalPermissionService) {
        this.indicadorLeanService = indicadorLeanService;
        this.unidadeContext = unidadeContext;
        this.operationalPermissionService = operationalPermissionService;
    }

    @GetMapping("/taxa-ocupacao")
    public String taxaOcupacao(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            Authentication authentication,
            Model model) {
        requirePermission(authentication, OperationalPermissionService.PERM_ATENDIMENTO_ACESSAR);
        Long unidadeId = unidadeContext.getUnidadeAtual()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Selecione uma unidade antes de consultar indicadores"));

        LocalDate fim = dataFim != null ? dataFim : LocalDate.now();
        LocalDate inicio = dataInicio != null ? dataInicio : fim.withDayOfMonth(1);

        model.addAttribute("dataInicio", inicio);
        model.addAttribute("dataFim", fim);

        try {
            TaxaOcupacaoLeanResponse response = indicadorLeanService.calcularTaxasOcupacao(unidadeId, inicio, fim);
            model.addAttribute("resultado", response);
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
        }

        return "pages/care/attendance/indicadores/lean-taxa-ocupacao";
    }

    @GetMapping("/porta-triagem")
    public String portaTriagem(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            Authentication authentication,
            Model model) {
        requirePermission(authentication, OperationalPermissionService.PERM_ATENDIMENTO_ACESSAR);
        Long unidadeId = unidadeContext.getUnidadeAtual()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Selecione uma unidade antes de consultar indicadores"));

        LocalDate fim = dataFim != null ? dataFim : LocalDate.now();
        LocalDate inicio = dataInicio != null ? dataInicio : fim.withDayOfMonth(1);

        model.addAttribute("dataInicio", inicio);
        model.addAttribute("dataFim", fim);

        try {
            LeanPortaTriagemResponse response = indicadorLeanService.calcularPortaTriagem(unidadeId, inicio, fim);
            model.addAttribute("resultado", response);
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
        }

        return "pages/care/attendance/indicadores/lean-porta-triagem";
    }

    @GetMapping("/consulta")
    public String consulta(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            Authentication authentication,
            Model model) {
        requirePermission(authentication, OperationalPermissionService.PERM_ATENDIMENTO_ACESSAR);
        Long unidadeId = unidadeContext.getUnidadeAtual()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Selecione uma unidade antes de consultar indicadores"));

        LocalDate fim = dataFim != null ? dataFim : LocalDate.now();
        LocalDate inicio = dataInicio != null ? dataInicio : fim.withDayOfMonth(1);

        model.addAttribute("dataInicio", inicio);
        model.addAttribute("dataFim", fim);

        try {
            LeanConsultaResponse response = indicadorLeanService.calcularConsulta(unidadeId, inicio, fim);
            model.addAttribute("resultado", response);
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
        }

        return "pages/care/attendance/indicadores/lean-consulta";
    }

    @GetMapping("/recepcao-operadores")
    public String recepcaoOperadores(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @RequestParam(required = false) Long operadorUsuarioId,
            Authentication authentication,
            Model model) {
        requirePermission(authentication, OperationalPermissionService.PERM_ATENDIMENTO_ACESSAR);
        Long unidadeId = unidadeContext.getUnidadeAtual()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Selecione uma unidade antes de consultar indicadores"));

        LocalDate fim = dataFim != null ? dataFim : LocalDate.now();
        LocalDate inicio = dataInicio != null ? dataInicio : fim.withDayOfMonth(1);

        model.addAttribute("dataInicio", inicio);
        model.addAttribute("dataFim", fim);

        try {
            LeanRecepcaoOperadoresResponse response = indicadorLeanService.calcularRecepcaoOperadores(unidadeId, inicio, fim);
            model.addAttribute("resultado", response);
            model.addAttribute("operadorUsuarioIdSelecionado", operadorUsuarioId);
            if (operadorUsuarioId != null) {
                model.addAttribute("detalhesOperador",
                        indicadorLeanService.listarDetalhesRecepcaoPorOperador(unidadeId, inicio, fim, operadorUsuarioId));
                String nomeOperadorSelecionado = response.operadores().stream()
                        .filter(item -> operadorUsuarioId.equals(item.usuarioId()))
                        .map(LeanRecepcaoOperadorItemResponse::operador)
                        .findFirst()
                        .orElse("OPERADOR");
                model.addAttribute("operadorSelecionadoNome", nomeOperadorSelecionado);
            }
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
        }

        return "pages/care/attendance/indicadores/lean-recepcao-operadores";
    }

    @GetMapping("/classificacao-operadores")
    public String classificacaoOperadores(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @RequestParam(required = false) Long operadorUsuarioId,
            Authentication authentication,
            Model model) {
        requirePermission(authentication, OperationalPermissionService.PERM_ATENDIMENTO_ACESSAR);
        Long unidadeId = unidadeContext.getUnidadeAtual()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Selecione uma unidade antes de consultar indicadores"));

        LocalDate fim = dataFim != null ? dataFim : LocalDate.now();
        LocalDate inicio = dataInicio != null ? dataInicio : fim.withDayOfMonth(1);

        model.addAttribute("dataInicio", inicio);
        model.addAttribute("dataFim", fim);

        try {
            LeanClassificacaoOperadoresResponse response = indicadorLeanService.calcularClassificacaoOperadores(unidadeId, inicio, fim);
            model.addAttribute("resultado", response);
            model.addAttribute("operadorUsuarioIdSelecionado", operadorUsuarioId);
            if (operadorUsuarioId != null) {
                model.addAttribute("detalhesOperador",
                        indicadorLeanService.listarDetalhesClassificacaoPorOperador(unidadeId, inicio, fim, operadorUsuarioId));
                String nomeOperadorSelecionado = response.operadores().stream()
                        .filter(item -> operadorUsuarioId.equals(item.usuarioId()))
                        .map(LeanClassificacaoOperadorItemResponse::operador)
                        .findFirst()
                        .orElse("OPERADOR");
                model.addAttribute("operadorSelecionadoNome", nomeOperadorSelecionado);
            }
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
        }

        return "pages/care/attendance/indicadores/lean-classificacao-operadores";
    }

    private void requirePermission(Authentication authentication, String permission) {
        if (!operationalPermissionService.has(authentication, permission)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acesso negado");
        }
    }
}
