package br.com.his.access.dto;

import jakarta.validation.constraints.NotNull;

public class UsuarioAtuacaoForm {

    @NotNull(message = "Informe a unidade")
    private Long unidadeId;

    @NotNull(message = "Informe a funcao")
    private Long funcaoUnidadeId;

    @NotNull(message = "Informe o perfil")
    private Long perfilId;

    private Long especialidadeId;

    public Long getUnidadeId() {
        return unidadeId;
    }

    public void setUnidadeId(Long unidadeId) {
        this.unidadeId = unidadeId;
    }

    public Long getFuncaoUnidadeId() {
        return funcaoUnidadeId;
    }

    public void setFuncaoUnidadeId(Long funcaoUnidadeId) {
        this.funcaoUnidadeId = funcaoUnidadeId;
    }

    public Long getPerfilId() {
        return perfilId;
    }

    public void setPerfilId(Long perfilId) {
        this.perfilId = perfilId;
    }

    public Long getEspecialidadeId() {
        return especialidadeId;
    }

    public void setEspecialidadeId(Long especialidadeId) {
        this.especialidadeId = especialidadeId;
    }
}
