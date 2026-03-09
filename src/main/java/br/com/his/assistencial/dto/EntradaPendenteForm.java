package br.com.his.assistencial.dto;

import br.com.his.paciente.dto.PacienteForm;
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
