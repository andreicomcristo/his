package br.com.his.care.scheduling.dto;

import java.time.LocalTime;

import org.springframework.format.annotation.DateTimeFormat;

import br.com.his.care.scheduling.model.TipoVagaAgenda;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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

    @NotNull(message = "Periodicidade da recorrencia e obrigatoria")
    private PeriodicidadeRecorrenciaAgendamento periodicidadeRecorrencia = PeriodicidadeRecorrenciaAgendamento.NENHUMA;

    @NotNull(message = "Quantidade de sessoes e obrigatoria")
    @Min(value = 1, message = "Quantidade de sessoes deve ser maior que zero")
    @Max(value = 52, message = "Quantidade de sessoes deve ser no maximo 52")
    private Integer quantidadeSessoes = 1;

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

    public PeriodicidadeRecorrenciaAgendamento getPeriodicidadeRecorrencia() {
        return periodicidadeRecorrencia;
    }

    public void setPeriodicidadeRecorrencia(PeriodicidadeRecorrenciaAgendamento periodicidadeRecorrencia) {
        this.periodicidadeRecorrencia = periodicidadeRecorrencia;
    }

    public Integer getQuantidadeSessoes() {
        return quantidadeSessoes;
    }

    public void setQuantidadeSessoes(Integer quantidadeSessoes) {
        this.quantidadeSessoes = quantidadeSessoes;
    }
}
