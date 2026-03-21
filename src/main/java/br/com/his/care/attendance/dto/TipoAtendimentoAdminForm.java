package br.com.his.care.attendance.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class TipoAtendimentoAdminForm {

    @NotBlank(message = "Codigo e obrigatorio")
    @Size(max = 40, message = "Codigo deve ter no maximo 40 caracteres")
    private String codigo;

    @NotBlank(message = "Descricao e obrigatoria")
    @Size(max = 120, message = "Descricao deve ter no maximo 120 caracteres")
    private String descricao;

    @NotNull(message = "Ordem e obrigatoria")
    @Min(value = 0, message = "Ordem deve ser maior ou igual a zero")
    @Max(value = 9999, message = "Ordem deve ser menor ou igual a 9999")
    private Integer ordemExibicao = 100;

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

    public Integer getOrdemExibicao() {
        return ordemExibicao;
    }

    public void setOrdemExibicao(Integer ordemExibicao) {
        this.ordemExibicao = ordemExibicao;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
}
