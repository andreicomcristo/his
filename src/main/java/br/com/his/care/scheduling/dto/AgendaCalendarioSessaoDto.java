package br.com.his.care.scheduling.dto;

import java.time.LocalDateTime;

public class AgendaCalendarioSessaoDto {

    private final Long agendaId;
    private final String cargo;
    private final String especialidade;
    private final LocalDateTime inicio;
    private final LocalDateTime fim;
    private final String modoAgenda;
    private final int vagasTotais;
    private final long vagasOcupadas;
    private final long vagasLivres;

    public AgendaCalendarioSessaoDto(Long agendaId,
                                     String cargo,
                                     String especialidade,
                                     LocalDateTime inicio,
                                     LocalDateTime fim,
                                     String modoAgenda,
                                     int vagasTotais,
                                     long vagasOcupadas,
                                     long vagasLivres) {
        this.agendaId = agendaId;
        this.cargo = cargo;
        this.especialidade = especialidade;
        this.inicio = inicio;
        this.fim = fim;
        this.modoAgenda = modoAgenda;
        this.vagasTotais = vagasTotais;
        this.vagasOcupadas = vagasOcupadas;
        this.vagasLivres = vagasLivres;
    }

    public Long getAgendaId() {
        return agendaId;
    }

    public String getCargo() {
        return cargo;
    }

    public String getEspecialidade() {
        return especialidade;
    }

    public LocalDateTime getInicio() {
        return inicio;
    }

    public LocalDateTime getFim() {
        return fim;
    }

    public String getModoAgenda() {
        return modoAgenda;
    }

    public int getVagasTotais() {
        return vagasTotais;
    }

    public long getVagasOcupadas() {
        return vagasOcupadas;
    }

    public long getVagasLivres() {
        return vagasLivres;
    }
}
