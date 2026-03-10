package br.com.his.care.attendance.model;

import br.com.his.care.attendance.api.dto.*;
import br.com.his.care.attendance.dto.*;
import br.com.his.care.attendance.model.*;
import br.com.his.care.attendance.repository.*;
import br.com.his.care.attendance.service.*;
import br.com.his.care.admission.dto.*;
import br.com.his.care.admission.model.*;
import br.com.his.care.admission.repository.*;
import br.com.his.care.triage.dto.*;
import br.com.his.care.triage.model.*;
import br.com.his.care.triage.repository.*;
import br.com.his.care.inpatient.dto.*;
import br.com.his.care.inpatient.model.*;
import br.com.his.care.inpatient.repository.*;
import br.com.his.care.inpatient.service.*;
import br.com.his.care.episode.model.*;
import br.com.his.care.episode.repository.*;
import br.com.his.care.timeline.dto.*;
import br.com.his.care.timeline.model.*;
import br.com.his.care.timeline.repository.*;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "desfecho")
public class Desfecho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "atendimento_id", nullable = false, unique = true)
    private Atendimento atendimento;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tipo_desfecho_id", nullable = false)
    private TipoDesfecho tipoDesfecho;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "motivo_desfecho_id", nullable = false)
    private MotivoDesfecho motivoDesfecho;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "destino_rede_id")
    private DestinoRede destinoRede;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    @Column(columnDefinition = "text")
    private String observacao;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Atendimento getAtendimento() {
        return atendimento;
    }

    public void setAtendimento(Atendimento atendimento) {
        this.atendimento = atendimento;
    }

    public TipoDesfecho getTipoDesfecho() {
        return tipoDesfecho;
    }

    public void setTipoDesfecho(TipoDesfecho tipoDesfecho) {
        this.tipoDesfecho = tipoDesfecho;
    }

    public MotivoDesfecho getMotivoDesfecho() {
        return motivoDesfecho;
    }

    public void setMotivoDesfecho(MotivoDesfecho motivoDesfecho) {
        this.motivoDesfecho = motivoDesfecho;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public DestinoRede getDestinoRede() {
        return destinoRede;
    }

    public void setDestinoRede(DestinoRede destinoRede) {
        this.destinoRede = destinoRede;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}
