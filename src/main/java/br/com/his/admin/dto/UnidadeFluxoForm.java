package br.com.his.admin.dto;

import br.com.his.care.attendance.model.PrimeiroPassoFluxo;
import jakarta.validation.constraints.NotNull;

public class UnidadeFluxoForm {

    @NotNull(message = "Primeiro passo e obrigatorio")
    private PrimeiroPassoFluxo primeiroPasso;

    private boolean exigeFichaParaMedico;

    private boolean triagemObrigatoriaUrgencia;
    private boolean triagemObrigatoriaAmbulatorial;
    private boolean triagemObrigatoriaInternacaoDireta;
    private boolean triagemObrigatoriaProcedimento;

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

    public boolean isTriagemObrigatoriaUrgencia() {
        return triagemObrigatoriaUrgencia;
    }

    public void setTriagemObrigatoriaUrgencia(boolean triagemObrigatoriaUrgencia) {
        this.triagemObrigatoriaUrgencia = triagemObrigatoriaUrgencia;
    }

    public boolean isTriagemObrigatoriaAmbulatorial() {
        return triagemObrigatoriaAmbulatorial;
    }

    public void setTriagemObrigatoriaAmbulatorial(boolean triagemObrigatoriaAmbulatorial) {
        this.triagemObrigatoriaAmbulatorial = triagemObrigatoriaAmbulatorial;
    }

    public boolean isTriagemObrigatoriaInternacaoDireta() {
        return triagemObrigatoriaInternacaoDireta;
    }

    public void setTriagemObrigatoriaInternacaoDireta(boolean triagemObrigatoriaInternacaoDireta) {
        this.triagemObrigatoriaInternacaoDireta = triagemObrigatoriaInternacaoDireta;
    }

    public boolean isTriagemObrigatoriaProcedimento() {
        return triagemObrigatoriaProcedimento;
    }

    public void setTriagemObrigatoriaProcedimento(boolean triagemObrigatoriaProcedimento) {
        this.triagemObrigatoriaProcedimento = triagemObrigatoriaProcedimento;
    }
}
