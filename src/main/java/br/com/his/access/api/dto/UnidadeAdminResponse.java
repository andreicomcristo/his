package br.com.his.access.api.dto;

public class UnidadeAdminResponse {

    private Long id;
    private String nome;
    private Long tipoUnidadeId;
    private String tipoUnidadeDescricao;
    private String tipoEstabelecimento;
    private String sigla;
    private String cnes;
    private boolean ativo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Long getTipoUnidadeId() {
        return tipoUnidadeId;
    }

    public void setTipoUnidadeId(Long tipoUnidadeId) {
        this.tipoUnidadeId = tipoUnidadeId;
    }

    public String getTipoUnidadeDescricao() {
        return tipoUnidadeDescricao;
    }

    public void setTipoUnidadeDescricao(String tipoUnidadeDescricao) {
        this.tipoUnidadeDescricao = tipoUnidadeDescricao;
    }

    public String getTipoEstabelecimento() {
        return tipoEstabelecimento;
    }

    public void setTipoEstabelecimento(String tipoEstabelecimento) {
        this.tipoEstabelecimento = tipoEstabelecimento;
    }

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

    public String getCnes() {
        return cnes;
    }

    public void setCnes(String cnes) {
        this.cnes = cnes;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
}
