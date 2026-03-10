package br.com.his.patient.dto;

public class PacienteLookupOption {

    private final Long id;
    private final String descricao;
    private final Long relacionamentoId;

    public PacienteLookupOption(Long id, String descricao) {
        this(id, descricao, null);
    }

    public PacienteLookupOption(Long id, String descricao, Long relacionamentoId) {
        this.id = id;
        this.descricao = descricao;
        this.relacionamentoId = relacionamentoId;
    }

    public Long getId() {
        return id;
    }

    public String getDescricao() {
        return descricao;
    }

    public Long getRelacionamentoId() {
        return relacionamentoId;
    }
}
