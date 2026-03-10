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

import br.com.his.care.inpatient.model.Leito;

public interface LeitoRepository extends JpaRepository<Leito, Long> {

    @Query("""
            select l
            from Leito l
            join fetch l.unidade u
            join fetch l.area a
            join fetch l.tipoLeito t
            left join fetch l.perfilLeito p
            join fetch l.naturezaOperacional n
            where upper(l.codigo) like concat('%', upper(:q), '%')
               or upper(coalesce(l.descricao, '')) like concat('%', upper(:q), '%')
               or upper(a.nome) like concat('%', upper(:q), '%')
               or upper(u.nome) like concat('%', upper(:q), '%')
               or upper(t.descricao) like concat('%', upper(:q), '%')
               or upper(coalesce(p.descricao, '')) like concat('%', upper(:q), '%')
               or upper(n.descricao) like concat('%', upper(:q), '%')
            order by u.nome, a.nome, l.codigo
            """)
    List<Leito> buscarPorFiltro(String q);

    @Query("""
            select l
            from Leito l
            join fetch l.unidade u
            join fetch l.area a
            join fetch l.tipoLeito t
            left join fetch l.perfilLeito p
            join fetch l.naturezaOperacional n
            order by u.nome, a.nome, l.codigo
            """)
    List<Leito> findAllWithReferencesOrderByNome();

    @Query("""
            select count(l) > 0
            from Leito l
            where l.unidade.id = :unidadeId
              and upper(l.codigo) = upper(:codigo)
              and (:ignoreId is null or l.id <> :ignoreId)
            """)
    boolean existsCodigoByUnidade(Long unidadeId, String codigo, Long ignoreId);

    boolean existsByNaturezaOperacionalId(Long naturezaOperacionalId);

    @Query("""
            select l
            from Leito l
            join fetch l.unidade u
            join fetch l.area a
            join fetch l.tipoLeito t
            left join fetch l.perfilLeito p
            join fetch l.naturezaOperacional n
            where l.unidade.id = :unidadeId
              and l.ativo = true
            order by a.nome, l.codigo
            """)
    List<Leito> findAtivosByUnidadeIdOrderByAreaENome(Long unidadeId);
}
