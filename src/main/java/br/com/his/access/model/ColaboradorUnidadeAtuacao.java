package br.com.his.access.model;

import java.time.LocalDate;

import br.com.his.care.scheduling.model.Especialidade;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "colaborador_unidade_atuacao")
public class ColaboradorUnidadeAtuacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "colaborador_unidade_vinculo_id", nullable = false)
    private ColaboradorUnidadeVinculo colaboradorUnidadeVinculo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "funcao_unidade_id", nullable = false)
    private FuncaoUnidade funcaoUnidade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "especialidade_id")
    private Especialidade especialidade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "perfil_id", nullable = false)
    private Perfil perfil;

    @Column(name = "inicio_vigencia")
    private LocalDate inicioVigencia;

    @Column(name = "fim_vigencia")
    private LocalDate fimVigencia;

    @Column(nullable = false)
    private boolean ativo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ColaboradorUnidadeVinculo getColaboradorUnidadeVinculo() {
        return colaboradorUnidadeVinculo;
    }

    public void setColaboradorUnidadeVinculo(ColaboradorUnidadeVinculo colaboradorUnidadeVinculo) {
        this.colaboradorUnidadeVinculo = colaboradorUnidadeVinculo;
    }

    public FuncaoUnidade getFuncaoUnidade() {
        return funcaoUnidade;
    }

    public void setFuncaoUnidade(FuncaoUnidade funcaoUnidade) {
        this.funcaoUnidade = funcaoUnidade;
    }

    public Especialidade getEspecialidade() {
        return especialidade;
    }

    public void setEspecialidade(Especialidade especialidade) {
        this.especialidade = especialidade;
    }

    public Perfil getPerfil() {
        return perfil;
    }

    public void setPerfil(Perfil perfil) {
        this.perfil = perfil;
    }

    public LocalDate getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(LocalDate inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public LocalDate getFimVigencia() {
        return fimVigencia;
    }

    public void setFimVigencia(LocalDate fimVigencia) {
        this.fimVigencia = fimVigencia;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
}
