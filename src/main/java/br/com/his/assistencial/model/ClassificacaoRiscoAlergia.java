package br.com.his.assistencial.model;

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
@Table(name = "classificacao_risco_alergia")
public class ClassificacaoRiscoAlergia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classificacao_risco_id", nullable = false)
    private ClassificacaoRisco classificacaoRisco;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alergia_substancia_id", nullable = false)
    private AlergiaSubstancia substancia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alergia_severidade_id")
    private AlergiaSeveridade severidade;

    @Column(length = 500)
    private String descricao;

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

    public AlergiaSubstancia getSubstancia() {
        return substancia;
    }

    public void setSubstancia(AlergiaSubstancia substancia) {
        this.substancia = substancia;
    }

    public AlergiaSeveridade getSeveridade() {
        return severidade;
    }

    public void setSeveridade(AlergiaSeveridade severidade) {
        this.severidade = severidade;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
