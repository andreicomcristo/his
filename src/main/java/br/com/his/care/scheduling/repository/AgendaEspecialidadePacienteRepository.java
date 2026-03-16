package br.com.his.care.scheduling.repository;

import java.util.List;
import java.time.LocalTime;
import java.util.Optional;
import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.his.care.scheduling.model.AgendaEspecialidadePaciente;
import br.com.his.care.scheduling.model.StatusAgendamentoPaciente;
import br.com.his.care.scheduling.model.TipoVagaAgenda;

public interface AgendaEspecialidadePacienteRepository extends JpaRepository<AgendaEspecialidadePaciente, Long> {

    @Query("""
            select ap
            from AgendaEspecialidadePaciente ap
            join fetch ap.paciente p
            join fetch ap.agendaSlot s
            where ap.agendaEspecialidade.id = ?1
            order by s.dataHoraInicio, p.nome
            """)
    List<AgendaEspecialidadePaciente> listarPorAgenda(Long agendaEspecialidadeId);

    @Query("""
            select ap
            from AgendaEspecialidadePaciente ap
            join fetch ap.paciente p
            join fetch ap.agendaEspecialidade a
            join fetch ap.agendaSlot s
            where ap.id = ?1
            """)
    Optional<AgendaEspecialidadePaciente> buscarPorIdComRelacionamentos(Long id);

    boolean existsByAgendaEspecialidadeId(Long agendaEspecialidadeId);

    boolean existsByAgendaEspecialidadeIdAndPacienteIdAndStatusNot(Long agendaEspecialidadeId,
                                                                    Long pacienteId,
                                                                    StatusAgendamentoPaciente status);

    boolean existsByAgendaSlotIdAndStatusNot(Long agendaSlotId, StatusAgendamentoPaciente status);

    boolean existsByAgendaSlotIdAndIdNotAndStatusNot(Long agendaSlotId,
                                                      Long agendaPacienteId,
                                                      StatusAgendamentoPaciente status);

    @Query("""
            select count(ap)
            from AgendaEspecialidadePaciente ap
            where ap.agendaEspecialidade.id = ?1
              and ap.status <> br.com.his.care.scheduling.model.StatusAgendamentoPaciente.CANCELADO
            """)
    long countByAgendaEspecialidadeId(Long agendaEspecialidadeId);

    @Query("""
            select count(ap)
            from AgendaEspecialidadePaciente ap
            where ap.agendaEspecialidade.id = ?1
              and ap.tipoVaga = ?2
              and ap.status <> br.com.his.care.scheduling.model.StatusAgendamentoPaciente.CANCELADO
            """)
    long countByAgendaEspecialidadeIdAndTipoVaga(Long agendaEspecialidadeId, TipoVagaAgenda tipoVaga);

    @Query("""
            select ap.horaAtendimento
            from AgendaEspecialidadePaciente ap
            where ap.agendaEspecialidade.id = ?1
              and ap.status <> br.com.his.care.scheduling.model.StatusAgendamentoPaciente.CANCELADO
            order by ap.horaAtendimento
            """)
    List<LocalTime> listarHorariosOcupadosPorAgenda(Long agendaEspecialidadeId);

    @Query("""
            select ap.agendaSlot.id
            from AgendaEspecialidadePaciente ap
            where ap.agendaEspecialidade.id = ?1
              and ap.status <> br.com.his.care.scheduling.model.StatusAgendamentoPaciente.CANCELADO
            """)
    List<Long> listarSlotsOcupadosPorAgenda(Long agendaEspecialidadeId);

    @Query("""
            select ap.agendaEspecialidade.id, count(ap)
            from AgendaEspecialidadePaciente ap
            where ap.agendaEspecialidade.id in ?1
              and ap.status <> br.com.his.care.scheduling.model.StatusAgendamentoPaciente.CANCELADO
            group by ap.agendaEspecialidade.id
            """)
    List<Object[]> contarAgendadosPorAgendaIds(Collection<Long> agendaIds);

    @Query("""
            select ap
            from AgendaEspecialidadePaciente ap
            join fetch ap.paciente p
            join fetch ap.agendaEspecialidade a
            join fetch a.cargoColaborador c
            left join fetch a.especialidade e
            join fetch ap.agendaSlot s
            where a.unidade.id = ?1
              and (?2 is null or c.id = ?2)
              and (?3 is null or e.id = ?3)
              and s.dataHoraInicio >= ?4
              and s.dataHoraInicio < ?5
            order by s.dataHoraInicio asc
            """)
    List<AgendaEspecialidadePaciente> listarParaCalendario(Long unidadeId,
                                                            Long cargoColaboradorId,
                                                                            Long especialidadeId,
                                                                            java.time.LocalDateTime dataHoraInicio,
                                                                            java.time.LocalDateTime dataHoraFim);

    @Query("""
            select ap
            from AgendaEspecialidadePaciente ap
            join fetch ap.paciente p
            join fetch ap.agendaEspecialidade a
            join fetch a.cargoColaborador c
            left join fetch a.especialidade e
            join fetch ap.agendaSlot s
            where a.unidade.id = ?1
              and (?2 is null or c.id = ?2)
              and (?3 is null or e.id = ?3)
              and (?4 is null or ap.status = ?4)
              and s.dataHoraInicio >= ?5
              and s.dataHoraInicio < ?6
            order by s.dataHoraInicio asc, p.nome asc
            """)
    List<AgendaEspecialidadePaciente> listarParaRecepcao(Long unidadeId,
                                                          Long cargoColaboradorId,
                                                          Long especialidadeId,
                                                          StatusAgendamentoPaciente status,
                                                          java.time.LocalDateTime dataHoraInicio,
                                                          java.time.LocalDateTime dataHoraFim);
}
