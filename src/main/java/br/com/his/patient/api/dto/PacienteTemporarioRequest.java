package br.com.his.patient.api.dto;

import java.time.LocalDate;

import br.com.his.patient.validation.Cpf;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class PacienteTemporarioRequest {

    @Size(max = 150)
    private String nome;

    @Size(max = 150)
    private String nomeSocial;

    @Cpf
    private String cpf;

    @Pattern(regexp = "^$|^[0-9]{15}$", message = "cns deve conter 15 digitos")
    private String cns;

    @Size(max = 30)
    private String rg;

    @PastOrPresent(message = "dataNascimento invalida")
    private LocalDate dataNascimento;

    @Pattern(regexp = "^$|^(M|F|NI)$", message = "sexo invalido")
    private String sexo;

    @Size(max = 20)
    private String telefone;

    @Size(max = 150)
    private String nomeMae;

    @Size(max = 150)
    private String nomePai;

    private Long racaCorId;

    private Long etniaIndigenaId;

    private Long nacionalidadeId;

    private Long naturalidadeId;

    private Long estadoCivilId;

    private Long escolaridadeId;

    private Long tipoSanguineoId;

    private Long orientacaoSexualId;

    private Long identidadeGeneroId;

    private Long deficienciaId;

    private Long profissaoId;

    private Long procedenciaId;

    @Size(max = 150)
    private String email;

    private String observacoes;

    @Size(max = 10)
    private String cep;

    @Size(max = 200)
    private String logradouro;

    @Size(max = 20)
    private String numero;

    @Size(max = 100)
    private String complemento;

    @Size(max = 100)
    private String bairro;

    private Long unidadeFederativaId;

    private Long municipioId;

    @Min(value = 0, message = "idadeAparente invalida")
    @Max(value = 150, message = "idadeAparente invalida")
    private Integer idadeAparente;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNomeSocial() {
        return nomeSocial;
    }

    public void setNomeSocial(String nomeSocial) {
        this.nomeSocial = nomeSocial;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getCns() {
        return cns;
    }

    public void setCns(String cns) {
        this.cns = cns;
    }

    public String getRg() {
        return rg;
    }

    public void setRg(String rg) {
        this.rg = rg;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getNomeMae() {
        return nomeMae;
    }

    public void setNomeMae(String nomeMae) {
        this.nomeMae = nomeMae;
    }

    public String getNomePai() {
        return nomePai;
    }

    public void setNomePai(String nomePai) {
        this.nomePai = nomePai;
    }

    public Long getRacaCorId() {
        return racaCorId;
    }

    public void setRacaCorId(Long racaCorId) {
        this.racaCorId = racaCorId;
    }

    public Long getEtniaIndigenaId() {
        return etniaIndigenaId;
    }

    public void setEtniaIndigenaId(Long etniaIndigenaId) {
        this.etniaIndigenaId = etniaIndigenaId;
    }

    public Long getNacionalidadeId() {
        return nacionalidadeId;
    }

    public void setNacionalidadeId(Long nacionalidadeId) {
        this.nacionalidadeId = nacionalidadeId;
    }

    public Long getNaturalidadeId() {
        return naturalidadeId;
    }

    public void setNaturalidadeId(Long naturalidadeId) {
        this.naturalidadeId = naturalidadeId;
    }

    public Long getEstadoCivilId() {
        return estadoCivilId;
    }

    public void setEstadoCivilId(Long estadoCivilId) {
        this.estadoCivilId = estadoCivilId;
    }

    public Long getEscolaridadeId() {
        return escolaridadeId;
    }

    public void setEscolaridadeId(Long escolaridadeId) {
        this.escolaridadeId = escolaridadeId;
    }

    public Long getTipoSanguineoId() {
        return tipoSanguineoId;
    }

    public void setTipoSanguineoId(Long tipoSanguineoId) {
        this.tipoSanguineoId = tipoSanguineoId;
    }

    public Long getOrientacaoSexualId() {
        return orientacaoSexualId;
    }

    public void setOrientacaoSexualId(Long orientacaoSexualId) {
        this.orientacaoSexualId = orientacaoSexualId;
    }

    public Long getIdentidadeGeneroId() {
        return identidadeGeneroId;
    }

    public void setIdentidadeGeneroId(Long identidadeGeneroId) {
        this.identidadeGeneroId = identidadeGeneroId;
    }

    public Long getDeficienciaId() {
        return deficienciaId;
    }

    public void setDeficienciaId(Long deficienciaId) {
        this.deficienciaId = deficienciaId;
    }

    public Long getProfissaoId() {
        return profissaoId;
    }

    public void setProfissaoId(Long profissaoId) {
        this.profissaoId = profissaoId;
    }

    public Long getProcedenciaId() {
        return procedenciaId;
    }

    public void setProcedenciaId(Long procedenciaId) {
        this.procedenciaId = procedenciaId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public Long getUnidadeFederativaId() {
        return unidadeFederativaId;
    }

    public void setUnidadeFederativaId(Long unidadeFederativaId) {
        this.unidadeFederativaId = unidadeFederativaId;
    }

    public Long getMunicipioId() {
        return municipioId;
    }

    public void setMunicipioId(Long municipioId) {
        this.municipioId = municipioId;
    }

    public Integer getIdadeAparente() {
        return idadeAparente;
    }

    public void setIdadeAparente(Integer idadeAparente) {
        this.idadeAparente = idadeAparente;
    }
}

