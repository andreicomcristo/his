package br.com.his.access.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.his.access.model.TipoUnidade;

public interface TipoUnidadeRepository extends JpaRepository<TipoUnidade, Long> {

    List<TipoUnidade> findByDtCancelamentoIsNullOrderByDescricaoAsc();

    Optional<TipoUnidade> findByIdAndDtCancelamentoIsNull(Long id);

    Optional<TipoUnidade> findByIdAndDtCancelamentoIsNotNull(Long id);

    @Query("""
            select t
            from TipoUnidade t
            where t.dtCancelamento is null
              and (
                    upper(t.codigo) like concat('%', upper(?1), '%')
                    or upper(t.descricao) like concat('%', upper(?1), '%')
              )
            order by t.descricao
            """)
    List<TipoUnidade> listarAtivosPorBusca(String q);

    @Query("""
            select t
            from TipoUnidade t
            where t.dtCancelamento is not null
            order by t.dtCancelamento desc, t.descricao
            """)
    List<TipoUnidade> listarCancelados();

    @Query("""
            select t
            from TipoUnidade t
            where t.dtCancelamento is not null
              and (
                    upper(t.codigo) like concat('%', upper(?1), '%')
                    or upper(t.descricao) like concat('%', upper(?1), '%')
              )
            order by t.dtCancelamento desc, t.descricao
            """)
    List<TipoUnidade> listarCanceladosPorBusca(String q);

    @Query("""
            select t
            from TipoUnidade t
            where upper(t.codigo) = upper(?1)
            """)
    Optional<TipoUnidade> findByCodigoIgnoreCase(String codigo);
}
