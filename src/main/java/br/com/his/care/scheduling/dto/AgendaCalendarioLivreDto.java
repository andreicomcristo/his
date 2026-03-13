package br.com.his.care.scheduling.dto;

import java.time.LocalDateTime;

public class AgendaCalendarioLivreDto {

    private final Long agendaSlotId;
    private final Long agendaId;
    private final String cargo;
    private final String especialidade;
    private final LocalDateTime inicio;
    private final LocalDateTime fim;

    public AgendaCalendarioLivreDto(Long agendaSlotId,
                                    Long agendaId,
                                    String cargo,
                                    String especialidade,
                                    LocalDateTime inicio,
                                    LocalDateTime fim) {
        this.agendaSlotId = agendaSlotId;
        this.agendaId = agendaId;
        this.cargo = cargo;
        this.especialidade = especialidade;
        this.inicio = inicio;
        this.fim = fim;
    }

    public Long getAgendaSlotId() {
        return agendaSlotId;
    }

    public Long getAgendaId() {
        return agendaId;
    }

    public String getCargo() {
        return cargo;
    }

    public String getEspecialidade() {
        return especialidade;
    }

    public LocalDateTime getInicio() {
        return inicio;
    }

    public LocalDateTime getFim() {
        return fim;
    }
}
