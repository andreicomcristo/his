package br.com.his.assistencial.dto;

public class LeitoMapaItemDto {

    private Long leitoId;
    private String codigo;
    private String descricao;
    private String areaNome;
    private String modalidadesDescricao;
    private String naturezaOperacionalDescricao;
    private boolean virtualSuperlotacao;
    private boolean permiteObservacao;
    private boolean permiteInternacao;
    private boolean livre;
    private Long ocupacaoId;
    private Long observacaoId;
    private String contexto;
    private String tipoOcupacaoDescricao;
    private Long atendimentoId;
    private String pacienteNome;
    private String tipoAtendimento;
    private boolean podeConverterParaInternacao;

    public Long getLeitoId() {
        return leitoId;
    }

    public void setLeitoId(Long leitoId) {
        this.leitoId = leitoId;
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

    public String getAreaNome() {
        return areaNome;
    }

    public void setAreaNome(String areaNome) {
        this.areaNome = areaNome;
    }

    public String getModalidadesDescricao() {
        return modalidadesDescricao;
    }

    public void setModalidadesDescricao(String modalidadesDescricao) {
        this.modalidadesDescricao = modalidadesDescricao;
    }

    public String getNaturezaOperacionalDescricao() {
        return naturezaOperacionalDescricao;
    }

    public void setNaturezaOperacionalDescricao(String naturezaOperacionalDescricao) {
        this.naturezaOperacionalDescricao = naturezaOperacionalDescricao;
    }

    public boolean isVirtualSuperlotacao() {
        return virtualSuperlotacao;
    }

    public void setVirtualSuperlotacao(boolean virtualSuperlotacao) {
        this.virtualSuperlotacao = virtualSuperlotacao;
    }

    public boolean isPermiteObservacao() {
        return permiteObservacao;
    }

    public void setPermiteObservacao(boolean permiteObservacao) {
        this.permiteObservacao = permiteObservacao;
    }

    public boolean isPermiteInternacao() {
        return permiteInternacao;
    }

    public void setPermiteInternacao(boolean permiteInternacao) {
        this.permiteInternacao = permiteInternacao;
    }

    public boolean isLivre() {
        return livre;
    }

    public void setLivre(boolean livre) {
        this.livre = livre;
    }

    public Long getOcupacaoId() {
        return ocupacaoId;
    }

    public void setOcupacaoId(Long ocupacaoId) {
        this.ocupacaoId = ocupacaoId;
    }

    public Long getObservacaoId() {
        return observacaoId;
    }

    public void setObservacaoId(Long observacaoId) {
        this.observacaoId = observacaoId;
    }

    public String getContexto() {
        return contexto;
    }

    public void setContexto(String contexto) {
        this.contexto = contexto;
    }

    public String getTipoOcupacaoDescricao() {
        return tipoOcupacaoDescricao;
    }

    public void setTipoOcupacaoDescricao(String tipoOcupacaoDescricao) {
        this.tipoOcupacaoDescricao = tipoOcupacaoDescricao;
    }

    public Long getAtendimentoId() {
        return atendimentoId;
    }

    public void setAtendimentoId(Long atendimentoId) {
        this.atendimentoId = atendimentoId;
    }

    public String getPacienteNome() {
        return pacienteNome;
    }

    public void setPacienteNome(String pacienteNome) {
        this.pacienteNome = pacienteNome;
    }

    public String getTipoAtendimento() {
        return tipoAtendimento;
    }

    public void setTipoAtendimento(String tipoAtendimento) {
        this.tipoAtendimento = tipoAtendimento;
    }

    public boolean isPodeConverterParaInternacao() {
        return podeConverterParaInternacao;
    }

    public void setPodeConverterParaInternacao(boolean podeConverterParaInternacao) {
        this.podeConverterParaInternacao = podeConverterParaInternacao;
    }
}
