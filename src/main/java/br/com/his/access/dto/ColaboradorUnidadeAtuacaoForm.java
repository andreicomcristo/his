package br.com.his.access.dto;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotNull;

public class ColaboradorUnidadeAtuacaoForm {

    @NotNull(message = "Informe o vinculo de unidade")
    private Long colaboradorUnidadeVinculoId;

    @NotNull(message = "Informe a funcao")
    private Long funcaoUnidadeId;

    private Long especialidadeId;

    @NotNull(message = "Informe o perfil de acesso")
    private Long perfilId;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate inicioVigencia;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fimVigencia;

    private boolean ativo = true;

    public Long getColaboradorUnidadeVinculoId() {
        return colaboradorUnidadeVinculoId;
    }

    public void setColaboradorUnidadeVinculoId(Long colaboradorUnidadeVinculoId) {
        this.colaboradorUnidadeVinculoId = colaboradorUnidadeVinculoId;
    }

    public Long getFuncaoUnidadeId() {
        return funcaoUnidadeId;
    }

    public void setFuncaoUnidadeId(Long funcaoUnidadeId) {
        this.funcaoUnidadeId = funcaoUnidadeId;
    }

    public Long getEspecialidadeId() {
        return especialidadeId;
    }

    public void setEspecialidadeId(Long especialidadeId) {
        this.especialidadeId = especialidadeId;
    }

    public Long getPerfilId() {
        return perfilId;
    }

    public void setPerfilId(Long perfilId) {
        this.perfilId = perfilId;
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
