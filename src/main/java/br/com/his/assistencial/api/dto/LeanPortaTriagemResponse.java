package br.com.his.assistencial.api.dto;

import java.time.LocalDate;
import java.util.List;

public record LeanPortaTriagemResponse(
        Long unidadeId,
        LocalDate dataInicio,
        LocalDate dataFim,
        List<LeanIndicadorValorResponse> indicadoresPorta,
        List<LeanIndicadorValorResponse> indicadoresTriagem,
        List<LeanChegadaHoraResponse> chegadasPorHora,
        List<LeanChegadaDiaSemanaResponse> chegadasPorDiaSemana,
        List<LeanTriagemCorDistribuicaoResponse> distribuicaoTriagem,
        LeanTempoTriagemResponse tempoTriagem) {
}
