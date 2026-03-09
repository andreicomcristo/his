package br.com.his.assistencial.api.dto;

public record LeanTempoTriagemResponse(
        Double mediaMinutos,
        Double medianaMinutos,
        Double p90Minutos,
        Long totalTriagensComTempo) {
}
