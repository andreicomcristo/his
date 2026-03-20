package br.com.his.care.inpatient.dto;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class LeitoForm {

    @NotNull(message = "Unidade e obrigatoria")
    private Long unidadeId;

    @NotNull(message = "Area e obrigatoria")
    private Long areaId;

    @NotNull(message = "Tipo de leito e obrigatorio")
    private Long tipoLeitoId;

    private List<Long> modalidadeIds = new ArrayList<>();

    private Long perfilLeitoId;

    @NotBlank(message = "Codigo e obrigatorio")
    @Size(max = 50)
    private String codigo;

    @Size(max = 255)
    private String descricao;

    private boolean recebePs = false;

    private boolean permiteDestinoDefinitivo = true;

    private boolean assistencial = true;

    @NotNull(message = "Natureza operacional e obrigatoria")
    private Long naturezaOperacionalId;

    public Long getUnidadeId() {
        return unidadeId;
    }

    public void setUnidadeId(Long unidadeId) {
        this.unidadeId = unidadeId;
    }

    public Long getAreaId() {
        return areaId;
    }

    public void setAreaId(Long areaId) {
        this.areaId = areaId;
    }

    public Long getTipoLeitoId() {
        return tipoLeitoId;
    }

    public void setTipoLeitoId(Long tipoLeitoId) {
        this.tipoLeitoId = tipoLeitoId;
    }

    public Long getPerfilLeitoId() {
        return perfilLeitoId;
    }

    public void setPerfilLeitoId(Long perfilLeitoId) {
        this.perfilLeitoId = perfilLeitoId;
    }

    public List<Long> getModalidadeIds() {
        return modalidadeIds;
    }

    public void setModalidadeIds(List<Long> modalidadeIds) {
        this.modalidadeIds = modalidadeIds;
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

    public boolean isRecebePs() {
        return recebePs;
    }

    public void setRecebePs(boolean recebePs) {
        this.recebePs = recebePs;
    }

    public boolean isAssistencial() {
        return assistencial;
    }

    public void setAssistencial(boolean assistencial) {
        this.assistencial = assistencial;
    }

    public Long getNaturezaOperacionalId() {
        return naturezaOperacionalId;
    }

    public void setNaturezaOperacionalId(Long naturezaOperacionalId) {
        this.naturezaOperacionalId = naturezaOperacionalId;
    }

    public boolean isPermiteDestinoDefinitivo() {
        return permiteDestinoDefinitivo;
    }

    public void setPermiteDestinoDefinitivo(boolean permiteDestinoDefinitivo) {
        this.permiteDestinoDefinitivo = permiteDestinoDefinitivo;
    }

}
