package br.com.his.care.inpatient.repository;

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

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.his.care.inpatient.model.LeitoOcupacao;

public interface LeitoOcupacaoRepository extends JpaRepository<LeitoOcupacao, Long> {

    List<LeitoOcupacao> findByLeitoIdOrderByDataHoraEntradaDesc(Long leitoId);

    List<LeitoOcupacao> findByObservacaoAtendimentoIdOrderByDataHoraEntradaDesc(Long observacaoId);

    List<LeitoOcupacao> findByInternacaoIdOrderByDataHoraEntradaDesc(Long internacaoId);

    Optional<LeitoOcupacao> findFirstByLeitoIdAndDataHoraSaidaIsNull(Long leitoId);

    Optional<LeitoOcupacao> findFirstByObservacaoAtendimentoIdAndDataHoraSaidaIsNull(Long observacaoId);

    Optional<LeitoOcupacao> findFirstByInternacaoIdAndDataHoraSaidaIsNull(Long internacaoId);

    @Query("""
            select lo
            from LeitoOcupacao lo
            join fetch lo.leito l
            join fetch l.area a
            where lo.observacaoAtendimento.id in :observacaoIds
              and lo.dataHoraSaida is null
            order by lo.dataHoraEntrada desc
            """)
    List<LeitoOcupacao> findAbertasByObservacaoIds(List<Long> observacaoIds);

    @Query("""
            select lo
            from LeitoOcupacao lo
            join fetch lo.leito l
            join fetch l.area a
            where lo.internacao.id in :internacaoIds
              and lo.dataHoraSaida is null
            order by lo.dataHoraEntrada desc
            """)
    List<LeitoOcupacao> findAbertasByInternacaoIds(List<Long> internacaoIds);

    @Query("""
            select lo.leito.id
            from LeitoOcupacao lo
            where lo.dataHoraSaida is null
              and lo.leito.id in :leitoIds
            """)
    List<Long> findLeitoIdsComOcupacaoAberta(List<Long> leitoIds);

    @Query("""
            select lo
            from LeitoOcupacao lo
            join fetch lo.leito l
            join fetch l.area a
            join fetch lo.tipoOcupacao to
            left join fetch lo.observacaoAtendimento o
            left join fetch o.atendimento ao
            left join fetch ao.paciente aop
            left join fetch lo.internacao i
            left join fetch i.atendimento ai
            left join fetch ai.paciente aip
            where lo.dataHoraSaida is null
              and l.id in :leitoIds
            order by lo.dataHoraEntrada desc
            """)
    List<LeitoOcupacao> findAbertasDetalhadasByLeitoIds(List<Long> leitoIds);

    @Query("""
            select lo
            from LeitoOcupacao lo
            join fetch lo.leito l
            join fetch l.area a
            join fetch lo.tipoOcupacao to
            left join fetch lo.observacaoAtendimento o
            left join fetch o.atendimento ao
            left join fetch ao.paciente aop
            left join fetch lo.internacao i
            left join fetch i.atendimento ai
            left join fetch ai.paciente aip
            where lo.id = :id
            """)
    Optional<LeitoOcupacao> findByIdDetalhada(Long id);
}
