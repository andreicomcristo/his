package br.com.his.care.attendance.dto;

import java.util.ArrayList;
import java.util.List;

import br.com.his.care.attendance.model.PrimeiroPassoFluxo;
import jakarta.validation.constraints.NotNull;

public class UnidadeFluxoForm {

    @NotNull(message = "Primeiro passo e obrigatorio")
    private PrimeiroPassoFluxo primeiroPasso;

    private boolean exigeFichaParaMedico;
    private boolean permiteAgendamento;

    private List<UnidadeTipoAtendimentoConfigForm> tiposAtendimento = new ArrayList<>();

    public PrimeiroPassoFluxo getPrimeiroPasso() {
        return primeiroPasso;
    }

    public void setPrimeiroPasso(PrimeiroPassoFluxo primeiroPasso) {
        this.primeiroPasso = primeiroPasso;
    }

    public boolean isExigeFichaParaMedico() {
        return exigeFichaParaMedico;
    }

    public void setExigeFichaParaMedico(boolean exigeFichaParaMedico) {
        this.exigeFichaParaMedico = exigeFichaParaMedico;
    }

    public boolean isPermiteAgendamento() {
        return permiteAgendamento;
    }

    public void setPermiteAgendamento(boolean permiteAgendamento) {
        this.permiteAgendamento = permiteAgendamento;
    }

    public List<UnidadeTipoAtendimentoConfigForm> getTiposAtendimento() {
        return tiposAtendimento;
    }

    public void setTiposAtendimento(List<UnidadeTipoAtendimentoConfigForm> tiposAtendimento) {
        this.tiposAtendimento = tiposAtendimento == null ? new ArrayList<>() : tiposAtendimento;
    }
}
