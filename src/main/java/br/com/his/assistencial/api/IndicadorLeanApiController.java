package br.com.his.assistencial.api;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import br.com.his.access.context.UnidadeContext;
import br.com.his.access.service.OperationalPermissionService;
import br.com.his.assistencial.api.dto.LeanConsultaResponse;
import br.com.his.assistencial.api.dto.LeanPortaTriagemResponse;
import br.com.his.assistencial.api.dto.TaxaOcupacaoLeanResponse;
import br.com.his.assistencial.service.IndicadorLeanService;

@RestController
@RequestMapping("/api/assistencial/indicadores/lean")
public class IndicadorLeanApiController {

    private final IndicadorLeanService indicadorLeanService;
    private final UnidadeContext unidadeContext;
    private final OperationalPermissionService operationalPermissionService;

    public IndicadorLeanApiController(IndicadorLeanService indicadorLeanService,
                                      UnidadeContext unidadeContext,
                                      OperationalPermissionService operationalPermissionService) {
        this.indicadorLeanService = indicadorLeanService;
        this.unidadeContext = unidadeContext;
        this.operationalPermissionService = operationalPermissionService;
    }

    @GetMapping("/taxa-ocupacao")
    public TaxaOcupacaoLeanResponse taxaOcupacao(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            Authentication authentication) {
        requirePermission(authentication, OperationalPermissionService.PERM_ATENDIMENTO_ACESSAR);
        Long unidadeId = unidadeContext.getUnidadeAtual()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Selecione uma unidade antes de consultar indicadores"));
        try {
            return indicadorLeanService.calcularTaxasOcupacao(unidadeId, dataInicio, dataFim);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
        }
    }

    @GetMapping("/porta-triagem")
    public LeanPortaTriagemResponse portaTriagem(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            Authentication authentication) {
        requirePermission(authentication, OperationalPermissionService.PERM_ATENDIMENTO_ACESSAR);
        Long unidadeId = unidadeContext.getUnidadeAtual()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Selecione uma unidade antes de consultar indicadores"));
        try {
            return indicadorLeanService.calcularPortaTriagem(unidadeId, dataInicio, dataFim);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
        }
    }

    @GetMapping("/consulta")
    public LeanConsultaResponse consulta(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            Authentication authentication) {
        requirePermission(authentication, OperationalPermissionService.PERM_ATENDIMENTO_ACESSAR);
        Long unidadeId = unidadeContext.getUnidadeAtual()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Selecione uma unidade antes de consultar indicadores"));
        try {
            return indicadorLeanService.calcularConsulta(unidadeId, dataInicio, dataFim);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
        }
    }

    private void requirePermission(Authentication authentication, String permission) {
        if (!operationalPermissionService.has(authentication, permission)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acesso negado");
        }
    }
}
