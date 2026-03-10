package br.com.his.patient.model.lookup;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "naturalidade")
public class Naturalidade {
    @Id
    private Long id;
    @Column(nullable = false, length = 150)
    private String descricao;
    @Column(name = "cod_ibge", length = 20)
    private String codIbge;
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getCodIbge() { return codIbge; }
    public void setCodIbge(String codIbge) { this.codIbge = codIbge; }
}
