package br.com.his.assistencial.dto;

import java.time.LocalDateTime;

import br.com.his.assistencial.model.AtendimentoPeriodoTipo;

public record TimelinePeriodoItem(
        AtendimentoPeriodoTipo tipo,
        String label,
        LocalDateTime inicioEm,
        LocalDateTime fimEm,
        long duracaoMinutos,
        String usuarioInicio,
        String usuarioFim) {
}
