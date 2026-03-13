package br.com.his.care.scheduling.model;

import java.time.LocalDateTime;
import java.time.LocalTime;

import br.com.his.access.model.Usuario;
import br.com.his.patient.model.Paciente;
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

@Entity
@Table(name = "agenda_especialidade_paciente")
public class AgendaEspecialidadePaciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agenda_especialidade_id", nullable = false)
    private AgendaEspecialidade agendaEspecialidade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agenda_slot_id", nullable = false)
    private AgendaEspecialidadeSlot agendaSlot;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_vaga", nullable = false, length = 20)
    private TipoVagaAgenda tipoVaga;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private StatusAgendamentoPaciente status = StatusAgendamentoPaciente.PENDENTE;

    @Column(name = "observacao", length = 255)
    private String observacao;

    @Column(name = "hora_atendimento", nullable = false)
    private LocalTime horaAtendimento;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;

    @Column(name = "criado_por", length = 120)
    private String criadoPor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "criado_por_usuario_id")
    private Usuario criadoPorUsuario;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AgendaEspecialidade getAgendaEspecialidade() {
        return agendaEspecialidade;
    }

    public void setAgendaEspecialidade(AgendaEspecialidade agendaEspecialidade) {
        this.agendaEspecialidade = agendaEspecialidade;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public AgendaEspecialidadeSlot getAgendaSlot() {
        return agendaSlot;
    }

    public void setAgendaSlot(AgendaEspecialidadeSlot agendaSlot) {
        this.agendaSlot = agendaSlot;
    }

    public TipoVagaAgenda getTipoVaga() {
        return tipoVaga;
    }

    public void setTipoVaga(TipoVagaAgenda tipoVaga) {
        this.tipoVaga = tipoVaga;
    }

    public StatusAgendamentoPaciente getStatus() {
        return status;
    }

    public void setStatus(StatusAgendamentoPaciente status) {
        this.status = status;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public LocalTime getHoraAtendimento() {
        return horaAtendimento;
    }

    public void setHoraAtendimento(LocalTime horaAtendimento) {
        this.horaAtendimento = horaAtendimento;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

    public String getCriadoPor() {
        return criadoPor;
    }

    public void setCriadoPor(String criadoPor) {
        this.criadoPor = criadoPor;
    }

    public Usuario getCriadoPorUsuario() {
        return criadoPorUsuario;
    }

    public void setCriadoPorUsuario(Usuario criadoPorUsuario) {
        this.criadoPorUsuario = criadoPorUsuario;
    }
}
