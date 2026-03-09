package br.com.his.patient.api.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class PacienteResponse {

    private Long id;
    private String nome;
    private String nomeSocial;
    private String cpf;
    private String cns;
    private String rg;
    private LocalDate dataNascimento;
    private String sexo;
    private String telefone;
    private String nomeMae;
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
    private String email;
    private String observacoes;
    private String cep;
    private String logradouro;
    private String numero;
    private String complemento;
    private String bairro;
    private Long unidadeFederativaId;
    private Long cidadeId;
    private String cidade;
    private String uf;
    private boolean temporario;
    private Integer idadeAparente;
    private boolean ativo;
    private LocalDateTime dataCancelamento;
    private Long mergedIntoId;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Long getCidadeId() {
        return cidadeId;
    }

    public void setCidadeId(Long cidadeId) {
        this.cidadeId = cidadeId;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public boolean isTemporario() {
        return temporario;
    }

    public void setTemporario(boolean temporario) {
        this.temporario = temporario;
    }

    public Integer getIdadeAparente() {
        return idadeAparente;
    }

    public void setIdadeAparente(Integer idadeAparente) {
        this.idadeAparente = idadeAparente;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public LocalDateTime getDataCancelamento() {
        return dataCancelamento;
    }

    public void setDataCancelamento(LocalDateTime dataCancelamento) {
        this.dataCancelamento = dataCancelamento;
    }

    public Long getMergedIntoId() {
        return mergedIntoId;
    }

    public void setMergedIntoId(Long mergedIntoId) {
        this.mergedIntoId = mergedIntoId;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

    public LocalDateTime getAtualizadoEm() {
        return atualizadoEm;
    }

    public void setAtualizadoEm(LocalDateTime atualizadoEm) {
        this.atualizadoEm = atualizadoEm;
    }
}
