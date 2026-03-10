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

import br.com.his.care.triage.model.AvcSinalAlerta;

public interface AvcSinalAlertaRepository extends JpaRepository<AvcSinalAlerta, Long> {

    @Query("""
            select a
            from AvcSinalAlerta a
            where upper(a.codigo) like concat('%', upper(:q), '%')
               or upper(a.descricao) like concat('%', upper(:q), '%')
            order by a.ordemExibicao, a.descricao
            """)
    List<AvcSinalAlerta> buscarPorFiltro(String q);

    List<AvcSinalAlerta> findAllByOrderByIdAsc();

    List<AvcSinalAlerta> findByAtivoTrueOrderByIdAsc();

    List<AvcSinalAlerta> findAllByOrderByOrdemExibicaoAscDescricaoAsc();

    List<AvcSinalAlerta> findByAtivoTrueOrderByOrdemExibicaoAscDescricaoAsc();

    boolean existsByCodigoIgnoreCase(String codigo);

    boolean existsByCodigoIgnoreCaseAndIdNot(String codigo, Long id);
}
