package br.com.his.admin.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ClassificacaoCorForm {

    @NotBlank(message = "Descricao e obrigatoria")
    @Size(max = 40)
    private String descricao;

    @Pattern(regexp = "^$|^#[0-9A-Fa-f]{6}$", message = "Cor deve estar no formato #RRGGBB")
    private String cor;

    @NotNull(message = "Ordem e obrigatoria")
    @Max(value = 9999, message = "Ordem maxima permitida e 9999")
    private Integer ordemExibicao = 0;

    private boolean riscoMaior;

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

    public Integer getOrdemExibicao() {
        return ordemExibicao;
    }

    public void setOrdemExibicao(Integer ordemExibicao) {
        this.ordemExibicao = ordemExibicao;
    }

    public boolean isRiscoMaior() {
        return riscoMaior;
    }

    public void setRiscoMaior(boolean riscoMaior) {
        this.riscoMaior = riscoMaior;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
}
