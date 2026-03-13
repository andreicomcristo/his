package br.com.his.access.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ColaboradorForm {

    @NotBlank(message = "Informe o nome")
    @Size(max = 150, message = "Nome deve ter no maximo 150 caracteres")
    private String nome;

    @Size(max = 30, message = "CPF deve ter no maximo 30 caracteres")
    private String cpf;

    private Long cargoColaboradorId;

    private boolean ativo = true;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public Long getCargoColaboradorId() {
        return cargoColaboradorId;
    }

    public void setCargoColaboradorId(Long cargoColaboradorId) {
        this.cargoColaboradorId = cargoColaboradorId;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
}
