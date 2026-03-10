package br.com.his.patient.model.lookup;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "estado_civil")
public class EstadoCivil {
    @Id
    private Long id;
    @Column(nullable = false, length = 100)
    private String nome;
    @Column(length = 255)
    private String descricao;
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
}
