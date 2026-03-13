package br.com.his.care.attendance.api.dto;

public record LeanClassificacaoOperadorItemResponse(
        Long usuarioId,
        String operador,
        String username,
        Long quantidadeClassificacoes,
        Double mediaTempoClassificacaoMinutos) {
}
