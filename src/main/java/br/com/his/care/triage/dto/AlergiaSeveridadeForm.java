package br.com.his.care.triage.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AlergiaSeveridadeForm {

    @NotBlank(message = "Descricao e obrigatoria")
    @Size(max = 120)
    private String descricao;

    private boolean ativo = true;

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
