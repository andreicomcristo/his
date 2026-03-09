package br.com.his.paciente.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import br.com.his.access.model.Usuario;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import br.com.his.configuracao.model.Cidade;
import br.com.his.paciente.model.lookup.Deficiencia;
import br.com.his.paciente.model.lookup.Escolaridade;
import br.com.his.paciente.model.lookup.EstadoCivil;
import br.com.his.paciente.model.lookup.EtniaIndigena;
import br.com.his.paciente.model.lookup.IdentidadeGenero;
import br.com.his.paciente.model.lookup.Nacionalidade;
import br.com.his.paciente.model.lookup.Naturalidade;
import br.com.his.paciente.model.lookup.OrientacaoSexual;
import br.com.his.paciente.model.lookup.Procedencia;
import br.com.his.paciente.model.lookup.Profissao;
import br.com.his.paciente.model.lookup.RacaCor;
import br.com.his.paciente.model.lookup.Sexo;
import br.com.his.paciente.model.lookup.TipoSanguineo;

@Entity
@Table(name = "paciente")
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(name = "nome_social", length = 150)
    private String nomeSocial;

    @Column(length = 11)
    private String cpf;

    @Column(length = 20)
    private String cns;

    @Column(length = 30)
    private String rg;

    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sexo_id")
    private Sexo sexo;

    @Column(length = 20)
    private String telefone;

    @Column(name = "nome_mae", length = 150)
    private String nomeMae;

    @Column(name = "nome_pai", length = 150)
    private String nomePai;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "raca_cor_id")
    private RacaCor racaCor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "etnia_indigena_id")
    private EtniaIndigena etniaIndigena;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nacionalidade_id")
    private Nacionalidade nacionalidade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "naturalidade_id")
    private Naturalidade naturalidade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estado_civil_id")
    private EstadoCivil estadoCivil;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "escolaridade_id")
    private Escolaridade escolaridade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_sanguineo_id")
    private TipoSanguineo tipoSanguineo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orientacao_sexual_id")
    private OrientacaoSexual orientacaoSexual;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "identidade_genero_id")
    private IdentidadeGenero identidadeGenero;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deficiencia_id")
    private Deficiencia deficiencia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profissao_id")
    private Profissao profissao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "procedencia_id")
    private Procedencia procedencia;

    @Column(length = 150)
    private String email;

    @Column(columnDefinition = "TEXT")
    private String observacoes;

    @Column(length = 10)
    private String cep;

    @Column(length = 200)
    private String logradouro;

    @Column(length = 20)
    private String numero;

    @Column(length = 100)
    private String complemento;

    @Column(length = 100)
    private String bairro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cidade_id")
    private Cidade cidade;

    @Column(nullable = false)
    private boolean temporario;

    @Column(name = "idade_aparente")
    private Integer idadeAparente;

    @Column(nullable = false)
    private boolean ativo;

    @Column(name = "data_cancelamento")
    private LocalDateTime dataCancelamento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merged_into_id")
    private Paciente mergedInto;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    @Column(name = "criado_por", length = 100)
    private String criadoPor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "criado_por_usuario_id")
    private Usuario criadoPorUsuario;

    @Column(name = "atualizado_por", length = 100)
    private String atualizadoPor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "atualizado_por_usuario_id")
    private Usuario atualizadoPorUsuario;

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

    public String getNomeExibicao() {
        Paciente principal = getPacientePrincipal();
        return principal == null ? nome : principal.getNome();
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
        return sexo == null ? null : sexo.getCodigo();
    }

    public String getSexoDescricao() {
        return sexo == null ? null : sexo.getDescricao();
    }

    public Sexo getSexoRegistro() {
        return sexo;
    }

    public void setSexoRegistro(Sexo sexo) {
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

    public RacaCor getRacaCor() {
        return racaCor;
    }

    public void setRacaCor(RacaCor racaCor) {
        this.racaCor = racaCor;
    }

    public EtniaIndigena getEtniaIndigena() {
        return etniaIndigena;
    }

    public void setEtniaIndigena(EtniaIndigena etniaIndigena) {
        this.etniaIndigena = etniaIndigena;
    }

    public Nacionalidade getNacionalidade() {
        return nacionalidade;
    }

    public void setNacionalidade(Nacionalidade nacionalidade) {
        this.nacionalidade = nacionalidade;
    }

    public Naturalidade getNaturalidade() {
        return naturalidade;
    }

    public void setNaturalidade(Naturalidade naturalidade) {
        this.naturalidade = naturalidade;
    }

    public EstadoCivil getEstadoCivil() {
        return estadoCivil;
    }

    public void setEstadoCivil(EstadoCivil estadoCivil) {
        this.estadoCivil = estadoCivil;
    }

    public Escolaridade getEscolaridade() {
        return escolaridade;
    }

    public void setEscolaridade(Escolaridade escolaridade) {
        this.escolaridade = escolaridade;
    }

    public TipoSanguineo getTipoSanguineo() {
        return tipoSanguineo;
    }

    public void setTipoSanguineo(TipoSanguineo tipoSanguineo) {
        this.tipoSanguineo = tipoSanguineo;
    }

    public OrientacaoSexual getOrientacaoSexual() {
        return orientacaoSexual;
    }

    public void setOrientacaoSexual(OrientacaoSexual orientacaoSexual) {
        this.orientacaoSexual = orientacaoSexual;
    }

    public IdentidadeGenero getIdentidadeGenero() {
        return identidadeGenero;
    }

    public void setIdentidadeGenero(IdentidadeGenero identidadeGenero) {
        this.identidadeGenero = identidadeGenero;
    }

    public Deficiencia getDeficiencia() {
        return deficiencia;
    }

    public void setDeficiencia(Deficiencia deficiencia) {
        this.deficiencia = deficiencia;
    }

    public Profissao getProfissao() {
        return profissao;
    }

    public void setProfissao(Profissao profissao) {
        this.profissao = profissao;
    }

    public Procedencia getProcedencia() {
        return procedencia;
    }

    public void setProcedencia(Procedencia procedencia) {
        this.procedencia = procedencia;
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

    public Cidade getCidade() {
        return cidade;
    }

    public void setCidade(Cidade cidade) {
        this.cidade = cidade;
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

    public Paciente getMergedInto() {
        return mergedInto;
    }

    public void setMergedInto(Paciente mergedInto) {
        this.mergedInto = mergedInto;
    }

    public Paciente getPacientePrincipal() {
        Paciente atual = this;
        int hops = 0;
        while (atual != null && atual.getMergedInto() != null && hops < 20) {
            atual = atual.getMergedInto();
            hops++;
        }
        return atual == null ? this : atual;
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

    public String getCriadoPor() {
        return criadoPor;
    }

    public void setCriadoPor(String criadoPor) {
        this.criadoPor = criadoPor;
    }

    public Usuario getCriadoPorUsuario() {
        return criadoPorUsuario;
    }

    public void setCriadoPorUsuario(Usuario criadoPorUsuario) {
        this.criadoPorUsuario = criadoPorUsuario;
    }

    public String getAtualizadoPor() {
        return atualizadoPor;
    }

    public void setAtualizadoPor(String atualizadoPor) {
        this.atualizadoPor = atualizadoPor;
    }

    public Usuario getAtualizadoPorUsuario() {
        return atualizadoPorUsuario;
    }

    public void setAtualizadoPorUsuario(Usuario atualizadoPorUsuario) {
        this.atualizadoPorUsuario = atualizadoPorUsuario;
    }

    public boolean isInativoOuMerged() {
        return !ativo || mergedInto != null;
    }
}
