package br.com.his.assistencial.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.his.assistencial.model.FormaChegada;

public interface FormaChegadaRepository extends JpaRepository<FormaChegada, Long> {

    @Query("""
            select f
            from FormaChegada f
            where upper(f.descricao) like concat('%', upper(:q), '%')
            order by f.descricao
            """)
    List<FormaChegada> buscarPorFiltro(String q);

    List<FormaChegada> findAllByOrderByDescricaoAsc();

    List<FormaChegada> findByAtivoTrueOrderByDescricaoAsc();

    Optional<FormaChegada> findByDescricaoIgnoreCase(String descricao);
}
