package br.com.his.care.attendance.api.dto;

import java.time.LocalDateTime;

public record LeanRecepcaoOperadorAtendimentoItemResponse(
        Long atendimentoId,
        String pacienteNome,
        LocalDateTime inicioRecepcao,
        LocalDateTime fimRecepcao,
        Double tempoMinutos) {
}
