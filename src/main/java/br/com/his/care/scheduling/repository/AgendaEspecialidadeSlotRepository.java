package br.com.his.care.scheduling.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.his.care.scheduling.model.AgendaEspecialidadeSlot;
import br.com.his.care.scheduling.model.StatusAgendaSlot;

public interface AgendaEspecialidadeSlotRepository extends JpaRepository<AgendaEspecialidadeSlot, Long> {

    Optional<AgendaEspecialidadeSlot> findByAgendaEspecialidadeIdAndDataHoraInicio(Long agendaEspecialidadeId, LocalDateTime dataHoraInicio);

    List<AgendaEspecialidadeSlot> findByAgendaEspecialidadeIdOrderByDataHoraInicioAsc(Long agendaEspecialidadeId);

    @Query("""
            select s
            from AgendaEspecialidadeSlot s
            join fetch s.agendaEspecialidade a
            join fetch a.cargoColaborador c
            left join fetch a.especialidade e
            where a.unidade.id = ?1
              and (?2 is null or c.id = ?2)
              and (?3 is null or e.id = ?3)
              and s.dataHoraInicio >= ?4
              and s.dataHoraInicio < ?5
            order by s.dataHoraInicio asc
            """)
    List<AgendaEspecialidadeSlot> listarCalendario(Long unidadeId,
                                                   Long cargoColaboradorId,
                                                                   Long especialidadeId,
                                                                   LocalDateTime dataHoraInicio,
                                                                   LocalDateTime dataHoraFim);

    long countByAgendaEspecialidadeIdAndStatus(Long agendaEspecialidadeId, StatusAgendaSlot status);
}
