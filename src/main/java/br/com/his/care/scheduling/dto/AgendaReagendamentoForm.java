package br.com.his.care.scheduling.dto;

import java.time.LocalTime;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class AgendaReagendamentoForm {

    @NotNull(message = "Agenda de destino e obrigatoria")
    private Long agendaDestinoId;

    @NotNull(message = "Horario de destino e obrigatorio")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime horarioDestino;

    @Size(max = 255, message = "Observacao deve ter no maximo 255 caracteres")
    private String observacao;

    public Long getAgendaDestinoId() {
        return agendaDestinoId;
    }

    public void setAgendaDestinoId(Long agendaDestinoId) {
        this.agendaDestinoId = agendaDestinoId;
    }

    public LocalTime getHorarioDestino() {
        return horarioDestino;
    }

    public void setHorarioDestino(LocalTime horarioDestino) {
        this.horarioDestino = horarioDestino;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}
