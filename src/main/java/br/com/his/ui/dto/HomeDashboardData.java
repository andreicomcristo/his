package br.com.his.ui.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record HomeDashboardData(
        LocalDate referencia,
        List<HomeCard> operacaoCards,
        List<HomeFilaItem> filaAtencao,
        List<HomeDesfechoItem> desfechosHoje) {

    public record HomeCard(
            String titulo,
            String valor,
            String detalhe,
            String icone,
            String backgroundClass) {
    }

    public record HomeFilaItem(
            Long atendimentoId,
            String paciente,
            String status,
            LocalDateTime chegadaEm,
            long minutosEspera) {
    }

    public record HomeDesfechoItem(
            String motivo,
            long total) {
    }
}
