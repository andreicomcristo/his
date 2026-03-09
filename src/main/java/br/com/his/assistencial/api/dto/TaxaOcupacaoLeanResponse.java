package br.com.his.assistencial.api.dto;

import java.time.LocalDate;
import java.util.List;

public record TaxaOcupacaoLeanResponse(
        Long unidadeId,
        LocalDate dataInicio,
        LocalDate dataFim,
        List<TaxaOcupacaoLeanIndicadorResponse> indicadores,
        List<TaxaOcupacaoTipoLeitoResponse> indicadoresPorTipoLeito) {
}
