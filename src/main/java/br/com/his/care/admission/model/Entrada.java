package br.com.his.care.admission.model;

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
import br.com.his.patient.model.lookup.Procedencia;
import br.com.his.patient.model.lookup.Profissao;
import br.com.his.patient.model.lookup.TipoProcedencia;
import br.com.his.reference.location.model.Bairro;
import br.com.his.reference.location.model.Cidade;

@Entity
@Table(name = "entrada")
public class Entrada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "atendimento_id", nullable = false, unique = true)
    private Atendimento atendimento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "area_id")
    private Area area;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "forma_chegada_id")
    private FormaChegada formaChegada;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "situacao_ocupacional_id")
    private SituacaoOcupacional situacaoOcupacional;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profissao_id")
    private Profissao profissao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "procedencia_id")
    private Procedencia procedencia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_procedencia_id")
    private TipoProcedencia tipoProcedencia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "procedencia_bairro_id")
    private Bairro procedenciaBairro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "procedencia_cidade_id")
    private Cidade procedenciaCidade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "motivo_entrada_id")
    private MotivoEntrada motivoEntrada;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grau_parentesco_id")
    private GrauParentesco grauParentesco;

    @Column(name = "data_hora_entrada", nullable = false)
    private LocalDateTime dataHoraEntrada;

    @Column(name = "telefone_comunicante", length = 30)
    private String telefoneComunicante;

    @Column(length = 150)
    private String comunicante;

    @Column(name = "informacao_ad_chegada", length = 500)
    private String informacaoAdChegada;

    @Column(name = "procedencia_observacao", length = 200)
    private String procedenciaObservacao;

    @Column(name = "profissao_observacao", length = 150)
    private String profissaoObservacao;

    @Column(name = "tempo_servico", length = 120)
    private String tempoServico;

    @Column(length = 500)
    private String observacoes;

    @Column(length = 120)
    private String convenio;

    @Column(length = 120)
    private String guia;

    @Column(name = "atualizado_em", nullable = false)
    private LocalDateTime atualizadoEm;

    @Column(name = "atualizado_por", length = 120)
    private String atualizadoPor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "atualizado_por_usuario_id")
    private Usuario atualizadoPorUsuario;

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

    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    public Procedencia getProcedencia() {
        return procedencia;
    }

    public void setProcedencia(Procedencia procedencia) {
        this.procedencia = procedencia;
    }

    public TipoProcedencia getTipoProcedencia() {
        return tipoProcedencia;
    }

    public void setTipoProcedencia(TipoProcedencia tipoProcedencia) {
        this.tipoProcedencia = tipoProcedencia;
    }

    public Bairro getProcedenciaBairro() {
        return procedenciaBairro;
    }

    public void setProcedenciaBairro(Bairro procedenciaBairro) {
        this.procedenciaBairro = procedenciaBairro;
    }

    public Cidade getProcedenciaCidade() {
        return procedenciaCidade;
    }

    public void setProcedenciaCidade(Cidade procedenciaCidade) {
        this.procedenciaCidade = procedenciaCidade;
    }

    public FormaChegada getFormaChegada() {
        return formaChegada;
    }

    public void setFormaChegada(FormaChegada formaChegada) {
        this.formaChegada = formaChegada;
    }

    public MotivoEntrada getMotivoEntrada() {
        return motivoEntrada;
    }

    public void setMotivoEntrada(MotivoEntrada motivoEntrada) {
        this.motivoEntrada = motivoEntrada;
    }

    public LocalDateTime getDataHoraEntrada() {
        return dataHoraEntrada;
    }

    public void setDataHoraEntrada(LocalDateTime dataHoraEntrada) {
        this.dataHoraEntrada = dataHoraEntrada;
    }

    public GrauParentesco getGrauParentesco() {
        return grauParentesco;
    }

    public void setGrauParentesco(GrauParentesco grauParentesco) {
        this.grauParentesco = grauParentesco;
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

    public SituacaoOcupacional getSituacaoOcupacional() {
        return situacaoOcupacional;
    }

    public void setSituacaoOcupacional(SituacaoOcupacional situacaoOcupacional) {
        this.situacaoOcupacional = situacaoOcupacional;
    }

    public Profissao getProfissao() {
        return profissao;
    }

    public void setProfissao(Profissao profissao) {
        this.profissao = profissao;
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

    public LocalDateTime getAtualizadoEm() {
        return atualizadoEm;
    }

    public void setAtualizadoEm(LocalDateTime atualizadoEm) {
        this.atualizadoEm = atualizadoEm;
    }

    public String getAtualizadoPor() {
        return atualizadoPor;
    }

    public void setAtualizadoPor(String atualizadoPor) {
        this.atualizadoPor = atualizadoPor;
    }

    public Usuario getAtualizadoPorUsuario() {
        return atualizadoPorUsuario;
    }

    public void setAtualizadoPorUsuario(Usuario atualizadoPorUsuario) {
        this.atualizadoPorUsuario = atualizadoPorUsuario;
    }
}
