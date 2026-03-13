package br.com.his.care.scheduling.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.his.care.scheduling.model.AgendaEspecialidadePacienteHistorico;

public interface AgendaEspecialidadePacienteHistoricoRepository extends JpaRepository<AgendaEspecialidadePacienteHistorico, Long> {

    @Query("""
            select h
            from AgendaEspecialidadePacienteHistorico h
            where h.agendaPaciente.id = ?1
            order by h.criadoEm desc
            """)
    List<AgendaEspecialidadePacienteHistorico> listarPorAgendaPaciente(Long agendaPacienteId);
}
