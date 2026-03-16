package br.com.his.patient.model.lookup;

import br.com.his.access.model.Unidade;
import br.com.his.reference.location.model.Bairro;
import br.com.his.reference.location.model.Municipio;
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
@Table(name = "procedencia")
public class Procedencia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 150)
    private String descricao;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_procedencia_id")
    private TipoProcedencia tipoProcedencia;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bairro_id")
    private Bairro bairro;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "municipio_id")
    private Municipio municipio;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unidade_id")
    private Unidade unidade;
    @Column(nullable = false)
    private boolean ativo = true;
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public TipoProcedencia getTipoProcedencia() { return tipoProcedencia; }
    public void setTipoProcedencia(TipoProcedencia tipoProcedencia) { this.tipoProcedencia = tipoProcedencia; }
    public Bairro getBairro() { return bairro; }
    public void setBairro(Bairro bairro) { this.bairro = bairro; }
    public Municipio getMunicipio() { return municipio; }
    public void setMunicipio(Municipio municipio) { this.municipio = municipio; }
    public Unidade getUnidade() { return unidade; }
    public void setUnidade(Unidade unidade) { this.unidade = unidade; }
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
}
