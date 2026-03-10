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

import br.com.his.care.inpatient.model.Observacao;

public interface ObservacaoRepository extends JpaRepository<Observacao, Long> {

    Optional<Observacao> findByAtendimentoId(Long atendimentoId);

    Optional<Observacao> findByAtendimentoIdAndDataHoraCancelamentoIsNullAndDataHoraFimIsNull(Long atendimentoId);

    @Query("""
            select distinct o.atendimento.id
            from Observacao o
            where o.atendimento.id in :atendimentoIds
              and o.dataHoraCancelamento is null
              and o.dataHoraFim is null
            """)
    List<Long> findAtendimentoIdsComObservacaoAtiva(@Param("atendimentoIds") List<Long> atendimentoIds);

    @Query("""
            select o
            from Observacao o
            join fetch o.atendimento a
            join fetch a.paciente p
            join fetch a.unidade u
            where o.dataHoraCancelamento is null
            order by o.dataHoraInicio desc
            """)
    List<Observacao> findAllDetalhadoOrderByDataHoraInicioDesc();
}
