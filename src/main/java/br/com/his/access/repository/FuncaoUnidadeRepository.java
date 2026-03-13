package br.com.his.access.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.his.access.model.FuncaoUnidade;

public interface FuncaoUnidadeRepository extends JpaRepository<FuncaoUnidade, Long> {

    List<FuncaoUnidade> findAllByOrderByDescricaoAsc();

    List<FuncaoUnidade> findByAtivoOrderByDescricaoAsc(Boolean ativo);

    @Query("""
            select f
            from FuncaoUnidade f
            where upper(f.codigo) like concat('%', upper(?1), '%')
               or upper(f.descricao) like concat('%', upper(?1), '%')
            order by f.descricao
            """)
    List<FuncaoUnidade> listarPorBusca(String q);

    @Query("""
            select f
            from FuncaoUnidade f
            where f.ativo = ?1
              and (
                    upper(f.codigo) like concat('%', upper(?2), '%')
                    or upper(f.descricao) like concat('%', upper(?2), '%')
              )
            order by f.descricao
            """)
    List<FuncaoUnidade> listarPorFiltroComBusca(Boolean ativo, String q);

    @Query("""
            select f
            from FuncaoUnidade f
            where upper(f.codigo) = upper(?1)
            """)
    Optional<FuncaoUnidade> findByCodigoIgnoreCase(String codigo);
}
