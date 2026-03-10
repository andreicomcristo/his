package br.com.his.patient.dto;

import jakarta.validation.constraints.Size;

public class PacienteCatalogoForm {

    @Size(max = 200)
    private String descricao;

    @Size(max = 150)
    private String nome;

    @Size(max = 10)
    private String codigo;

    @Size(max = 20)
    private String codIbge;

    @Size(max = 6)
    private String cboCod;

    @Size(max = 255)
    private String descricaoComplementar;

    private Long tipoProcedenciaId;
    private Long unidadeId;

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getCodIbge() { return codIbge; }
    public void setCodIbge(String codIbge) { this.codIbge = codIbge; }
    public String getCboCod() { return cboCod; }
    public void setCboCod(String cboCod) { this.cboCod = cboCod; }
    public String getDescricaoComplementar() { return descricaoComplementar; }
    public void setDescricaoComplementar(String descricaoComplementar) { this.descricaoComplementar = descricaoComplementar; }
    public Long getTipoProcedenciaId() { return tipoProcedenciaId; }
    public void setTipoProcedenciaId(Long tipoProcedenciaId) { this.tipoProcedenciaId = tipoProcedenciaId; }
    public Long getUnidadeId() { return unidadeId; }
    public void setUnidadeId(Long unidadeId) { this.unidadeId = unidadeId; }
}
