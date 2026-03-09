package br.com.his.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class MotivoDesfechoForm {

    @NotBlank(message = "Descricao e obrigatoria")
    @Size(max = 100)
    private String descricao;

    @Pattern(regexp = "^$|^#[0-9A-Fa-f]{6}$", message = "Cor deve estar no formato #RRGGBB")
    private String cor;

    private boolean ativo = true;

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getCor() {
        return cor;
    }

    public void setCor(String cor) {
        this.cor = cor;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
}
