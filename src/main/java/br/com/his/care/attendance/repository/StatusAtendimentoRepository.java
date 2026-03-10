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

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.his.care.attendance.model.StatusAtendimento;

public interface StatusAtendimentoRepository extends JpaRepository<StatusAtendimento, Long> {

    @Query("""
            select s
            from StatusAtendimento s
            where upper(s.codigo) like concat('%', upper(?1), '%')
               or upper(s.descricao) like concat('%', upper(?1), '%')
            order by s.descricao
            """)
    List<StatusAtendimento> buscarPorFiltro(String q);

    List<StatusAtendimento> findAllByOrderByDescricaoAsc();

    List<StatusAtendimento> findByAtivoTrueOrderByDescricaoAsc();

    Optional<StatusAtendimento> findByCodigoIgnoreCase(String codigo);
}
