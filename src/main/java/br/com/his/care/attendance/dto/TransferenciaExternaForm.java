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

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class TransferenciaExternaForm {

    @NotNull(message = "Unidade destino e obrigatoria")
    private Long unidadeDestinoId;

    @NotBlank(message = "Motivo e obrigatorio")
    @Size(max = 255, message = "Motivo deve ter no maximo 255 caracteres")
    private String motivo;

    @Size(max = 4000, message = "Observacao muito longa")
    private String observacao;

    public Long getUnidadeDestinoId() {
        return unidadeDestinoId;
    }

    public void setUnidadeDestinoId(Long unidadeDestinoId) {
        this.unidadeDestinoId = unidadeDestinoId;
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
}

