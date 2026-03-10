package br.com.his.access.api.dto;

import java.util.List;

public class PerfilPermissoesRequest {

    private List<Long> permissaoIds;

    public List<Long> getPermissaoIds() {
        return permissaoIds;
    }

    public void setPermissaoIds(List<Long> permissaoIds) {
        this.permissaoIds = permissaoIds;
    }
}
