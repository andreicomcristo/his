package br.com.his.patient.model.lookup;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "nacionalidade")
public class Nacionalidade {
    @Id
    private Long id;
    @Column(nullable = false, length = 150)
    private String descricao;
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
}
