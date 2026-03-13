package br.com.his.care.scheduling.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.his.care.scheduling.model.Especialidade;

public interface EspecialidadeRepository extends JpaRepository<Especialidade, Long> {

    List<Especialidade> findAllByOrderByDescricaoAsc();

    List<Especialidade> findByAtivoOrderByDescricaoAsc(Boolean ativo);

    List<Especialidade> findByAtivoTrueOrderByDescricaoAsc();

    @Query("""
            select e
            from Especialidade e
            where upper(e.codigo) like concat('%', upper(?1), '%')
               or upper(e.descricao) like concat('%', upper(?1), '%')
            order by e.descricao
            """)
    List<Especialidade> listarPorBusca(String q);

    @Query("""
            select e
            from Especialidade e
            where e.ativo = ?1
              and (
                    upper(e.codigo) like concat('%', upper(?2), '%')
                    or upper(e.descricao) like concat('%', upper(?2), '%')
              )
            order by e.descricao
            """)
    List<Especialidade> listarPorFiltroComBusca(Boolean ativo, String q);

    @Query("""
            select e
            from Especialidade e
            where upper(e.codigo) = upper(?1)
            """)
    Optional<Especialidade> findByCodigoIgnoreCase(String codigo);
}
