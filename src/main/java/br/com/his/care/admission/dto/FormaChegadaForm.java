package br.com.his.care.admission.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import br.com.his.care.admission.model.PerfilChegada;

public class FormaChegadaForm {

    @NotBlank(message = "Descricao e obrigatoria")
    @Size(max = 150)
    private String descricao;

    @NotNull(message = "Perfil de chegada e obrigatorio")
    private PerfilChegada perfilChegada = PerfilChegada.VERTICAL;

    private boolean ativo = true;

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public PerfilChegada getPerfilChegada() {
        return perfilChegada;
    }

    public void setPerfilChegada(PerfilChegada perfilChegada) {
        this.perfilChegada = perfilChegada;
    }
}
