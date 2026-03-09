package br.com.his.assistencial.model;

import java.time.LocalDateTime;

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
@Table(name = "classificacao_risco")
public class ClassificacaoRisco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "atendimento_id", nullable = false)
    private Atendimento atendimento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classificacao_cor_id")
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
    @JoinColumn(name = "regua_dor_id")
    private ReguaDor reguaDor;

    @Column(name = "medicacoes_uso_continuo", length = 500)
    private String medicacoesUsoContinuo;

    @Column(length = 2000)
    private String discriminador;

    @Column(columnDefinition = "TEXT")
    private String observacao;

    @Column(name = "queixa_principal", columnDefinition = "TEXT")
    private String queixaPrincipal;

    @Column(name = "data_inicio", nullable = false)
    private LocalDateTime dataInicio;

    @Column(name = "data_fim")
    private LocalDateTime dataFim;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Atendimento getAtendimento() {
        return atendimento;
    }

    public void setAtendimento(Atendimento atendimento) {
        this.atendimento = atendimento;
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

    public String getQueixaPrincipal() {
        return queixaPrincipal;
    }

    public void setQueixaPrincipal(String queixaPrincipal) {
        this.queixaPrincipal = queixaPrincipal;
    }

    public LocalDateTime getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDateTime dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDateTime getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDateTime dataFim) {
        this.dataFim = dataFim;
    }
}
