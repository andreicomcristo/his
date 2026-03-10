package br.com.his.access.model;

import br.com.his.reference.location.model.Cidade;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "unidade")
public class Unidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(name = "tipo_estabelecimento", length = 80)
    private String tipoEstabelecimento;

    @Column(length = 20)
    private String cnes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cidade_id")
    private Cidade cidade;

    @Column(nullable = false)
    private boolean ativo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTipoEstabelecimento() {
        return tipoEstabelecimento;
    }

    public void setTipoEstabelecimento(String tipoEstabelecimento) {
        this.tipoEstabelecimento = tipoEstabelecimento;
    }

    public String getCnes() {
        return cnes;
    }

    public void setCnes(String cnes) {
        this.cnes = cnes;
    }

    public Cidade getCidade() {
        return cidade;
    }

    public void setCidade(Cidade cidade) {
        this.cidade = cidade;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
}
