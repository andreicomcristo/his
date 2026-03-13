package br.com.his.care.scheduling.dto;

import java.time.LocalDateTime;

import br.com.his.care.scheduling.model.StatusAgendamentoPaciente;

public class AgendaCalendarioEventoDto {

    private final Long agendaPacienteId;
    private final Long agendaId;
    private final String cargo;
    private final String especialidade;
    private final String paciente;
    private final String tipoVaga;
    private final StatusAgendamentoPaciente status;
    private final LocalDateTime inicio;
    private final LocalDateTime fim;
    private final String observacao;

    public AgendaCalendarioEventoDto(Long agendaPacienteId,
                                     Long agendaId,
                                     String cargo,
                                     String especialidade,
                                     String paciente,
                                     String tipoVaga,
                                     StatusAgendamentoPaciente status,
                                     LocalDateTime inicio,
                                     LocalDateTime fim,
                                     String observacao) {
        this.agendaPacienteId = agendaPacienteId;
        this.agendaId = agendaId;
        this.cargo = cargo;
        this.especialidade = especialidade;
        this.paciente = paciente;
        this.tipoVaga = tipoVaga;
        this.status = status;
        this.inicio = inicio;
        this.fim = fim;
        this.observacao = observacao;
    }

    public Long getAgendaPacienteId() {
        return agendaPacienteId;
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

    public String getPaciente() {
        return paciente;
    }

    public String getTipoVaga() {
        return tipoVaga;
    }

    public StatusAgendamentoPaciente getStatus() {
        return status;
    }

    public LocalDateTime getInicio() {
        return inicio;
    }

    public LocalDateTime getFim() {
        return fim;
    }

    public String getObservacao() {
        return observacao;
    }
}
