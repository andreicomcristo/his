package br.com.his.care.scheduling.model;

import java.time.LocalDateTime;
import java.time.LocalTime;

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

@Entity
@Table(name = "agenda_especialidade_paciente_hist")
public class AgendaEspecialidadePacienteHistorico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agenda_especialidade_paciente_id", nullable = false)
    private AgendaEspecialidadePaciente agendaPaciente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agenda_origem_id")
    private AgendaEspecialidade agendaOrigem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agenda_destino_id")
    private AgendaEspecialidade agendaDestino;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "slot_origem_id")
    private AgendaEspecialidadeSlot slotOrigem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "slot_destino_id")
    private AgendaEspecialidadeSlot slotDestino;

    @Column(name = "horario_origem")
    private LocalTime horarioOrigem;

    @Column(name = "horario_destino")
    private LocalTime horarioDestino;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_anterior", length = 20)
    private StatusAgendamentoPaciente statusAnterior;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_novo", length = 20)
    private StatusAgendamentoPaciente statusNovo;

    @Enumerated(EnumType.STRING)
    @Column(name = "acao", nullable = false, length = 30)
    private AcaoAgendamentoHistorico acao;

    @Column(name = "observacao", length = 255)
    private String observacao;

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

    public AgendaEspecialidadePaciente getAgendaPaciente() {
        return agendaPaciente;
    }

    public void setAgendaPaciente(AgendaEspecialidadePaciente agendaPaciente) {
        this.agendaPaciente = agendaPaciente;
    }

    public AgendaEspecialidade getAgendaOrigem() {
        return agendaOrigem;
    }

    public void setAgendaOrigem(AgendaEspecialidade agendaOrigem) {
        this.agendaOrigem = agendaOrigem;
    }

    public AgendaEspecialidade getAgendaDestino() {
        return agendaDestino;
    }

    public void setAgendaDestino(AgendaEspecialidade agendaDestino) {
        this.agendaDestino = agendaDestino;
    }

    public AgendaEspecialidadeSlot getSlotOrigem() {
        return slotOrigem;
    }

    public void setSlotOrigem(AgendaEspecialidadeSlot slotOrigem) {
        this.slotOrigem = slotOrigem;
    }

    public AgendaEspecialidadeSlot getSlotDestino() {
        return slotDestino;
    }

    public void setSlotDestino(AgendaEspecialidadeSlot slotDestino) {
        this.slotDestino = slotDestino;
    }

    public LocalTime getHorarioOrigem() {
        return horarioOrigem;
    }

    public void setHorarioOrigem(LocalTime horarioOrigem) {
        this.horarioOrigem = horarioOrigem;
    }

    public LocalTime getHorarioDestino() {
        return horarioDestino;
    }

    public void setHorarioDestino(LocalTime horarioDestino) {
        this.horarioDestino = horarioDestino;
    }

    public StatusAgendamentoPaciente getStatusAnterior() {
        return statusAnterior;
    }

    public void setStatusAnterior(StatusAgendamentoPaciente statusAnterior) {
        this.statusAnterior = statusAnterior;
    }

    public StatusAgendamentoPaciente getStatusNovo() {
        return statusNovo;
    }

    public void setStatusNovo(StatusAgendamentoPaciente statusNovo) {
        this.statusNovo = statusNovo;
    }

    public AcaoAgendamentoHistorico getAcao() {
        return acao;
    }

    public void setAcao(AcaoAgendamentoHistorico acao) {
        this.acao = acao;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
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
