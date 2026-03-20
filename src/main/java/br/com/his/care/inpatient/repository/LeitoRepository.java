package br.com.his.care.inpatient.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.his.care.inpatient.model.Leito;

public interface LeitoRepository extends JpaRepository<Leito, Long> {

    Optional<Leito> findByIdAndDtCancelamentoIsNull(Long id);
    Optional<Leito> findByIdAndDtCancelamentoIsNotNull(Long id);

    @Query("""
            select l
            from Leito l
            join fetch l.unidade u
            join fetch l.area a
            join fetch l.tipoLeito t
            left join fetch l.perfilLeito p
            join fetch l.naturezaOperacional n
            where l.dtCancelamento is null
              and (
                   upper(l.codigo) like concat('%', upper(:q), '%')
                or upper(coalesce(l.descricao, '')) like concat('%', upper(:q), '%')
                or upper(a.descricao) like concat('%', upper(:q), '%')
                or upper(u.nome) like concat('%', upper(:q), '%')
                or upper(t.descricao) like concat('%', upper(:q), '%')
                or upper(coalesce(p.descricao, '')) like concat('%', upper(:q), '%')
                or upper(n.descricao) like concat('%', upper(:q), '%')
              )
            order by u.nome, a.descricao, l.codigo
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
            where l.dtCancelamento is null
            order by u.nome, a.descricao, l.codigo
            """)
    List<Leito> findAllWithReferencesOrderByNome();

    @Query("""
            select l
            from Leito l
            join fetch l.unidade u
            join fetch l.area a
            join fetch l.tipoLeito t
            left join fetch l.perfilLeito p
            join fetch l.naturezaOperacional n
            where l.dtCancelamento is not null
              and (
                   upper(l.codigo) like concat('%', upper(:q), '%')
                or upper(coalesce(l.descricao, '')) like concat('%', upper(:q), '%')
                or upper(a.descricao) like concat('%', upper(:q), '%')
                or upper(u.nome) like concat('%', upper(:q), '%')
                or upper(t.descricao) like concat('%', upper(:q), '%')
                or upper(coalesce(p.descricao, '')) like concat('%', upper(:q), '%')
                or upper(n.descricao) like concat('%', upper(:q), '%')
              )
            order by l.dtCancelamento desc, u.nome, a.descricao, l.codigo
            """)
    List<Leito> buscarCanceladosPorFiltro(String q);

    @Query("""
            select l
            from Leito l
            join fetch l.unidade u
            join fetch l.area a
            join fetch l.tipoLeito t
            left join fetch l.perfilLeito p
            join fetch l.naturezaOperacional n
            where l.dtCancelamento is not null
            order by l.dtCancelamento desc, u.nome, a.descricao, l.codigo
            """)
    List<Leito> findAllCanceladosWithReferencesOrderByNome();

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
              and l.dtCancelamento is null
            order by a.descricao, l.codigo
            """)
    List<Leito> findAtivosByUnidadeIdOrderByAreaENome(Long unidadeId);
}
