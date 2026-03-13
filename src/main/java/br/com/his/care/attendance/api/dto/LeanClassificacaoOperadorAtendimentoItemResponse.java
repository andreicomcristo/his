package br.com.his.care.attendance.api.dto;

import java.time.LocalDateTime;

public record LeanClassificacaoOperadorAtendimentoItemResponse(
        Long atendimentoId,
        String pacienteNome,
        LocalDateTime inicioClassificacao,
        LocalDateTime fimClassificacao,
        Double tempoMinutos) {
}
