package br.com.his.access.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UnidadeAdminRequest {

    @NotBlank
    @Size(max = 150)
    private String nome;

    @NotNull
    private Long tipoUnidadeId;

    @Size(max = 20)
    private String sigla;

    @Size(max = 20)
    private String cnes;

    @NotNull
    private Long unidadeFederativaId;

    @NotNull
    private Long cidadeId;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Long getTipoUnidadeId() {
        return tipoUnidadeId;
    }

    public void setTipoUnidadeId(Long tipoUnidadeId) {
        this.tipoUnidadeId = tipoUnidadeId;
    }

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

    public String getCnes() {
        return cnes;
    }

    public void setCnes(String cnes) {
        this.cnes = cnes;
    }

    public Long getUnidadeFederativaId() {
        return unidadeFederativaId;
    }

    public void setUnidadeFederativaId(Long unidadeFederativaId) {
        this.unidadeFederativaId = unidadeFederativaId;
    }

    public Long getCidadeId() {
        return cidadeId;
    }

    public void setCidadeId(Long cidadeId) {
        this.cidadeId = cidadeId;
    }
}
