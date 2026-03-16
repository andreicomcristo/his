package br.com.his.patient.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ProcedenciaAdminForm {

    @NotNull(message = "Tipo de procedencia e obrigatorio")
    private Long tipoProcedenciaId;

    private Long unidadeId;

    private Long unidadeFederativaId;

    private Long municipioId;

    private Long bairroId;

    @Size(max = 150, message = "Descricao deve ter no maximo 150 caracteres")
    private String descricao;

    private boolean ativo = true;

    public Long getTipoProcedenciaId() {
        return tipoProcedenciaId;
    }

    public void setTipoProcedenciaId(Long tipoProcedenciaId) {
        this.tipoProcedenciaId = tipoProcedenciaId;
    }

    public Long getUnidadeId() {
        return unidadeId;
    }

    public void setUnidadeId(Long unidadeId) {
        this.unidadeId = unidadeId;
    }

    public Long getUnidadeFederativaId() {
        return unidadeFederativaId;
    }

    public void setUnidadeFederativaId(Long unidadeFederativaId) {
        this.unidadeFederativaId = unidadeFederativaId;
    }

    public Long getMunicipioId() {
        return municipioId;
    }

    public void setMunicipioId(Long municipioId) {
        this.municipioId = municipioId;
    }

    public Long getBairroId() {
        return bairroId;
    }

    public void setBairroId(Long bairroId) {
        this.bairroId = bairroId;
    }

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
}
