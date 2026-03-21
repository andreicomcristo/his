package br.com.his.care.attendance.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class DestinoAssistencialForm {

    @NotNull(message = "Unidade e obrigatoria")
    private Long unidadeId;

    @NotNull(message = "Tipo de destino e obrigatorio")
    private Long tipoDestinoAssistencialId;

    @NotBlank(message = "Codigo e obrigatorio")
    @Size(max = 60)
    private String codigo;

    @NotBlank(message = "Descricao e obrigatoria")
    @Size(max = 150)
    private String descricao;

    @Size(max = 500)
    private String observacao;

    @NotNull(message = "Ordem e obrigatoria")
    @Min(value = 0, message = "Ordem invalida")
    @Max(value = 9999, message = "Ordem invalida")
    private Integer ordemExibicao = 100;

    private boolean ativo = true;

    public Long getUnidadeId() {
        return unidadeId;
    }

    public void setUnidadeId(Long unidadeId) {
        this.unidadeId = unidadeId;
    }

    public Long getTipoDestinoAssistencialId() {
        return tipoDestinoAssistencialId;
    }

    public void setTipoDestinoAssistencialId(Long tipoDestinoAssistencialId) {
        this.tipoDestinoAssistencialId = tipoDestinoAssistencialId;
    }

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

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
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
