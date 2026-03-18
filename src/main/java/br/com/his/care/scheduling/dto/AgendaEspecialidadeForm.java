package br.com.his.care.scheduling.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.format.annotation.DateTimeFormat;

import br.com.his.care.scheduling.model.ModoAgendaEspecialidade;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class AgendaEspecialidadeForm {

    @NotNull(message = "Cargo assistencial e obrigatorio")
    private Long cargoColaboradorId;

    private Long especialidadeId;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dataAgenda;

    @Size(max = 7, message = "Competencia invalida")
    private String competencia;

    private Set<Integer> diasSelecionados = new LinkedHashSet<>();

    @NotNull(message = "Hora inicial e obrigatoria")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime horaInicio;

    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime horaFim;

    private Integer intervaloMinutos = 60;

    @NotNull(message = "Quantidade de vagas e obrigatoria")
    private Integer vagasTotais = 1;

    @NotNull(message = "Tipo de agenda e obrigatorio")
    private ModoAgendaEspecialidade modoAgenda = ModoAgendaEspecialidade.CAPACIDADE_TURNO;

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

    public String getCompetencia() {
        return competencia;
    }

    public void setCompetencia(String competencia) {
        this.competencia = competencia;
    }

    public Set<Integer> getDiasSelecionados() {
        return diasSelecionados;
    }

    public void setDiasSelecionados(Set<Integer> diasSelecionados) {
        this.diasSelecionados = diasSelecionados;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public Integer getVagasTotais() {
        return vagasTotais;
    }

    public void setVagasTotais(Integer vagasTotais) {
        this.vagasTotais = vagasTotais;
    }

    public LocalTime getHoraFim() {
        return horaFim;
    }

    public void setHoraFim(LocalTime horaFim) {
        this.horaFim = horaFim;
    }

    public Integer getIntervaloMinutos() {
        return intervaloMinutos;
    }

    public void setIntervaloMinutos(Integer intervaloMinutos) {
        this.intervaloMinutos = intervaloMinutos;
    }

    public ModoAgendaEspecialidade getModoAgenda() {
        return modoAgenda;
    }

    public void setModoAgenda(ModoAgendaEspecialidade modoAgenda) {
        this.modoAgenda = modoAgenda;
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
