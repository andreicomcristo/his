package br.com.his.patient.dto;

public class ProcedenciaEntradaOption {

    private final Long id;
    private final String descricao;
    private final Long tipoProcedenciaId;

    public ProcedenciaEntradaOption(Long id, String descricao, Long tipoProcedenciaId) {
        this.id = id;
        this.descricao = descricao;
        this.tipoProcedenciaId = tipoProcedenciaId;
    }

    public Long getId() {
        return id;
    }

    public String getDescricao() {
        return descricao;
    }

    public Long getTipoProcedenciaId() {
        return tipoProcedenciaId;
    }
}
