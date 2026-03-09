package br.com.his.paciente.model;

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
import jakarta.persistence.Table;

@Entity
@Table(name = "paciente_merge_log")
public class PacienteMergeLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_paciente_id", nullable = false)
    private Paciente fromPaciente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_paciente_id", nullable = false)
    private Paciente toPaciente;

    @Column(columnDefinition = "TEXT")
    private String motivo;

    @Column(name = "merged_em", nullable = false)
    private LocalDateTime mergedEm;

    @Column(name = "merged_por", nullable = false, length = 100)
    private String mergedPor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merged_por_usuario_id")
    private Usuario mergedPorUsuario;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Paciente getFromPaciente() {
        return fromPaciente;
    }

    public void setFromPaciente(Paciente fromPaciente) {
        this.fromPaciente = fromPaciente;
    }

    public Paciente getToPaciente() {
        return toPaciente;
    }

    public void setToPaciente(Paciente toPaciente) {
        this.toPaciente = toPaciente;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public LocalDateTime getMergedEm() {
        return mergedEm;
    }

    public void setMergedEm(LocalDateTime mergedEm) {
        this.mergedEm = mergedEm;
    }

    public String getMergedPor() {
        return mergedPor;
    }

    public void setMergedPor(String mergedPor) {
        this.mergedPor = mergedPor;
    }

    public Usuario getMergedPorUsuario() {
        return mergedPorUsuario;
    }

    public void setMergedPorUsuario(Usuario mergedPorUsuario) {
        this.mergedPorUsuario = mergedPorUsuario;
    }
}
