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

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "classificacao_perfusao")
public class ClassificacaoPerfusao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "perfusao_capilar_periferica_seg")
    private Integer perfusaoCapilarPerifericaSeg;

    @Column(name = "preenchimento_capilar_central_seg")
    private Integer preenchimentoCapilarCentralSeg;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
}
