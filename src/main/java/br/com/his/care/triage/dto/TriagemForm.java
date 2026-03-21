package br.com.his.care.triage.dto;

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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.Size;

public class TriagemForm {

    private Long classificacaoCorId;

    @Size(max = 20)
    private String pressaoArterial;

    private BigDecimal temperatura;
    private Integer frequenciaCardiaca;
    private Integer saturacaoO2;
    private Integer frequenciaRespiratoria;
    private Integer saturacaoO2ComTerapiaO2;
    private Integer saturacaoO2Aa;
    private BigDecimal glicemiaCapilar;
    private BigDecimal pesoKg;
    private BigDecimal alturaCm;
    private Integer hgt;
    private Integer perfusaoCapilarPerifericaSeg;
    private Integer preenchimentoCapilarCentralSeg;

    private Long reguaDorId;
    private Long areaExecucaoId;
    private Long glasgowAberturaOcularId;
    private Long glasgowRespostaVerbalId;
    private Long glasgowRespostaMotoraId;
    private Long glasgowRespostaPupilarId;
    private Integer glasgowTotal;

    @Size(max = 1000)
    private String queixaPrincipal;

    @Size(max = 500)
    private String medicacoesUsoContinuo;

    @Size(max = 2000)
    private String discriminador;

    @Size(max = 2000)
    private String observacao;

    private List<AlergiaItem> alergias = new ArrayList<>();

    private List<Long> comorbidadeIds = new ArrayList<>();
    private List<Long> avcSinalAlertaIds = new ArrayList<>();

    public static class AlergiaItem {
        private Long substanciaId;
        private Long severidadeId;

        @Size(max = 500)
        private String descricao;

        public Long getSubstanciaId() {
            return substanciaId;
        }

        public void setSubstanciaId(Long substanciaId) {
            this.substanciaId = substanciaId;
        }

        public Long getSeveridadeId() {
            return severidadeId;
        }

        public void setSeveridadeId(Long severidadeId) {
            this.severidadeId = severidadeId;
        }

        public String getDescricao() {
            return descricao;
        }

        public void setDescricao(String descricao) {
            this.descricao = descricao;
        }
    }

    public Long getClassificacaoCorId() {
        return classificacaoCorId;
    }

    public void setClassificacaoCorId(Long classificacaoCorId) {
        this.classificacaoCorId = classificacaoCorId;
    }

    public String getPressaoArterial() {
        return pressaoArterial;
    }

    public void setPressaoArterial(String pressaoArterial) {
        this.pressaoArterial = pressaoArterial;
    }

    public BigDecimal getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(BigDecimal temperatura) {
        this.temperatura = temperatura;
    }

    public Integer getFrequenciaCardiaca() {
        return frequenciaCardiaca;
    }

    public void setFrequenciaCardiaca(Integer frequenciaCardiaca) {
        this.frequenciaCardiaca = frequenciaCardiaca;
    }

    public Integer getSaturacaoO2() {
        return saturacaoO2;
    }

    public void setSaturacaoO2(Integer saturacaoO2) {
        this.saturacaoO2 = saturacaoO2;
    }

    public Integer getFrequenciaRespiratoria() {
        return frequenciaRespiratoria;
    }

    public void setFrequenciaRespiratoria(Integer frequenciaRespiratoria) {
        this.frequenciaRespiratoria = frequenciaRespiratoria;
    }

    public Integer getSaturacaoO2ComTerapiaO2() {
        return saturacaoO2ComTerapiaO2;
    }

    public void setSaturacaoO2ComTerapiaO2(Integer saturacaoO2ComTerapiaO2) {
        this.saturacaoO2ComTerapiaO2 = saturacaoO2ComTerapiaO2;
    }

    public Integer getSaturacaoO2Aa() {
        return saturacaoO2Aa;
    }

    public void setSaturacaoO2Aa(Integer saturacaoO2Aa) {
        this.saturacaoO2Aa = saturacaoO2Aa;
    }

    public BigDecimal getGlicemiaCapilar() {
        return glicemiaCapilar;
    }

    public void setGlicemiaCapilar(BigDecimal glicemiaCapilar) {
        this.glicemiaCapilar = glicemiaCapilar;
    }

    public BigDecimal getPesoKg() {
        return pesoKg;
    }

    public void setPesoKg(BigDecimal pesoKg) {
        this.pesoKg = pesoKg;
    }

    public BigDecimal getAlturaCm() {
        return alturaCm;
    }

    public void setAlturaCm(BigDecimal alturaCm) {
        this.alturaCm = alturaCm;
    }

    public Integer getHgt() {
        return hgt;
    }

    public void setHgt(Integer hgt) {
        this.hgt = hgt;
    }

    public Integer getPerfusaoCapilarPerifericaSeg() {
        return perfusaoCapilarPerifericaSeg;
    }

    public void setPerfusaoCapilarPerifericaSeg(Integer perfusaoCapilarPerifericaSeg) {
        this.perfusaoCapilarPerifericaSeg = perfusaoCapilarPerifericaSeg;
    }

    public Integer getPreenchimentoCapilarCentralSeg() {
        return preenchimentoCapilarCentralSeg;
    }

    public void setPreenchimentoCapilarCentralSeg(Integer preenchimentoCapilarCentralSeg) {
        this.preenchimentoCapilarCentralSeg = preenchimentoCapilarCentralSeg;
    }

    public Long getReguaDorId() {
        return reguaDorId;
    }

    public void setReguaDorId(Long reguaDorId) {
        this.reguaDorId = reguaDorId;
    }

    public Long getAreaExecucaoId() {
        return areaExecucaoId;
    }

    public void setAreaExecucaoId(Long areaExecucaoId) {
        this.areaExecucaoId = areaExecucaoId;
    }

    public Long getGlasgowAberturaOcularId() {
        return glasgowAberturaOcularId;
    }

    public void setGlasgowAberturaOcularId(Long glasgowAberturaOcularId) {
        this.glasgowAberturaOcularId = glasgowAberturaOcularId;
    }

    public Long getGlasgowRespostaVerbalId() {
        return glasgowRespostaVerbalId;
    }

    public void setGlasgowRespostaVerbalId(Long glasgowRespostaVerbalId) {
        this.glasgowRespostaVerbalId = glasgowRespostaVerbalId;
    }

    public Long getGlasgowRespostaMotoraId() {
        return glasgowRespostaMotoraId;
    }

    public void setGlasgowRespostaMotoraId(Long glasgowRespostaMotoraId) {
        this.glasgowRespostaMotoraId = glasgowRespostaMotoraId;
    }

    public Long getGlasgowRespostaPupilarId() {
        return glasgowRespostaPupilarId;
    }

    public void setGlasgowRespostaPupilarId(Long glasgowRespostaPupilarId) {
        this.glasgowRespostaPupilarId = glasgowRespostaPupilarId;
    }

    public Integer getGlasgowTotal() {
        return glasgowTotal;
    }

    public void setGlasgowTotal(Integer glasgowTotal) {
        this.glasgowTotal = glasgowTotal;
    }

    public String getQueixaPrincipal() {
        return queixaPrincipal;
    }

    public void setQueixaPrincipal(String queixaPrincipal) {
        this.queixaPrincipal = queixaPrincipal;
    }

    public String getMedicacoesUsoContinuo() {
        return medicacoesUsoContinuo;
    }

    public void setMedicacoesUsoContinuo(String medicacoesUsoContinuo) {
        this.medicacoesUsoContinuo = medicacoesUsoContinuo;
    }

    public String getDiscriminador() {
        return discriminador;
    }

    public void setDiscriminador(String discriminador) {
        this.discriminador = discriminador;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public List<AlergiaItem> getAlergias() {
        return alergias;
    }

    public void setAlergias(List<AlergiaItem> alergias) {
        this.alergias = alergias;
    }

    public List<Long> getComorbidadeIds() {
        return comorbidadeIds;
    }

    public void setComorbidadeIds(List<Long> comorbidadeIds) {
        this.comorbidadeIds = comorbidadeIds;
    }

    public List<Long> getAvcSinalAlertaIds() {
        return avcSinalAlertaIds;
    }

    public void setAvcSinalAlertaIds(List<Long> avcSinalAlertaIds) {
        this.avcSinalAlertaIds = avcSinalAlertaIds;
    }
}
