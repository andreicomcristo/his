package br.com.his.care.scheduling.dto;

import java.time.LocalTime;

import org.springframework.format.annotation.DateTimeFormat;

import br.com.his.care.scheduling.model.TipoVagaAgenda;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class AgendaPacienteForm {

    @NotNull(message = "Paciente e obrigatorio")
    private Long pacienteId;

    @NotNull(message = "Tipo de vaga e obrigatorio")
    private TipoVagaAgenda tipoVaga = TipoVagaAgenda.NORMAL;

    @NotNull(message = "Horario e obrigatorio")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime horaAtendimento;

    @Size(max = 255, message = "Observacao deve ter no maximo 255 caracteres")
    private String observacao;

    public Long getPacienteId() {
        return pacienteId;
    }

    public void setPacienteId(Long pacienteId) {
        this.pacienteId = pacienteId;
    }

    public TipoVagaAgenda getTipoVaga() {
        return tipoVaga;
    }

    public void setTipoVaga(TipoVagaAgenda tipoVaga) {
        this.tipoVaga = tipoVaga;
    }

    public LocalTime getHoraAtendimento() {
        return horaAtendimento;
    }

    public void setHoraAtendimento(LocalTime horaAtendimento) {
        this.horaAtendimento = horaAtendimento;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}
