package br.com.his.care.inpatient.model;

import java.time.LocalDateTime;

import br.com.his.access.model.Unidade;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "area")
public class Area {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unidade_id", nullable = false)
    private Unidade unidade;

    @Column(name = "descricao", nullable = false, length = 150)
    private String descricao;

    @Column(name = "detalhamento", length = 500)
    private String detalhamento;

    @Column(name = "dt_cadastro", nullable = false)
    private LocalDateTime dtCadastro;

    @Column(name = "dt_cancelamento")
    private LocalDateTime dtCancelamento;

    @Column(name = "dt_atualizacao", nullable = false)
    private LocalDateTime dtAtualizacao;

    @Column(name = "cadastro_user_id")
    private Long cadastroUserId;

    @Column(name = "atualizacao_user_id")
    private Long atualizacaoUserId;

    @Column(name = "cancelamento_user_id")
    private Long cancelamentoUserId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Unidade getUnidade() {
        return unidade;
    }

    public void setUnidade(Unidade unidade) {
        this.unidade = unidade;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getDetalhamento() {
        return detalhamento;
    }

    public void setDetalhamento(String detalhamento) {
        this.detalhamento = detalhamento;
    }

    public LocalDateTime getDtCadastro() {
        return dtCadastro;
    }

    public void setDtCadastro(LocalDateTime dtCadastro) {
        this.dtCadastro = dtCadastro;
    }

    public LocalDateTime getDtCancelamento() {
        return dtCancelamento;
    }

    public void setDtCancelamento(LocalDateTime dtCancelamento) {
        this.dtCancelamento = dtCancelamento;
    }

    public LocalDateTime getDtAtualizacao() {
        return dtAtualizacao;
    }

    public void setDtAtualizacao(LocalDateTime dtAtualizacao) {
        this.dtAtualizacao = dtAtualizacao;
    }

    public Long getCadastroUserId() {
        return cadastroUserId;
    }

    public void setCadastroUserId(Long cadastroUserId) {
        this.cadastroUserId = cadastroUserId;
    }

    public Long getAtualizacaoUserId() {
        return atualizacaoUserId;
    }

    public void setAtualizacaoUserId(Long atualizacaoUserId) {
        this.atualizacaoUserId = atualizacaoUserId;
    }

    public Long getCancelamentoUserId() {
        return cancelamentoUserId;
    }

    public void setCancelamentoUserId(Long cancelamentoUserId) {
        this.cancelamentoUserId = cancelamentoUserId;
    }
}
