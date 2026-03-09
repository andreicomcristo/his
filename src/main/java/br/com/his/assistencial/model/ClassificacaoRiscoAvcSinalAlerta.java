package br.com.his.assistencial.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "classificacao_risco_avc_sinal_alerta")
public class ClassificacaoRiscoAvcSinalAlerta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classificacao_risco_id", nullable = false)
    private ClassificacaoRisco classificacaoRisco;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "avc_sinal_alerta_id", nullable = false)
    private AvcSinalAlerta avcSinalAlerta;

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

    public AvcSinalAlerta getAvcSinalAlerta() {
        return avcSinalAlerta;
    }

    public void setAvcSinalAlerta(AvcSinalAlerta avcSinalAlerta) {
        this.avcSinalAlerta = avcSinalAlerta;
    }
}
