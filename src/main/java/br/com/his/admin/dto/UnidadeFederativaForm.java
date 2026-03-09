package br.com.his.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UnidadeFederativaForm {

    @NotBlank(message = "Nome e obrigatorio")
    @Size(max = 100)
    private String nome;

    @NotBlank(message = "Sigla e obrigatoria")
    @Size(max = 2)
    private String sigla;

    @Size(max = 2)
    private String codigoIbge;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
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
