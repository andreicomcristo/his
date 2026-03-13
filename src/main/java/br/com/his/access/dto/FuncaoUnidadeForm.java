package br.com.his.access.dto;

import br.com.his.access.model.TipoNaturezaAtuacao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class FuncaoUnidadeForm {

    @NotBlank(message = "Informe o codigo")
    @Size(max = 80, message = "Codigo deve ter no maximo 80 caracteres")
    private String codigo;

    @NotBlank(message = "Informe a descricao")
    @Size(max = 150, message = "Descricao deve ter no maximo 150 caracteres")
    private String descricao;

    @NotNull(message = "Informe o tipo da funcao")
    private TipoNaturezaAtuacao tipoFuncao;

    private boolean requerEspecialidade;

    private boolean ativo = true;

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
