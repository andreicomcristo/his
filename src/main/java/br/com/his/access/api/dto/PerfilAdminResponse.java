package br.com.his.access.api.dto;

import java.util.List;

public class PerfilAdminResponse {

    private Long id;
    private String nome;
    private List<Long> permissaoIds;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public List<Long> getPermissaoIds() {
        return permissaoIds;
    }

    public void setPermissaoIds(List<Long> permissaoIds) {
        this.permissaoIds = permissaoIds;
    }
}
