package br.com.his.assistencial.api.dto;

public record LeanTriagemCorDistribuicaoResponse(
        String classificacaoCor,
        Long quantidade,
        Double percentual) {
}
