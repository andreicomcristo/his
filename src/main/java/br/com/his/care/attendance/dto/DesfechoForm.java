package br.com.his.care.attendance.dto;

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

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class DesfechoForm {

    @NotNull(message = "Atendimento e obrigatorio")
    private Long atendimentoId;

    @NotNull(message = "Tipo de desfecho e obrigatorio")
    private Long tipoDesfechoId;

    @NotNull(message = "Motivo do desfecho e obrigatorio")
    private Long motivoDesfechoId;

    private Long destinoRedeId;

    @NotNull(message = "Data/hora e obrigatoria")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime dataHora = LocalDateTime.now();

    @Size(max = 2000)
    private String observacao;

    public Long getAtendimentoId() {
        return atendimentoId;
    }

    public void setAtendimentoId(Long atendimentoId) {
        this.atendimentoId = atendimentoId;
    }

    public Long getTipoDesfechoId() {
        return tipoDesfechoId;
    }

    public void setTipoDesfechoId(Long tipoDesfechoId) {
        this.tipoDesfechoId = tipoDesfechoId;
    }

    public Long getMotivoDesfechoId() {
        return motivoDesfechoId;
    }

    public void setMotivoDesfechoId(Long motivoDesfechoId) {
        this.motivoDesfechoId = motivoDesfechoId;
    }

    public Long getDestinoRedeId() {
        return destinoRedeId;
    }

    public void setDestinoRedeId(Long destinoRedeId) {
        this.destinoRedeId = destinoRedeId;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}
