package br.com.his.reference.location.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class MunicipioForm {

    @NotNull(message = "UF e obrigatoria")
    private Long unidadeFederativaId;

    @NotBlank(message = "Descricao e obrigatoria")
    @Size(max = 100)
    private String descricao;

    @Size(max = 5)
    private String codigoIbge;

    public Long getUnidadeFederativaId() {
        return unidadeFederativaId;
    }

    public void setUnidadeFederativaId(Long unidadeFederativaId) {
        this.unidadeFederativaId = unidadeFederativaId;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getCodigoIbge() {
        return codigoIbge;
    }

    public void setCodigoIbge(String codigoIbge) {
        this.codigoIbge = codigoIbge;
    }
}
