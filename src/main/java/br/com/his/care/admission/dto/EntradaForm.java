package br.com.his.care.admission.dto;

import br.com.his.care.attendance.api.dto.*;
import br.com.his.care.attendance.dto.*;
import br.com.his.care.attendance.model.*;
import br.com.his.care.attendance.repository.*;
import br.com.his.care.attendance.service.*;
import br.com.his.care.admission.dto.*;
import br.com.his.care.admission.model.*;
import br.com.his.care.admission.repository.*;
import br.com.his.care.triage.dto.*;
import br.com.his.care.triage.model.*;
import br.com.his.care.triage.repository.*;
import br.com.his.care.inpatient.dto.*;
import br.com.his.care.inpatient.model.*;
import br.com.his.care.inpatient.repository.*;
import br.com.his.care.inpatient.service.*;
import br.com.his.care.episode.model.*;
import br.com.his.care.episode.repository.*;
import br.com.his.care.timeline.dto.*;
import br.com.his.care.timeline.model.*;
import br.com.his.care.timeline.repository.*;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class EntradaForm {

    @NotNull(message = "Area da entrada e obrigatoria")
    private Long areaId;

    @NotNull(message = "Tipo de procedencia e obrigatorio")
    private Long tipoProcedenciaId;

    private Long procedenciaId;

    private Long procedenciaBairroId;

    private Long procedenciaCidadeUfId;

    private Long procedenciaCidadeId;

    @NotNull(message = "Forma de chegada e obrigatoria")
    private Long formaChegadaId;

    private Long motivoEntradaId;

    @Size(max = 30)
    private String telefoneComunicante;

    @Size(max = 150)
    private String comunicante;

    private Long grauParentescoId;

    @Size(max = 500)
    private String informacaoAdChegada;

    @Size(max = 200)
    private String procedenciaObservacao;

    private Long situacaoOcupacionalId;

    private Long profissaoId;

    @Size(max = 150)
    private String profissaoObservacao;

    @Size(max = 120)
    private String tempoServico;

    @Size(max = 500)
    private String observacoes;

    @Size(max = 120)
    private String convenio;

    @Size(max = 120)
    private String guia;

    public Long getAreaId() {
        return areaId;
    }

    public void setAreaId(Long areaId) {
        this.areaId = areaId;
    }

    public Long getProcedenciaId() {
        return procedenciaId;
    }

    public void setProcedenciaId(Long procedenciaId) {
        this.procedenciaId = procedenciaId;
    }

    public Long getTipoProcedenciaId() {
        return tipoProcedenciaId;
    }

    public void setTipoProcedenciaId(Long tipoProcedenciaId) {
        this.tipoProcedenciaId = tipoProcedenciaId;
    }

    public Long getProcedenciaBairroId() {
        return procedenciaBairroId;
    }

    public void setProcedenciaBairroId(Long procedenciaBairroId) {
        this.procedenciaBairroId = procedenciaBairroId;
    }

    public Long getProcedenciaCidadeId() {
        return procedenciaCidadeId;
    }

    public void setProcedenciaCidadeId(Long procedenciaCidadeId) {
        this.procedenciaCidadeId = procedenciaCidadeId;
    }

    public Long getProcedenciaCidadeUfId() {
        return procedenciaCidadeUfId;
    }

    public void setProcedenciaCidadeUfId(Long procedenciaCidadeUfId) {
        this.procedenciaCidadeUfId = procedenciaCidadeUfId;
    }

    public Long getFormaChegadaId() {
        return formaChegadaId;
    }

    public void setFormaChegadaId(Long formaChegadaId) {
        this.formaChegadaId = formaChegadaId;
    }

    public Long getMotivoEntradaId() {
        return motivoEntradaId;
    }

    public void setMotivoEntradaId(Long motivoEntradaId) {
        this.motivoEntradaId = motivoEntradaId;
    }

    public String getTelefoneComunicante() {
        return telefoneComunicante;
    }

    public void setTelefoneComunicante(String telefoneComunicante) {
        this.telefoneComunicante = telefoneComunicante;
    }

    public String getComunicante() {
        return comunicante;
    }

    public void setComunicante(String comunicante) {
        this.comunicante = comunicante;
    }

    public Long getGrauParentescoId() {
        return grauParentescoId;
    }

    public void setGrauParentescoId(Long grauParentescoId) {
        this.grauParentescoId = grauParentescoId;
    }

    public String getInformacaoAdChegada() {
        return informacaoAdChegada;
    }

    public void setInformacaoAdChegada(String informacaoAdChegada) {
        this.informacaoAdChegada = informacaoAdChegada;
    }

    public String getProcedenciaObservacao() {
        return procedenciaObservacao;
    }

    public void setProcedenciaObservacao(String procedenciaObservacao) {
        this.procedenciaObservacao = procedenciaObservacao;
    }

    public Long getSituacaoOcupacionalId() {
        return situacaoOcupacionalId;
    }

    public void setSituacaoOcupacionalId(Long situacaoOcupacionalId) {
        this.situacaoOcupacionalId = situacaoOcupacionalId;
    }

    public Long getProfissaoId() {
        return profissaoId;
    }

    public void setProfissaoId(Long profissaoId) {
        this.profissaoId = profissaoId;
    }

    public String getProfissaoObservacao() {
        return profissaoObservacao;
    }

    public void setProfissaoObservacao(String profissaoObservacao) {
        this.profissaoObservacao = profissaoObservacao;
    }

    public String getTempoServico() {
        return tempoServico;
    }

    public void setTempoServico(String tempoServico) {
        this.tempoServico = tempoServico;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public String getConvenio() {
        return convenio;
    }

    public void setConvenio(String convenio) {
        this.convenio = convenio;
    }

    public String getGuia() {
        return guia;
    }

    public void setGuia(String guia) {
        this.guia = guia;
    }
}
