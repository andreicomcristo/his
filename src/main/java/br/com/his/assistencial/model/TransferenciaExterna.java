package br.com.his.assistencial.model;

import java.time.LocalDateTime;

import br.com.his.access.model.Usuario;
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
@Table(name = "transferencia_externa")
public class TransferenciaExterna {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "episodio_id", nullable = false)
    private Episodio episodio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "atendimento_origem_id", nullable = false)
    private Atendimento atendimentoOrigem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "atendimento_destino_id")
    private Atendimento atendimentoDestino;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unidade_origem_id", nullable = false)
    private Unidade unidadeOrigem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unidade_destino_id", nullable = false)
    private Unidade unidadeDestino;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id", nullable = false)
    private StatusTransferenciaExterna status;

    @Column(nullable = false, length = 255)
    private String motivo;

    @Column(columnDefinition = "TEXT")
    private String observacao;

    @Column(name = "data_solicitacao", nullable = false)
    private LocalDateTime dataSolicitacao;

    @Column(name = "data_saida")
    private LocalDateTime dataSaida;

    @Column(name = "data_chegada")
    private LocalDateTime dataChegada;

    @Column(name = "usuario_solicitacao", length = 120)
    private String usuarioSolicitacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_solicitacao_id")
    private Usuario usuarioSolicitacaoUsuario;

    @Column(name = "usuario_saida", length = 120)
    private String usuarioSaida;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_saida_id")
    private Usuario usuarioSaidaUsuario;

    @Column(name = "usuario_acolhimento", length = 120)
    private String usuarioAcolhimento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_acolhimento_id")
    private Usuario usuarioAcolhimentoUsuario;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Episodio getEpisodio() {
        return episodio;
    }

    public void setEpisodio(Episodio episodio) {
        this.episodio = episodio;
    }

    public Atendimento getAtendimentoOrigem() {
        return atendimentoOrigem;
    }

    public void setAtendimentoOrigem(Atendimento atendimentoOrigem) {
        this.atendimentoOrigem = atendimentoOrigem;
    }

    public Atendimento getAtendimentoDestino() {
        return atendimentoDestino;
    }

    public void setAtendimentoDestino(Atendimento atendimentoDestino) {
        this.atendimentoDestino = atendimentoDestino;
    }

    public Unidade getUnidadeOrigem() {
        return unidadeOrigem;
    }

    public void setUnidadeOrigem(Unidade unidadeOrigem) {
        this.unidadeOrigem = unidadeOrigem;
    }

    public Unidade getUnidadeDestino() {
        return unidadeDestino;
    }

    public void setUnidadeDestino(Unidade unidadeDestino) {
        this.unidadeDestino = unidadeDestino;
    }

    public StatusTransferenciaExterna getStatus() {
        return status;
    }

    public void setStatus(StatusTransferenciaExterna status) {
        this.status = status;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public LocalDateTime getDataSolicitacao() {
        return dataSolicitacao;
    }

    public void setDataSolicitacao(LocalDateTime dataSolicitacao) {
        this.dataSolicitacao = dataSolicitacao;
    }

    public LocalDateTime getDataSaida() {
        return dataSaida;
    }

    public void setDataSaida(LocalDateTime dataSaida) {
        this.dataSaida = dataSaida;
    }

    public LocalDateTime getDataChegada() {
        return dataChegada;
    }

    public void setDataChegada(LocalDateTime dataChegada) {
        this.dataChegada = dataChegada;
    }

    public String getUsuarioSolicitacao() {
        return usuarioSolicitacao;
    }

    public void setUsuarioSolicitacao(String usuarioSolicitacao) {
        this.usuarioSolicitacao = usuarioSolicitacao;
    }

    public Usuario getUsuarioSolicitacaoUsuario() {
        return usuarioSolicitacaoUsuario;
    }

    public void setUsuarioSolicitacaoUsuario(Usuario usuarioSolicitacaoUsuario) {
        this.usuarioSolicitacaoUsuario = usuarioSolicitacaoUsuario;
    }

    public String getUsuarioSaida() {
        return usuarioSaida;
    }

    public void setUsuarioSaida(String usuarioSaida) {
        this.usuarioSaida = usuarioSaida;
    }

    public Usuario getUsuarioSaidaUsuario() {
        return usuarioSaidaUsuario;
    }

    public void setUsuarioSaidaUsuario(Usuario usuarioSaidaUsuario) {
        this.usuarioSaidaUsuario = usuarioSaidaUsuario;
    }

    public String getUsuarioAcolhimento() {
        return usuarioAcolhimento;
    }

    public void setUsuarioAcolhimento(String usuarioAcolhimento) {
        this.usuarioAcolhimento = usuarioAcolhimento;
    }

    public Usuario getUsuarioAcolhimentoUsuario() {
        return usuarioAcolhimentoUsuario;
    }

    public void setUsuarioAcolhimentoUsuario(Usuario usuarioAcolhimentoUsuario) {
        this.usuarioAcolhimentoUsuario = usuarioAcolhimentoUsuario;
    }
}
