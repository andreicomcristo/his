package br.com.his.patient.model.lookup;

import br.com.his.access.model.Unidade;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "procedencia")
public class Procedencia {
    @Id
    private Long id;
    @Column(nullable = false, length = 150)
    private String descricao;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_procedencia_id")
    private TipoProcedencia tipoProcedencia;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unidade_id")
    private Unidade unidade;
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public TipoProcedencia getTipoProcedencia() { return tipoProcedencia; }
    public void setTipoProcedencia(TipoProcedencia tipoProcedencia) { this.tipoProcedencia = tipoProcedencia; }
    public Unidade getUnidade() { return unidade; }
    public void setUnidade(Unidade unidade) { this.unidade = unidade; }
}
