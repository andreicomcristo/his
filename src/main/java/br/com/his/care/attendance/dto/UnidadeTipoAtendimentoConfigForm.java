package br.com.his.care.attendance.dto;

import jakarta.validation.constraints.NotNull;

public class UnidadeTipoAtendimentoConfigForm {

    @NotNull
    private Long tipoAtendimentoId;

    private String codigo;
    private String descricao;
    private boolean ativo = true;
    private boolean triagemObrigatoria;
    private boolean passaConsultorio = true;
    private boolean permiteAgendamento = true;

    public Long getTipoAtendimentoId() {
        return tipoAtendimentoId;
    }

    public void setTipoAtendimentoId(Long tipoAtendimentoId) {
        this.tipoAtendimentoId = tipoAtendimentoId;
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

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public boolean isTriagemObrigatoria() {
        return triagemObrigatoria;
    }

    public void setTriagemObrigatoria(boolean triagemObrigatoria) {
        this.triagemObrigatoria = triagemObrigatoria;
    }

    public boolean isPassaConsultorio() {
        return passaConsultorio;
    }

    public void setPassaConsultorio(boolean passaConsultorio) {
        this.passaConsultorio = passaConsultorio;
    }

    public boolean isPermiteAgendamento() {
        return permiteAgendamento;
    }

    public void setPermiteAgendamento(boolean permiteAgendamento) {
        this.permiteAgendamento = permiteAgendamento;
    }
}
