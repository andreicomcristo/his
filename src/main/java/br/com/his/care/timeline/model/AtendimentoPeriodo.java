package br.com.his.care.timeline.model;

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
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "atendimento_periodo")
public class AtendimentoPeriodo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "atendimento_id", nullable = false)
    private Atendimento atendimento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private AtendimentoPeriodoTipo tipo;

    @Column(name = "inicio_em", nullable = false)
    private LocalDateTime inicioEm;

    @Column(name = "fim_em")
    private LocalDateTime fimEm;

    @Column(name = "usuario_inicio", length = 120)
    private String usuarioInicio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_inicio_id")
    private Usuario usuarioInicioUsuario;

    @Column(name = "usuario_fim", length = 120)
    private String usuarioFim;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_fim_id")
    private Usuario usuarioFimUsuario;

    @Column(columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private String metadata;

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

    public AtendimentoPeriodoTipo getTipo() {
        return tipo;
    }

    public void setTipo(AtendimentoPeriodoTipo tipo) {
        this.tipo = tipo;
    }

    public LocalDateTime getInicioEm() {
        return inicioEm;
    }

    public void setInicioEm(LocalDateTime inicioEm) {
        this.inicioEm = inicioEm;
    }

    public LocalDateTime getFimEm() {
        return fimEm;
    }

    public void setFimEm(LocalDateTime fimEm) {
        this.fimEm = fimEm;
    }

    public String getUsuarioInicio() {
        return usuarioInicio;
    }

    public void setUsuarioInicio(String usuarioInicio) {
        this.usuarioInicio = usuarioInicio;
    }

    public Usuario getUsuarioInicioUsuario() {
        return usuarioInicioUsuario;
    }

    public void setUsuarioInicioUsuario(Usuario usuarioInicioUsuario) {
        this.usuarioInicioUsuario = usuarioInicioUsuario;
    }

    public String getUsuarioFim() {
        return usuarioFim;
    }

    public void setUsuarioFim(String usuarioFim) {
        this.usuarioFim = usuarioFim;
    }

    public Usuario getUsuarioFimUsuario() {
        return usuarioFimUsuario;
    }

    public void setUsuarioFimUsuario(Usuario usuarioFimUsuario) {
        this.usuarioFimUsuario = usuarioFimUsuario;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }
}
