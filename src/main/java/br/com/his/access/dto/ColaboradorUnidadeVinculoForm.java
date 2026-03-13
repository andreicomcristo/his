package br.com.his.access.dto;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotNull;

public class ColaboradorUnidadeVinculoForm {

    @NotNull(message = "Informe o colaborador")
    private Long colaboradorId;

    @NotNull(message = "Informe a unidade")
    private Long unidadeId;

    private Long tipoVinculoTrabalhistaId;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate inicioVigencia;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fimVigencia;

    private boolean ativo = true;

    public Long getColaboradorId() {
        return colaboradorId;
    }

    public void setColaboradorId(Long colaboradorId) {
        this.colaboradorId = colaboradorId;
    }

    public Long getUnidadeId() {
        return unidadeId;
    }

    public void setUnidadeId(Long unidadeId) {
        this.unidadeId = unidadeId;
    }

    public Long getTipoVinculoTrabalhistaId() {
        return tipoVinculoTrabalhistaId;
    }

    public void setTipoVinculoTrabalhistaId(Long tipoVinculoTrabalhistaId) {
        this.tipoVinculoTrabalhistaId = tipoVinculoTrabalhistaId;
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
