package br.com.his.reference.location.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class BairroForm {

    @NotNull(message = "UF e obrigatoria")
    private Long unidadeFederativaId;

    @NotNull(message = "Municipio e obrigatoria")
    private Long municipioId;

    @NotBlank(message = "Descricao e obrigatoria")
    @Size(max = 100)
    private String descricao;

    public Long getUnidadeFederativaId() {
        return unidadeFederativaId;
    }

    public void setUnidadeFederativaId(Long unidadeFederativaId) {
        this.unidadeFederativaId = unidadeFederativaId;
    }

    public Long getMunicipioId() {
        return municipioId;
    }

    public void setMunicipioId(Long municipioId) {
        this.municipioId = municipioId;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

}

