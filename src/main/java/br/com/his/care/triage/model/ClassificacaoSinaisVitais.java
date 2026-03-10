package br.com.his.care.triage.model;

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

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "classificacao_sinais_vitais")
public class ClassificacaoSinaisVitais {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pressao_arterial", length = 20)
    private String pressaoArterial;

    @Column(precision = 5, scale = 2)
    private BigDecimal temperatura;

    @Column(name = "frequencia_cardiaca")
    private Integer frequenciaCardiaca;

    @Column(name = "saturacao_o2")
    private Integer saturacaoO2;

    @Column(name = "frequencia_respiratoria")
    private Integer frequenciaRespiratoria;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
}
