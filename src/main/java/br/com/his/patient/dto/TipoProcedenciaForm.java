package br.com.his.patient.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class TipoProcedenciaForm {

    @NotBlank(message = "Descricao e obrigatoria")
    @Size(max = 80, message = "Descricao deve ter no maximo 80 caracteres")
    private String descricao;

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
