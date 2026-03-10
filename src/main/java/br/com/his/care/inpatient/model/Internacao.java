package br.com.his.care.inpatient.model;

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

@Entity
@Table(name = "internacao")
public class Internacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "atendimento_id", nullable = false, unique = true)
    private Atendimento atendimento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "origem_demanda_id")
    private InternacaoOrigemDemanda origemDemanda;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "perfil_internacao_id")
    private InternacaoPerfil perfilInternacao;

    @Column(name = "data_hora_decisao_internacao", nullable = false)
    private LocalDateTime dataHoraDecisaoInternacao;

    @Column(name = "data_hora_inicio_internacao")
    private LocalDateTime dataHoraInicioInternacao;

    @Column(name = "data_hora_fim_internacao")
    private LocalDateTime dataHoraFimInternacao;

    @Column(name = "data_hora_cancelamento")
    private LocalDateTime dataHoraCancelamento;

    @Column(name = "cancelado_por", length = 100)
    private String canceladoPor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cancelado_por_usuario_id")
    private Usuario canceladoPorUsuario;

    @Column(name = "motivo_cancelamento", columnDefinition = "text")
    private String motivoCancelamento;

    @Column(columnDefinition = "text")
    private String observacao;

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

    public InternacaoOrigemDemanda getOrigemDemanda() {
        return origemDemanda;
    }

    public void setOrigemDemanda(InternacaoOrigemDemanda origemDemanda) {
        this.origemDemanda = origemDemanda;
    }

    public InternacaoPerfil getPerfilInternacao() {
        return perfilInternacao;
    }

    public void setPerfilInternacao(InternacaoPerfil perfilInternacao) {
        this.perfilInternacao = perfilInternacao;
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

    public LocalDateTime getDataHoraCancelamento() {
        return dataHoraCancelamento;
    }

    public void setDataHoraCancelamento(LocalDateTime dataHoraCancelamento) {
        this.dataHoraCancelamento = dataHoraCancelamento;
    }

    public String getCanceladoPor() {
        return canceladoPor;
    }

    public void setCanceladoPor(String canceladoPor) {
        this.canceladoPor = canceladoPor;
    }

    public Usuario getCanceladoPorUsuario() {
        return canceladoPorUsuario;
    }

    public void setCanceladoPorUsuario(Usuario canceladoPorUsuario) {
        this.canceladoPorUsuario = canceladoPorUsuario;
    }

    public String getMotivoCancelamento() {
        return motivoCancelamento;
    }

    public void setMotivoCancelamento(String motivoCancelamento) {
        this.motivoCancelamento = motivoCancelamento;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}
