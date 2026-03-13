package br.com.his.access.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.his.access.model.ColaboradorUnidadeVinculo;

public interface ColaboradorUnidadeVinculoRepository extends JpaRepository<ColaboradorUnidadeVinculo, Long> {

    @Query("""
            select v
            from ColaboradorUnidadeVinculo v
            join fetch v.colaborador c
            join fetch v.unidade u
            left join fetch v.tipoVinculoTrabalhista tv
            order by c.nome, u.nome
            """)
    List<ColaboradorUnidadeVinculo> findAllComDetalhesOrderByColaboradorUnidadeAsc();

    @Query("""
            select v
            from ColaboradorUnidadeVinculo v
            join fetch v.colaborador c
            join fetch v.unidade u
            left join fetch v.tipoVinculoTrabalhista tv
            where v.ativo = ?1
            order by c.nome, u.nome
            """)
    List<ColaboradorUnidadeVinculo> findByAtivoComDetalhesOrderByColaboradorUnidadeAsc(Boolean ativo);

    @Query("""
            select v
            from ColaboradorUnidadeVinculo v
            join fetch v.colaborador c
            join fetch v.unidade u
            left join fetch v.tipoVinculoTrabalhista tv
            where upper(c.nome) like concat('%', upper(?1), '%')
               or upper(coalesce(c.cpf, '')) like concat('%', upper(?1), '%')
               or upper(u.nome) like concat('%', upper(?1), '%')
               or upper(coalesce(u.cnes, '')) like concat('%', upper(?1), '%')
               or upper(coalesce(tv.codigo, '')) like concat('%', upper(?1), '%')
               or upper(coalesce(tv.descricao, '')) like concat('%', upper(?1), '%')
            order by c.nome, u.nome
            """)
    List<ColaboradorUnidadeVinculo> listarPorBusca(String q);

    @Query("""
            select v
            from ColaboradorUnidadeVinculo v
            join fetch v.colaborador c
            join fetch v.unidade u
            left join fetch v.tipoVinculoTrabalhista tv
            where v.ativo = ?1
              and (
                    upper(c.nome) like concat('%', upper(?2), '%')
                    or upper(coalesce(c.cpf, '')) like concat('%', upper(?2), '%')
                    or upper(u.nome) like concat('%', upper(?2), '%')
                    or upper(coalesce(u.cnes, '')) like concat('%', upper(?2), '%')
                    or upper(coalesce(tv.codigo, '')) like concat('%', upper(?2), '%')
                    or upper(coalesce(tv.descricao, '')) like concat('%', upper(?2), '%')
              )
            order by c.nome, u.nome
            """)
    List<ColaboradorUnidadeVinculo> listarPorFiltroComBusca(Boolean ativo, String q);

    Optional<ColaboradorUnidadeVinculo> findByColaboradorIdAndUnidadeId(Long colaboradorId, Long unidadeId);

    @Query("""
            select v
            from ColaboradorUnidadeVinculo v
            join fetch v.colaborador c
            join fetch v.unidade u
            left join fetch v.tipoVinculoTrabalhista tv
            where v.ativo = true
            order by c.nome, u.nome
            """)
    List<ColaboradorUnidadeVinculo> findAtivosComDetalhesOrderByColaboradorUnidadeAsc();
}
