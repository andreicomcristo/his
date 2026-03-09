package br.com.his.paciente.model.lookup;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "profissao")
public class Profissao {
    @Id
    private Long id;
    @Column(nullable = false, length = 150)
    private String nome;
    @Column(name = "cbo_cod", length = 6)
    private String cboCod;
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCboCod() { return cboCod; }
    public void setCboCod(String cboCod) { this.cboCod = cboCod; }
}
