package br.com.his.paciente.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class PacienteMergeForm {

    @NotNull(message = "Paciente origem e obrigatorio")
    private Long fromId;

    @NotNull(message = "Paciente destino e obrigatorio")
    private Long toId;

    @NotBlank(message = "Motivo do merge e obrigatorio")
    private String motivo;

    public Long getFromId() {
        return fromId;
    }

    public void setFromId(Long fromId) {
        this.fromId = fromId;
    }

    public Long getToId() {
        return toId;
    }

    public void setToId(Long toId) {
        this.toId = toId;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
}
