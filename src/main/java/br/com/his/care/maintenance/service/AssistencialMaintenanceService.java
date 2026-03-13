package br.com.his.care.maintenance.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AssistencialMaintenanceService {

    private final JdbcTemplate jdbcTemplate;

    public AssistencialMaintenanceService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public void resetFluxoAssistencialSemPacientes() {
        jdbcTemplate.execute("""
                TRUNCATE TABLE
                    atendimento_evento,
                    atendimento_periodo,
                    entrada,
                    classificacao_risco,
                    episodio,
                    atendimento
                RESTART IDENTITY CASCADE
                """);
    }

    @Transactional
    public void resetFluxoAgendamento() {
        jdbcTemplate.execute("""
                TRUNCATE TABLE
                    agenda_especialidade_paciente_hist,
                    agenda_especialidade_paciente,
                    agenda_especialidade_slot,
                    agenda_especialidade
                RESTART IDENTITY CASCADE
                """);
    }
}
