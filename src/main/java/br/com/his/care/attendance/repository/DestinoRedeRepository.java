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

import br.com.his.care.attendance.model.DestinoRede;

public interface DestinoRedeRepository extends JpaRepository<DestinoRede, Long> {

    @Query("""
            select d
            from DestinoRede d
            where upper(d.descricao) like concat('%', upper(:q), '%')
            order by d.descricao
            """)
    List<DestinoRede> buscarPorFiltro(String q);

    List<DestinoRede> findAllByOrderByDescricaoAsc();

    List<DestinoRede> findByAtivoTrueOrderByDescricaoAsc();

    Optional<DestinoRede> findByDescricaoIgnoreCase(String descricao);
}
