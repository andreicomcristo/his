package br.com.his.assistencial.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.his.assistencial.model.TipoDesfecho;

public interface TipoDesfechoRepository extends JpaRepository<TipoDesfecho, Long> {

    @Query("""
            select t
            from TipoDesfecho t
            where upper(t.descricao) like concat('%', upper(?1), '%')
            order by t.descricao
            """)
    List<TipoDesfecho> buscarPorFiltro(String q);

    List<TipoDesfecho> findAllByOrderByDescricaoAsc();

    List<TipoDesfecho> findByAtivoTrueOrderByDescricaoAsc();

    Optional<TipoDesfecho> findByDescricaoIgnoreCase(String descricao);
}
