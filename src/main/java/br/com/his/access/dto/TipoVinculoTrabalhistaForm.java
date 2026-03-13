package br.com.his.access.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class TipoVinculoTrabalhistaForm {

    @NotBlank(message = "Informe o codigo")
    @Size(max = 60, message = "Codigo deve ter no maximo 60 caracteres")
    private String codigo;

    @NotBlank(message = "Informe a descricao")
    @Size(max = 120, message = "Descricao deve ter no maximo 120 caracteres")
    private String descricao;

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

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
}
