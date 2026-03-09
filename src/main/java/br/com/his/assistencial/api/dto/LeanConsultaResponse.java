package br.com.his.assistencial.api.dto;

import java.time.LocalDate;
import java.util.List;

public record LeanConsultaResponse(
        Long unidadeId,
        LocalDate dataInicio,
        LocalDate dataFim,
        List<LeanConsultaIndicadorResponse> indicadores) {
}
