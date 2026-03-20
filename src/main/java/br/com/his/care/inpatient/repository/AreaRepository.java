package br.com.his.care.inpatient.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.his.care.inpatient.model.Area;

public interface AreaRepository extends JpaRepository<Area, Long> {

    Optional<Area> findByIdAndDtCancelamentoIsNull(Long id);
    Optional<Area> findByIdAndDtCancelamentoIsNotNull(Long id);

    @Query("""
            select distinct a
            from Area a
            join fetch a.unidade u
            join AreaCapacidade ac on ac.area.id = a.id
            join CapacidadeArea ca on ca.id = ac.capacidadeArea.id
            where u.id = :unidadeId
              and a.dtCancelamento is null
              and ca.ativo = true
              and upper(ca.nome) = 'RECEBE_ENTRADA'
            order by a.descricao
            """)
    List<Area> findAreasAtivasRecebemEntradaByUnidadeId(Long unidadeId);

    @Query("""
            select a
            from Area a
            join fetch a.unidade u
            where a.dtCancelamento is null
              and (
                   upper(a.descricao) like concat('%', upper(:q), '%')
                or upper(coalesce(a.detalhamento, '')) like concat('%', upper(:q), '%')
                or upper(u.nome) like concat('%', upper(:q), '%')
              )
            order by u.nome, a.descricao
            """)
    List<Area> buscarPorFiltro(String q);

    @Query("""
            select a
            from Area a
            join fetch a.unidade u
            where a.dtCancelamento is null
            order by u.nome, a.descricao
            """)
    List<Area> findAllAtivasWithUnidadeOrderByDescricao();

    @Query("""
            select a
            from Area a
            join fetch a.unidade u
            where a.dtCancelamento is not null
              and (
                   upper(a.descricao) like concat('%', upper(:q), '%')
                or upper(coalesce(a.detalhamento, '')) like concat('%', upper(:q), '%')
                or upper(u.nome) like concat('%', upper(:q), '%')
              )
            order by a.dtCancelamento desc, u.nome, a.descricao
            """)
    List<Area> buscarCanceladosPorFiltro(String q);

    @Query("""
            select a
            from Area a
            join fetch a.unidade u
            where a.dtCancelamento is not null
            order by a.dtCancelamento desc, u.nome, a.descricao
            """)
    List<Area> findAllCanceladasWithUnidadeOrderByDescricao();

    @Query("""
            select distinct a
            from Area a
            join fetch a.unidade u
            join AreaCapacidade ac on ac.area.id = a.id
            join CapacidadeArea ca on ca.id = ac.capacidadeArea.id
            where a.dtCancelamento is null
              and ca.ativo = true
              and upper(ca.nome) = 'POSSUI_LEITO'
            order by u.nome, a.descricao
            """)
    List<Area> findAreasAtivasComLeito();
}


