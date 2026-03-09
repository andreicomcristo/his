package br.com.his.assistencial.api.dto;

public record TaxaOcupacaoTipoLeitoResponse(
        String tipoLeito,
        long leitosFixosElegiveis,
        long leitosTotaisElegiveis,
        double minutosOcupados,
        long minutosDisponiveisNominal,
        long minutosDisponiveisOperacional,
        Double taxaNominalPercentual,
        Double taxaOperacionalPercentual) {
}
