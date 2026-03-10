package br.com.his.access.api.dto;

import jakarta.validation.constraints.NotNull;

public class UsuarioVinculoRequest {

    @NotNull
    private Long unidadeId;

    @NotNull
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
