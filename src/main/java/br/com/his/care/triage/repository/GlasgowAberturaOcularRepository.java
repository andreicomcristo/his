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

import br.com.his.care.triage.model.GlasgowAberturaOcular;

public interface GlasgowAberturaOcularRepository extends JpaRepository<GlasgowAberturaOcular, Long> {

    @Query("""
            select g
            from GlasgowAberturaOcular g
            where upper(g.descricao) like concat('%', upper(:q), '%')
               or str(g.pontos) like concat('%', :q, '%')
            order by g.pontos desc
            """)
    List<GlasgowAberturaOcular> buscarPorFiltro(String q);

    List<GlasgowAberturaOcular> findAllByOrderByPontosDesc();

    List<GlasgowAberturaOcular> findByAtivoTrueOrderByPontosDesc();
}
