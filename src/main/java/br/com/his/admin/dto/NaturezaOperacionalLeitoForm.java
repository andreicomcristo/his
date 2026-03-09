package br.com.his.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class NaturezaOperacionalLeitoForm {

    @NotBlank(message = "Codigo e obrigatorio")
    @Size(max = 40)
    private String codigo;

    @NotBlank(message = "Descricao e obrigatoria")
    @Size(max = 80)
    private String descricao;

    private boolean consideraTaxaNominal = true;

    private boolean consideraTaxaOperacional = true;

    private boolean virtualSuperlotacao = false;

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

    public boolean isConsideraTaxaNominal() {
        return consideraTaxaNominal;
    }

    public void setConsideraTaxaNominal(boolean consideraTaxaNominal) {
        this.consideraTaxaNominal = consideraTaxaNominal;
    }

    public boolean isConsideraTaxaOperacional() {
        return consideraTaxaOperacional;
    }

    public void setConsideraTaxaOperacional(boolean consideraTaxaOperacional) {
        this.consideraTaxaOperacional = consideraTaxaOperacional;
    }

    public boolean isVirtualSuperlotacao() {
        return virtualSuperlotacao;
    }

    public void setVirtualSuperlotacao(boolean virtualSuperlotacao) {
        this.virtualSuperlotacao = virtualSuperlotacao;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
}
