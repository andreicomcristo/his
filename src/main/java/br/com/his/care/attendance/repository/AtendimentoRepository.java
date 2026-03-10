package br.com.his.care.attendance.repository;

import br.com.his.care.attendance.api.dto.*;
import br.com.his.care.attendance.dto.*;
import br.com.his.care.attendance.model.*;
import br.com.his.care.attendance.repository.*;
import br.com.his.care.attendance.service.*;
import br.com.his.care.admission.dto.*;
import br.com.his.care.admission.model.*;
import br.com.his.care.admission.repository.*;
import br.com.his.care.triage.dto.*;
import br.com.his.care.triage.model.*;
import br.com.his.care.triage.repository.*;
import br.com.his.care.inpatient.dto.*;
import br.com.his.care.inpatient.model.*;
import br.com.his.care.inpatient.repository.*;
import br.com.his.care.inpatient.service.*;
import br.com.his.care.episode.model.*;
import br.com.his.care.episode.repository.*;
import br.com.his.care.timeline.dto.*;
import br.com.his.care.timeline.model.*;
import br.com.his.care.timeline.repository.*;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.his.care.attendance.model.Atendimento;

public interface AtendimentoRepository extends JpaRepository<Atendimento, Long> {

    List<Atendimento> findByUnidadeIdAndDataHoraChegadaBetweenOrderByDataHoraChegadaDesc(Long unidadeId,
                                                                                           LocalDateTime inicio,
                                                                                           LocalDateTime fim);

    List<Atendimento> findByUnidadeIdAndStatusCodigoInOrderByDataHoraChegadaAsc(Long unidadeId,
                                                                                List<String> statusCodigos);

    java.util.Optional<Atendimento> findFirstByPacienteIdAndUnidadeIdAndStatusCodigoInOrderByDataHoraChegadaAsc(Long pacienteId,
                                                                                                                 Long unidadeId,
                                                                                                                 List<String> statusCodigos);

    @Query("""
            SELECT a
            FROM Atendimento a
            JOIN FETCH a.paciente p
            WHERE a.unidade.id = :unidadeId
              AND a.status.codigo IN :statusCodigos
            ORDER BY a.dataHoraChegada ASC
            """)
    List<Atendimento> findFilaClassificacao(@Param("unidadeId") Long unidadeId,
                                            @Param("statusCodigos") List<String> statusCodigos);

    @Query("""
            SELECT a
            FROM Atendimento a
            JOIN FETCH a.paciente p
            WHERE a.unidade.id = :unidadeId
              AND a.status.codigo IN :statusCodigos
              AND p.temporario = true
              AND p.ativo = true
              AND p.mergedInto IS NULL
            ORDER BY a.dataHoraChegada ASC
            """)
    List<Atendimento> findNaoIdentificadosEmAberto(@Param("unidadeId") Long unidadeId,
                                                   @Param("statusCodigos") List<String> statusCodigos);

    @Query("""
            select a
            from Atendimento a
            join fetch a.paciente p
            where a.unidade.id = :unidadeId
              and a.status.codigo in :statusCodigos
              and not exists (
                  select 1
                  from Observacao o
                  where o.atendimento.id = a.id
                    and o.dataHoraCancelamento is null
                    and o.dataHoraFim is null
              )
              and not exists (
                  select 1
                  from Internacao i
                  where i.atendimento.id = a.id
                    and i.dataHoraCancelamento is null
                    and i.dataHoraFimInternacao is null
              )
            order by a.dataHoraChegada desc
            """)
    List<Atendimento> findAbertosSemObservacao(@Param("unidadeId") Long unidadeId,
                                               @Param("statusCodigos") List<String> statusCodigos);

    @Query("""
            select a
            from Atendimento a
            join fetch a.paciente p
            where a.unidade.id = :unidadeId
              and a.status.codigo in :statusCodigos
              and not exists (
                  select 1
                  from Observacao o
                  where o.atendimento.id = a.id
                    and o.dataHoraCancelamento is null
                    and o.dataHoraFim is null
              )
              and not exists (
                  select 1
                  from Internacao i
                  where i.atendimento.id = a.id
                    and i.dataHoraCancelamento is null
                    and i.dataHoraFimInternacao is null
              )
            order by a.dataHoraChegada desc
            """)
    List<Atendimento> findAbertosSemInternacao(@Param("unidadeId") Long unidadeId,
                                               @Param("statusCodigos") List<String> statusCodigos);
}
