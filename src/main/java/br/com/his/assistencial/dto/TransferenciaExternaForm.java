package br.com.his.assistencial.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class TransferenciaExternaForm {

    @NotNull(message = "Unidade destino e obrigatoria")
    private Long unidadeDestinoId;

    @NotBlank(message = "Motivo e obrigatorio")
    @Size(max = 255, message = "Motivo deve ter no maximo 255 caracteres")
    private String motivo;

    @Size(max = 4000, message = "Observacao muito longa")
    private String observacao;

    public Long getUnidadeDestinoId() {
        return unidadeDestinoId;
    }

    public void setUnidadeDestinoId(Long unidadeDestinoId) {
        this.unidadeDestinoId = unidadeDestinoId;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}

