package br.com.his.access.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.his.access.model.TipoVinculoTrabalhista;

public interface TipoVinculoTrabalhistaRepository extends JpaRepository<TipoVinculoTrabalhista, Long> {

    List<TipoVinculoTrabalhista> findAllByOrderByDescricaoAsc();

    List<TipoVinculoTrabalhista> findByAtivoOrderByDescricaoAsc(Boolean ativo);

    @Query("""
            select t
            from TipoVinculoTrabalhista t
            where upper(t.codigo) like concat('%', upper(?1), '%')
               or upper(t.descricao) like concat('%', upper(?1), '%')
            order by t.descricao
            """)
    List<TipoVinculoTrabalhista> listarPorBusca(String q);

    @Query("""
            select t
            from TipoVinculoTrabalhista t
            where t.ativo = ?1
              and (
                    upper(t.codigo) like concat('%', upper(?2), '%')
                    or upper(t.descricao) like concat('%', upper(?2), '%')
              )
            order by t.descricao
            """)
    List<TipoVinculoTrabalhista> listarPorFiltroComBusca(Boolean ativo, String q);

    @Query("""
            select t
            from TipoVinculoTrabalhista t
            where upper(t.codigo) = upper(?1)
            """)
    Optional<TipoVinculoTrabalhista> findByCodigoIgnoreCase(String codigo);
}
