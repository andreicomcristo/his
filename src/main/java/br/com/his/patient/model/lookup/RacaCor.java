package br.com.his.patient.model.lookup;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "raca_cor")
public class RacaCor {

    @Id
    private Long id;

    @Column(nullable = false, length = 120)
    private String descricao;

    @Column(length = 10)
    private String codigo;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
}
