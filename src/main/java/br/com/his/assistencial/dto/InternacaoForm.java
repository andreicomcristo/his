package br.com.his.assistencial.dto;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class InternacaoForm {

    @NotNull(message = "Atendimento e obrigatorio")
    private Long atendimentoId;

    private Long observacaoOrigemId;

    private Long origemDemandaId;

    private Long perfilInternacaoId;

    @NotNull(message = "Data/hora de decisao e obrigatoria")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime dataHoraDecisaoInternacao = LocalDateTime.now();

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime dataHoraInicioInternacao;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime dataHoraFimInternacao;

    private Long leitoId;

    private Long tipoOcupacaoId;

    @Size(max = 2000)
    private String observacao;

    public Long getAtendimentoId() {
        return atendimentoId;
    }

    public void setAtendimentoId(Long atendimentoId) {
        this.atendimentoId = atendimentoId;
    }

    public Long getObservacaoOrigemId() {
        return observacaoOrigemId;
    }

    public void setObservacaoOrigemId(Long observacaoOrigemId) {
        this.observacaoOrigemId = observacaoOrigemId;
    }

    public Long getOrigemDemandaId() {
        return origemDemandaId;
    }

    public void setOrigemDemandaId(Long origemDemandaId) {
        this.origemDemandaId = origemDemandaId;
    }

    public Long getPerfilInternacaoId() {
        return perfilInternacaoId;
    }

    public void setPerfilInternacaoId(Long perfilInternacaoId) {
        this.perfilInternacaoId = perfilInternacaoId;
    }

    public LocalDateTime getDataHoraDecisaoInternacao() {
        return dataHoraDecisaoInternacao;
    }

    public void setDataHoraDecisaoInternacao(LocalDateTime dataHoraDecisaoInternacao) {
        this.dataHoraDecisaoInternacao = dataHoraDecisaoInternacao;
    }

    public LocalDateTime getDataHoraInicioInternacao() {
        return dataHoraInicioInternacao;
    }

    public void setDataHoraInicioInternacao(LocalDateTime dataHoraInicioInternacao) {
        this.dataHoraInicioInternacao = dataHoraInicioInternacao;
    }

    public LocalDateTime getDataHoraFimInternacao() {
        return dataHoraFimInternacao;
    }

    public void setDataHoraFimInternacao(LocalDateTime dataHoraFimInternacao) {
        this.dataHoraFimInternacao = dataHoraFimInternacao;
    }

    public Long getLeitoId() {
        return leitoId;
    }

    public void setLeitoId(Long leitoId) {
        this.leitoId = leitoId;
    }

    public Long getTipoOcupacaoId() {
        return tipoOcupacaoId;
    }

    public void setTipoOcupacaoId(Long tipoOcupacaoId) {
        this.tipoOcupacaoId = tipoOcupacaoId;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}
