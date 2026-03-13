package br.com.his.care.attendance.api.dto;

import java.time.LocalDate;
import java.util.List;

public record LeanClassificacaoOperadoresResponse(
        Long unidadeId,
        LocalDate dataInicio,
        LocalDate dataFim,
        Long totalOperadores,
        Long totalClassificacoes,
        Double mediaClassificacoesPorOperador,
        Double mediaTempoClassificacaoMinutos,
        List<LeanClassificacaoOperadorItemResponse> operadores) {
}
