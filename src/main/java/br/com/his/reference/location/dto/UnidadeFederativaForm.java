package br.com.his.reference.location.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UnidadeFederativaForm {

    @NotBlank(message = "Descricao e obrigatoria")
    @Size(max = 100)
    private String descricao;

    @NotBlank(message = "Sigla e obrigatoria")
    @Size(max = 2)
    private String sigla;

    @Size(max = 2)
    private String codigoIbge;

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

    public String getCodigoIbge() {
        return codigoIbge;
    }

    public void setCodigoIbge(String codigoIbge) {
        this.codigoIbge = codigoIbge;
    }
}
