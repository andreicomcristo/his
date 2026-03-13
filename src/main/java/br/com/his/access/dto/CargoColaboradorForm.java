package br.com.his.access.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CargoColaboradorForm {

    @NotBlank(message = "Informe o codigo")
    @Size(max = 80, message = "Codigo deve ter no maximo 80 caracteres")
    private String codigo;

    @NotBlank(message = "Informe a descricao")
    @Size(max = 150, message = "Descricao deve ter no maximo 150 caracteres")
    private String descricao;

    @NotNull(message = "Informe o tipo do cargo")
    private Long tipoCargoId;

    private boolean exigeEspecialidadeAgendamento;

    private boolean ativo = true;

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Long getTipoCargoId() {
        return tipoCargoId;
    }

    public void setTipoCargoId(Long tipoCargoId) {
        this.tipoCargoId = tipoCargoId;
    }

    public boolean isExigeEspecialidadeAgendamento() {
        return exigeEspecialidadeAgendamento;
    }

    public void setExigeEspecialidadeAgendamento(boolean exigeEspecialidadeAgendamento) {
        this.exigeEspecialidadeAgendamento = exigeEspecialidadeAgendamento;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
}
