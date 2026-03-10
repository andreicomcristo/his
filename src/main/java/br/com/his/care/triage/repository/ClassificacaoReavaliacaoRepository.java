package br.com.his.care.triage.repository;

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

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.his.care.triage.model.ClassificacaoReavaliacao;

public interface ClassificacaoReavaliacaoRepository extends JpaRepository<ClassificacaoReavaliacao, Long> {

    @Query("""
            select cr
            from ClassificacaoReavaliacao cr
            join fetch cr.classificacaoRisco r
            left join fetch cr.classificacaoCor
            where r.atendimento.id in :atendimentoIds
            order by cr.dataHora desc
            """)
    List<ClassificacaoReavaliacao> findByAtendimentoIdsOrderByDataHoraDesc(@Param("atendimentoIds") List<Long> atendimentoIds);

    @Query("""
            select cr
            from ClassificacaoReavaliacao cr
            join fetch cr.classificacaoCor
            where cr.classificacaoRisco.id = :classificacaoRiscoId
            order by cr.dataHora desc
            """)
    List<ClassificacaoReavaliacao> findByClassificacaoRiscoIdOrderByDataHoraDesc(@Param("classificacaoRiscoId") Long classificacaoRiscoId);

    ClassificacaoReavaliacao findFirstByClassificacaoRiscoIdOrderByDataHoraDesc(Long classificacaoRiscoId);
}
