package br.com.his.care.scheduling.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class AgendaEspecialidadeForm {

    @NotNull(message = "Cargo assistencial e obrigatorio")
    private Long cargoColaboradorId;

    private Long especialidadeId;

    @NotNull(message = "Data e obrigatoria")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dataAgenda;

    @NotNull(message = "Hora inicial e obrigatoria")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime horaInicio;

    @NotNull(message = "Hora final e obrigatoria")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime horaFim;

    @NotNull(message = "Vagas totais e obrigatorio")
    @Min(value = 1, message = "Vagas totais deve ser maior que zero")
    private Integer vagasTotais;

    @NotNull(message = "Vagas de retorno e obrigatorio")
    @Min(value = 0, message = "Vagas de retorno nao pode ser negativo")
    private Integer vagasRetorno = 0;

    @NotNull(message = "Intervalo e obrigatorio")
    @Min(value = 1, message = "Intervalo deve ser maior que zero")
    private Integer intervaloMinutos = 15;

    @Size(max = 255, message = "Observacao deve ter no maximo 255 caracteres")
    private String observacao;

    private boolean ativo = true;

    public Long getCargoColaboradorId() {
        return cargoColaboradorId;
    }

    public void setCargoColaboradorId(Long cargoColaboradorId) {
        this.cargoColaboradorId = cargoColaboradorId;
    }

    public Long getEspecialidadeId() {
        return especialidadeId;
    }

    public void setEspecialidadeId(Long especialidadeId) {
        this.especialidadeId = especialidadeId;
    }

    public LocalDate getDataAgenda() {
        return dataAgenda;
    }

    public void setDataAgenda(LocalDate dataAgenda) {
        this.dataAgenda = dataAgenda;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFim() {
        return horaFim;
    }

    public void setHoraFim(LocalTime horaFim) {
        this.horaFim = horaFim;
    }

    public Integer getVagasTotais() {
        return vagasTotais;
    }

    public void setVagasTotais(Integer vagasTotais) {
        this.vagasTotais = vagasTotais;
    }

    public Integer getVagasRetorno() {
        return vagasRetorno;
    }

    public void setVagasRetorno(Integer vagasRetorno) {
        this.vagasRetorno = vagasRetorno;
    }

    public Integer getIntervaloMinutos() {
        return intervaloMinutos;
    }

    public void setIntervaloMinutos(Integer intervaloMinutos) {
        this.intervaloMinutos = intervaloMinutos;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
}
