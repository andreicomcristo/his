package br.com.his.care.attendance.api.dto;

public record LeanRecepcaoOperadorItemResponse(
        Long usuarioId,
        String operador,
        String username,
        Long quantidadeAtendimentos,
        Double mediaTempoRecepcaoMinutos) {
}
