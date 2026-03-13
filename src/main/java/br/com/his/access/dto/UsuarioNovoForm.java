package br.com.his.access.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UsuarioNovoForm {

    @NotBlank(message = "Username e obrigatorio")
    @Size(max = 120)
    private String username;

    @Email(message = "Email invalido")
    @Size(max = 180)
    private String email;

    @Size(max = 120)
    private String nome;

    @Size(max = 120)
    private String sobrenome;

    @Size(max = 80)
    private String senhaTemporaria;

    private boolean exigirTrocaSenha = true;

    @Size(max = 30)
    private String colaboradorCpf;

    @Size(max = 150)
    private String colaboradorNome;

    private Long colaboradorCargoId;

    private boolean associarColaboradorExistente;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSobrenome() {
        return sobrenome;
    }

    public void setSobrenome(String sobrenome) {
        this.sobrenome = sobrenome;
    }

    public String getSenhaTemporaria() {
        return senhaTemporaria;
    }

    public void setSenhaTemporaria(String senhaTemporaria) {
        this.senhaTemporaria = senhaTemporaria;
    }

    public boolean isExigirTrocaSenha() {
        return exigirTrocaSenha;
    }

    public void setExigirTrocaSenha(boolean exigirTrocaSenha) {
        this.exigirTrocaSenha = exigirTrocaSenha;
    }

    public String getColaboradorCpf() {
        return colaboradorCpf;
    }

    public void setColaboradorCpf(String colaboradorCpf) {
        this.colaboradorCpf = colaboradorCpf;
    }

    public String getColaboradorNome() {
        return colaboradorNome;
    }

    public void setColaboradorNome(String colaboradorNome) {
        this.colaboradorNome = colaboradorNome;
    }

    public Long getColaboradorCargoId() {
        return colaboradorCargoId;
    }

    public void setColaboradorCargoId(Long colaboradorCargoId) {
        this.colaboradorCargoId = colaboradorCargoId;
    }

    public boolean isAssociarColaboradorExistente() {
        return associarColaboradorExistente;
    }

    public void setAssociarColaboradorExistente(boolean associarColaboradorExistente) {
        this.associarColaboradorExistente = associarColaboradorExistente;
    }
}
