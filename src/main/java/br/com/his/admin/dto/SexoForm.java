package br.com.his.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class SexoForm {

    @NotBlank(message = "Codigo e obrigatorio")
    @Size(max = 10)
    @Pattern(regexp = "^[A-Za-z0-9_]+$", message = "Codigo deve conter apenas letras, numeros ou underscore")
    private String codigo;

    @NotBlank(message = "Descricao e obrigatoria")
    @Size(max = 60)
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
