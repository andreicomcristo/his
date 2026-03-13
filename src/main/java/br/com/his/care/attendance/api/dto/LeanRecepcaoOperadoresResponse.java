package br.com.his.care.attendance.api.dto;

import java.time.LocalDate;
import java.util.List;

public record LeanRecepcaoOperadoresResponse(
        Long unidadeId,
        LocalDate dataInicio,
        LocalDate dataFim,
        Long totalOperadores,
        Long totalAtendimentos,
        Double mediaAtendimentosPorOperador,
        Double mediaTempoRecepcaoMinutos,
        List<LeanRecepcaoOperadorItemResponse> operadores) {
}
