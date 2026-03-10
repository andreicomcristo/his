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
import org.springframework.data.repository.query.Param;

import br.com.his.care.inpatient.model.Internacao;

public interface InternacaoRepository extends JpaRepository<Internacao, Long> {

    Optional<Internacao> findByAtendimentoId(Long atendimentoId);

    Optional<Internacao> findByAtendimentoIdAndDataHoraCancelamentoIsNullAndDataHoraFimInternacaoIsNull(Long atendimentoId);

    @Query("""
            select i
            from Internacao i
            join fetch i.atendimento a
            join fetch a.paciente p
            join fetch a.unidade u
            left join fetch i.origemDemanda od
            left join fetch i.perfilInternacao pi
            where i.dataHoraCancelamento is null
            order by i.dataHoraDecisaoInternacao desc
            """)
    List<Internacao> findAllDetalhadoOrderByDataHoraDecisaoDesc();

    @Query("""
            select distinct i.atendimento.id
            from Internacao i
            where i.atendimento.id in :atendimentoIds
              and i.dataHoraCancelamento is null
              and i.dataHoraFimInternacao is null
            """)
    List<Long> findAtendimentoIdsComInternacao(@Param("atendimentoIds") List<Long> atendimentoIds);
}
