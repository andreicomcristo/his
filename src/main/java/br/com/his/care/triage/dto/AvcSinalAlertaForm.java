package br.com.his.care.triage.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class AvcSinalAlertaForm {

    @NotBlank(message = "Descricao e obrigatoria")
    @Size(max = 180)
    private String descricao;

    @NotNull(message = "Ordem e obrigatoria")
    @Max(value = 9999, message = "Ordem maxima permitida e 9999")
    private Integer ordemExibicao = 0;

    private boolean ativo = true;

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Integer getOrdemExibicao() {
        return ordemExibicao;
    }

    public void setOrdemExibicao(Integer ordemExibicao) {
        this.ordemExibicao = ordemExibicao;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
}
