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

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.his.care.inpatient.model.Area;

public interface AreaRepository extends JpaRepository<Area, Long> {

    @Query("""
            select distinct a
            from Area a
            join fetch a.unidade u
            join AreaCapacidade ac on ac.area.id = a.id
            join CapacidadeArea ca on ca.id = ac.capacidadeArea.id
            where u.id = :unidadeId
              and a.ativo = true
              and ca.ativo = true
              and upper(ca.nome) = 'RECEBE_ENTRADA'
            order by a.nome
            """)
    List<Area> findAreasAtivasRecebemEntradaByUnidadeId(Long unidadeId);

    @Query("""
            select a
            from Area a
            join fetch a.unidade u
            where upper(a.nome) like concat('%', upper(:q), '%')
                or upper(u.nome) like concat('%', upper(:q), '%')
            order by u.nome, a.nome
            """)
    List<Area> buscarPorFiltro(String q);

    @Query("""
            select a
            from Area a
            join fetch a.unidade u
            order by u.nome, a.nome
            """)
    List<Area> findAllWithUnidadeOrderByNome();

    @Query("""
            select distinct a
            from Area a
            join fetch a.unidade u
            join AreaCapacidade ac on ac.area.id = a.id
            join CapacidadeArea ca on ca.id = ac.capacidadeArea.id
            where a.ativo = true
              and ca.ativo = true
              and upper(ca.nome) = 'POSSUI_LEITO'
            order by u.nome, a.nome
            """)
    List<Area> findAreasAtivasComLeito();
}


