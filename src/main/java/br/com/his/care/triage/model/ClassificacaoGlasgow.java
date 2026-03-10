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

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "classificacao_glasgow")
public class ClassificacaoGlasgow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classificacao_risco_id", nullable = false)
    private ClassificacaoRisco classificacaoRisco;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "glasgow_abertura_ocular_id", nullable = false)
    private GlasgowAberturaOcular aberturaOcular;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "glasgow_resposta_verbal_id", nullable = false)
    private GlasgowRespostaVerbal respostaVerbal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "glasgow_resposta_motora_id", nullable = false)
    private GlasgowRespostaMotora respostaMotora;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "glasgow_resposta_pupilar_id")
    private GlasgowRespostaPupilar respostaPupilar;

    @Column(nullable = false)
    private Integer total;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ClassificacaoRisco getClassificacaoRisco() {
        return classificacaoRisco;
    }

    public void setClassificacaoRisco(ClassificacaoRisco classificacaoRisco) {
        this.classificacaoRisco = classificacaoRisco;
    }

    public GlasgowAberturaOcular getAberturaOcular() {
        return aberturaOcular;
    }

    public void setAberturaOcular(GlasgowAberturaOcular aberturaOcular) {
        this.aberturaOcular = aberturaOcular;
    }

    public GlasgowRespostaVerbal getRespostaVerbal() {
        return respostaVerbal;
    }

    public void setRespostaVerbal(GlasgowRespostaVerbal respostaVerbal) {
        this.respostaVerbal = respostaVerbal;
    }

    public GlasgowRespostaMotora getRespostaMotora() {
        return respostaMotora;
    }

    public void setRespostaMotora(GlasgowRespostaMotora respostaMotora) {
        this.respostaMotora = respostaMotora;
    }

    public GlasgowRespostaPupilar getRespostaPupilar() {
        return respostaPupilar;
    }

    public void setRespostaPupilar(GlasgowRespostaPupilar respostaPupilar) {
        this.respostaPupilar = respostaPupilar;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }
}
