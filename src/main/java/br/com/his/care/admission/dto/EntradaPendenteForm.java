package br.com.his.care.admission.dto;

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

import br.com.his.patient.dto.PacienteForm;
import jakarta.validation.Valid;

public class EntradaPendenteForm {

    @Valid
    private final PacienteForm pacienteForm = new PacienteForm();

    @Valid
    private final EntradaForm entradaForm = new EntradaForm();

    public PacienteForm getPacienteForm() {
        return pacienteForm;
    }

    public EntradaForm getEntradaForm() {
        return entradaForm;
    }
}
