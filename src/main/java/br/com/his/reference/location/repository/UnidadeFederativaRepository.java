package br.com.his.reference.location.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.his.reference.location.model.UnidadeFederativa;

public interface UnidadeFederativaRepository extends JpaRepository<UnidadeFederativa, Long> {

    @Query("""
            select uf
            from UnidadeFederativa uf
            where uf.dtCancelamento is null
              and (
                    upper(uf.descricao) like concat('%', upper(:q), '%')
                    or upper(uf.sigla) like concat('%', upper(:q), '%')
              )
            order by uf.descricao
            """)
    List<UnidadeFederativa> buscarAtivasPorFiltro(String q);

    @Query("""
            select uf
            from UnidadeFederativa uf
            where uf.dtCancelamento is null
              and (
                    upper(uf.descricao) like concat('%', upper(:q), '%')
                    or upper(uf.sigla) like concat('%', upper(:q), '%')
              )
            order by uf.descricao
            """)
    List<UnidadeFederativa> buscarPorFiltro(String q);

    @Query("""
            select uf
            from UnidadeFederativa uf
            where uf.dtCancelamento is null
            order by uf.descricao
            """)
    List<UnidadeFederativa> findAtivasOrderByDescricaoAsc();

    @Query("""
            select uf
            from UnidadeFederativa uf
            where uf.dtCancelamento is null
            order by uf.descricao
            """)
    List<UnidadeFederativa> findAllByOrderByDescricaoAsc();

    @Query("""
            select uf
            from UnidadeFederativa uf
            where uf.dtCancelamento is not null
            order by uf.dtCancelamento desc, uf.descricao
            """)
    List<UnidadeFederativa> findCanceladasOrderByDescricaoAsc();

    @Query("""
            select uf
            from UnidadeFederativa uf
            where uf.dtCancelamento is not null
              and (
                    upper(uf.descricao) like concat('%', upper(:q), '%')
                    or upper(uf.sigla) like concat('%', upper(:q), '%')
              )
            order by uf.dtCancelamento desc, uf.descricao
            """)
    List<UnidadeFederativa> buscarCanceladasPorFiltro(String q);

    Optional<UnidadeFederativa> findByIdAndDtCancelamentoIsNull(Long id);

    Optional<UnidadeFederativa> findByIdAndDtCancelamentoIsNotNull(Long id);

    Optional<UnidadeFederativa> findBySiglaIgnoreCase(String sigla);
}
