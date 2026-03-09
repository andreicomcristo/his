package br.com.his.assistencial.api.dto;

public record LeanIndicadorValorResponse(
        String codigo,
        String descricao,
        Double valor,
        String unidade,
        String observacao) {
}
