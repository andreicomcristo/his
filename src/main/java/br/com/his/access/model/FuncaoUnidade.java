package br.com.his.access.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "funcao_unidade")
public class FuncaoUnidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String codigo;

    @Column(nullable = false, length = 150)
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_funcao", nullable = false, length = 20)
    private TipoNaturezaAtuacao tipoFuncao;

    @Column(name = "requer_especialidade", nullable = false)
    private boolean requerEspecialidade;

    @Column(nullable = false)
    private boolean ativo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public TipoNaturezaAtuacao getTipoFuncao() {
        return tipoFuncao;
    }

    public void setTipoFuncao(TipoNaturezaAtuacao tipoFuncao) {
        this.tipoFuncao = tipoFuncao;
    }

    public boolean isRequerEspecialidade() {
        return requerEspecialidade;
    }

    public void setRequerEspecialidade(boolean requerEspecialidade) {
        this.requerEspecialidade = requerEspecialidade;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
}
