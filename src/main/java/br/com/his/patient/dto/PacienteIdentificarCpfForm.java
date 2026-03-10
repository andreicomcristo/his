package br.com.his.patient.dto;

import jakarta.validation.constraints.NotBlank;

public class PacienteIdentificarCpfForm {

    @NotBlank(message = "CPF e obrigatorio")
    private String cpf;

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }
}
