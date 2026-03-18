package br.com.his.care.scheduling.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class EspecialidadeForm {

    @NotBlank(message = "Codigo e obrigatorio")
    @Size(max = 80)
    private String codigo;

    @NotBlank(message = "Descricao e obrigatoria")
    @Size(max = 120)
    private String descricao;

    @NotNull(message = "Cargo assistencial e obrigatorio")
    private Long cargoColaboradorId;

    private boolean ativo = true;

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Long getCargoColaboradorId() {
        return cargoColaboradorId;
    }

    public void setCargoColaboradorId(Long cargoColaboradorId) {
        this.cargoColaboradorId = cargoColaboradorId;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
}
