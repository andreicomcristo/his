package br.com.his.care.scheduling.dto;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class EspecialidadeForm {

    @NotBlank(message = "Codigo e obrigatorio")
    @Size(max = 80)
    private String codigo;

    @NotBlank(message = "Descricao e obrigatoria")
    @Size(max = 120)
    private String descricao;

    private List<Long> cargoColaboradorIds = new ArrayList<>();

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

    public List<Long> getCargoColaboradorIds() {
        return cargoColaboradorIds;
    }

    public void setCargoColaboradorIds(List<Long> cargoColaboradorIds) {
        this.cargoColaboradorIds = cargoColaboradorIds;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
}
