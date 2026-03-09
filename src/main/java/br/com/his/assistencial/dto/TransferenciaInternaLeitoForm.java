package br.com.his.assistencial.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class TransferenciaInternaLeitoForm {

    @NotNull(message = "Leito de destino e obrigatorio")
    private Long leitoDestinoId;

    @Size(max = 500)
    private String observacao;

    public Long getLeitoDestinoId() {
        return leitoDestinoId;
    }

    public void setLeitoDestinoId(Long leitoDestinoId) {
        this.leitoDestinoId = leitoDestinoId;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}
