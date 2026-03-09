package br.com.his.assistencial.model;

import java.time.LocalDateTime;

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
@Table(name = "leito_ocupacao")
public class LeitoOcupacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leito_id", nullable = false)
    private Leito leito;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "observacao_id")
    private Observacao observacaoAtendimento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "internacao_id")
    private Internacao internacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_ocupacao_id", nullable = false)
    private LeitoOcupacaoTipo tipoOcupacao;

    @Column(name = "data_hora_entrada", nullable = false)
    private LocalDateTime dataHoraEntrada;

    @Column(name = "data_hora_saida")
    private LocalDateTime dataHoraSaida;

    @Column(columnDefinition = "text")
    private String observacao;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Leito getLeito() {
        return leito;
    }

    public void setLeito(Leito leito) {
        this.leito = leito;
    }

    public Observacao getObservacaoAtendimento() {
        return observacaoAtendimento;
    }

    public void setObservacaoAtendimento(Observacao observacaoAtendimento) {
        this.observacaoAtendimento = observacaoAtendimento;
    }

    public Internacao getInternacao() {
        return internacao;
    }

    public void setInternacao(Internacao internacao) {
        this.internacao = internacao;
    }

    public LeitoOcupacaoTipo getTipoOcupacao() {
        return tipoOcupacao;
    }

    public void setTipoOcupacao(LeitoOcupacaoTipo tipoOcupacao) {
        this.tipoOcupacao = tipoOcupacao;
    }

    public LocalDateTime getDataHoraEntrada() {
        return dataHoraEntrada;
    }

    public void setDataHoraEntrada(LocalDateTime dataHoraEntrada) {
        this.dataHoraEntrada = dataHoraEntrada;
    }

    public LocalDateTime getDataHoraSaida() {
        return dataHoraSaida;
    }

    public void setDataHoraSaida(LocalDateTime dataHoraSaida) {
        this.dataHoraSaida = dataHoraSaida;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}
