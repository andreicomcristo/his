package br.com.his.care.inpatient.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class AreaForm {

    @NotNull(message = "Unidade e obrigatoria")
    private Long unidadeId;

    @NotBlank(message = "Descricao e obrigatoria")
    @Size(max = 150)
    private String descricao;

    @Size(max = 500)
    private String detalhamento;

    public Long getUnidadeId() {
        return unidadeId;
    }

    public void setUnidadeId(Long unidadeId) {
        this.unidadeId = unidadeId;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getDetalhamento() {
        return detalhamento;
    }

    public void setDetalhamento(String detalhamento) {
        this.detalhamento = detalhamento;
    }
}
