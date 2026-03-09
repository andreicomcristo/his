package br.com.his.admin.dto;

import jakarta.validation.constraints.NotNull;

public class UsuarioVinculoForm {

    @NotNull(message = "Unidade e obrigatoria")
    private Long unidadeId;

    @NotNull(message = "Perfil e obrigatorio")
    private Long perfilId;

    public Long getUnidadeId() {
        return unidadeId;
    }

    public void setUnidadeId(Long unidadeId) {
        this.unidadeId = unidadeId;
    }

    public Long getPerfilId() {
        return perfilId;
    }

    public void setPerfilId(Long perfilId) {
        this.perfilId = perfilId;
    }
}
