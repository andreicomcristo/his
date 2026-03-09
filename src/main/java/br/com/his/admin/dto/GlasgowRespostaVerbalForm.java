package br.com.his.admin.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class GlasgowRespostaVerbalForm {

    @NotNull(message = "Pontuacao e obrigatoria")
    @Min(value = 1, message = "Pontuacao minima e 1")
    @Max(value = 5, message = "Pontuacao maxima e 5")
    private Integer pontos;

    @NotBlank(message = "Descricao e obrigatoria")
    @Size(max = 120)
    private String descricao;

    private boolean ativo = true;

    public Integer getPontos() {
        return pontos;
    }

    public void setPontos(Integer pontos) {
        this.pontos = pontos;
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
