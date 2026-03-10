package br.com.his.access.api.dto;

import java.util.List;

public class UsuarioAdminDetalheResponse extends UsuarioAdminResponse {

    private List<UsuarioVinculoResponse> vinculos;

    public List<UsuarioVinculoResponse> getVinculos() {
        return vinculos;
    }

    public void setVinculos(List<UsuarioVinculoResponse> vinculos) {
        this.vinculos = vinculos;
    }
}
