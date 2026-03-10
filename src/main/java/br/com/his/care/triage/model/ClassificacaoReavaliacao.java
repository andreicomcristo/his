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

import br.com.his.access.model.Usuario;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "classificacao_reavaliacao")
public class ClassificacaoReavaliacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classificacao_risco_id", nullable = false)
    private ClassificacaoRisco classificacaoRisco;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classificacao_cor_id", nullable = false)
    private ClassificacaoCor classificacaoCor;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "classificacao_sinais_vitais_id")
    private ClassificacaoSinaisVitais sinaisVitais;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "classificacao_oxigenacao_id")
    private ClassificacaoOxigenacao oxigenacao;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "classificacao_glicemia_id")
    private ClassificacaoGlicemia glicemia;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "classificacao_antropometria_id")
    private ClassificacaoAntropometria antropometria;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "classificacao_perfusao_id")
    private ClassificacaoPerfusao perfusao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "regua_dor_id", nullable = false)
    private ReguaDor reguaDor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "glasgow_abertura_ocular_id")
    private GlasgowAberturaOcular glasgowAberturaOcular;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "glasgow_resposta_verbal_id")
    private GlasgowRespostaVerbal glasgowRespostaVerbal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "glasgow_resposta_motora_id")
    private GlasgowRespostaMotora glasgowRespostaMotora;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "glasgow_resposta_pupilar_id")
    private GlasgowRespostaPupilar glasgowRespostaPupilar;

    @Column(name = "glasgow_total")
    private Integer glasgowTotal;

    @Column(length = 2000)
    private String discriminador;

    @Column(columnDefinition = "TEXT")
    private String observacao;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    @Column(length = 120)
    private String usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuarioRegistro;

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

    public ClassificacaoCor getClassificacaoCor() {
        return classificacaoCor;
    }

    public void setClassificacaoCor(ClassificacaoCor classificacaoCor) {
        this.classificacaoCor = classificacaoCor;
    }

    public ClassificacaoSinaisVitais getSinaisVitais() {
        return sinaisVitais;
    }

    public void setSinaisVitais(ClassificacaoSinaisVitais sinaisVitais) {
        this.sinaisVitais = sinaisVitais;
    }

    public ClassificacaoOxigenacao getOxigenacao() {
        return oxigenacao;
    }

    public void setOxigenacao(ClassificacaoOxigenacao oxigenacao) {
        this.oxigenacao = oxigenacao;
    }

    public ClassificacaoGlicemia getGlicemia() {
        return glicemia;
    }

    public void setGlicemia(ClassificacaoGlicemia glicemia) {
        this.glicemia = glicemia;
    }

    public ClassificacaoAntropometria getAntropometria() {
        return antropometria;
    }

    public void setAntropometria(ClassificacaoAntropometria antropometria) {
        this.antropometria = antropometria;
    }

    public ClassificacaoPerfusao getPerfusao() {
        return perfusao;
    }

    public void setPerfusao(ClassificacaoPerfusao perfusao) {
        this.perfusao = perfusao;
    }

    public ReguaDor getReguaDor() {
        return reguaDor;
    }

    public void setReguaDor(ReguaDor reguaDor) {
        this.reguaDor = reguaDor;
    }

    public GlasgowAberturaOcular getGlasgowAberturaOcular() {
        return glasgowAberturaOcular;
    }

    public void setGlasgowAberturaOcular(GlasgowAberturaOcular glasgowAberturaOcular) {
        this.glasgowAberturaOcular = glasgowAberturaOcular;
    }

    public GlasgowRespostaVerbal getGlasgowRespostaVerbal() {
        return glasgowRespostaVerbal;
    }

    public void setGlasgowRespostaVerbal(GlasgowRespostaVerbal glasgowRespostaVerbal) {
        this.glasgowRespostaVerbal = glasgowRespostaVerbal;
    }

    public GlasgowRespostaMotora getGlasgowRespostaMotora() {
        return glasgowRespostaMotora;
    }

    public void setGlasgowRespostaMotora(GlasgowRespostaMotora glasgowRespostaMotora) {
        this.glasgowRespostaMotora = glasgowRespostaMotora;
    }

    public GlasgowRespostaPupilar getGlasgowRespostaPupilar() {
        return glasgowRespostaPupilar;
    }

    public void setGlasgowRespostaPupilar(GlasgowRespostaPupilar glasgowRespostaPupilar) {
        this.glasgowRespostaPupilar = glasgowRespostaPupilar;
    }

    public Integer getGlasgowTotal() {
        return glasgowTotal;
    }

    public void setGlasgowTotal(Integer glasgowTotal) {
        this.glasgowTotal = glasgowTotal;
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

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public Usuario getUsuarioRegistro() {
        return usuarioRegistro;
    }

    public void setUsuarioRegistro(Usuario usuarioRegistro) {
        this.usuarioRegistro = usuarioRegistro;
    }
}
