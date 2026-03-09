package br.com.his.assistencial.api.dto;

public record TaxaOcupacaoLeanIndicadorResponse(
        String codigo,
        String descricao,
        long leitosFixosElegiveis,
        long leitosTotaisElegiveis,
        double minutosOcupados,
        long minutosDisponiveisNominal,
        long minutosDisponiveisOperacional,
        Double taxaNominalPercentual,
        Double taxaOperacionalPercentual) {
}
