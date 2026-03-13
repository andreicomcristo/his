package br.com.his.care.scheduling.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.his.care.scheduling.model.AgendaEspecialidade;

public interface AgendaEspecialidadeRepository extends JpaRepository<AgendaEspecialidade, Long> {

    @Query("""
            select a
            from AgendaEspecialidade a
            join fetch a.cargoColaborador c
            left join fetch a.especialidade e
            where a.unidade.id = ?1
              and (?2 is null or a.cargoColaborador.id = ?2)
              and (?3 is null or a.especialidade.id = ?3)
              and a.dataAgenda >= coalesce(?4, a.dataAgenda)
              and a.dataAgenda <= coalesce(?5, a.dataAgenda)
            order by a.dataAgenda, a.horaInicio, c.descricao, coalesce(e.descricao, 'SEM ESPECIALIDADE')
            """)
    List<AgendaEspecialidade> listarPorFiltros(Long unidadeId,
                                               Long cargoColaboradorId,
                                               Long especialidadeId,
                                               LocalDate dataInicio,
                                               LocalDate dataFim);

    Optional<AgendaEspecialidade> findByIdAndUnidadeId(Long id, Long unidadeId);

    @Query("""
            select count(a) > 0
            from AgendaEspecialidade a
            where a.unidade.id = ?1
              and a.cargoColaborador.id = ?2
              and (
                    (?3 is null and a.especialidade is null)
                    or (?3 is not null and a.especialidade.id = ?3)
              )
              and a.dataAgenda = ?4
              and (?5 is null or a.id <> ?5)
              and a.horaInicio < ?7
              and a.horaFim > ?6
            """)
    boolean existsConflitoHorario(Long unidadeId,
                                  Long cargoColaboradorId,
                                  Long especialidadeId,
                                  LocalDate dataAgenda,
                                  Long agendaIdIgnorar,
                                  LocalTime horaInicio,
                                  LocalTime horaFim);

    @Query("""
            select a
            from AgendaEspecialidade a
            where a.unidade.id = ?1
              and a.cargoColaborador.id = ?2
              and (
                    (?3 is null and a.especialidade is null)
                    or (?3 is not null and a.especialidade.id = ?3)
              )
              and a.dataAgenda = ?4
              and a.ativo = true
              and a.horaInicio <= ?5
              and a.horaFim > ?5
            order by a.horaInicio
            """)
    List<AgendaEspecialidade> listarAtivasPorContextoDataEHorario(Long unidadeId,
                                                                   Long cargoColaboradorId,
                                                                   Long especialidadeId,
                                                                   LocalDate dataAgenda,
                                                                   LocalTime horarioAtendimento);
}
