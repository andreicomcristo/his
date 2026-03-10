package br.com.his.care.inpatient.dto;

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

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class TransferenciaInternaLeitoForm {

    @NotNull(message = "Leito de destino e obrigatorio")
    private Long leitoDestinoId;

    @Size(max = 500)
    private String observacao;

    public Long getLeitoDestinoId() {
        return leitoDestinoId;
    }

    public void setLeitoDestinoId(Long leitoDestinoId) {
        this.leitoDestinoId = leitoDestinoId;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}
