package br.com.his.assistencial.api.dto;

public record LeanConsultaIndicadorResponse(
        String codigo,
        String descricao,
        Double mediaMinutos,
        Double medianaMinutos,
        Double p90Minutos,
        Long totalCasos,
        Long totalFallbackDesfecho,
        String observacao) {
}
