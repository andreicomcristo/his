package br.com.his.care.attendance.dto;

public class TipoAtendimentoOption {

    private final String codigo;
    private final String descricao;

    public TipoAtendimentoOption(String codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }
}
