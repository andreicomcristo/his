package br.com.his.assistencial.dto;

import br.com.his.assistencial.model.TipoAtendimento;
import jakarta.validation.constraints.NotNull;

public class AtendimentoNovoForm {

    @NotNull(message = "Paciente e obrigatorio")
    private Long pacienteId;
    private String chegadaToken;

    @NotNull(message = "Tipo de atendimento e obrigatorio")
    private TipoAtendimento tipoAtendimento;

    public Long getPacienteId() {
        return pacienteId;
    }

    public void setPacienteId(Long pacienteId) {
        this.pacienteId = pacienteId;
    }

    public String getChegadaToken() {
        return chegadaToken;
    }

    public void setChegadaToken(String chegadaToken) {
        this.chegadaToken = chegadaToken;
    }

    public TipoAtendimento getTipoAtendimento() {
        return tipoAtendimento;
    }

    public void setTipoAtendimento(TipoAtendimento tipoAtendimento) {
        this.tipoAtendimento = tipoAtendimento;
    }
}
