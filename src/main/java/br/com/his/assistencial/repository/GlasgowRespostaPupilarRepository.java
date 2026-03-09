package br.com.his.assistencial.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.his.assistencial.model.GlasgowRespostaPupilar;

public interface GlasgowRespostaPupilarRepository extends JpaRepository<GlasgowRespostaPupilar, Long> {

    @Query("""
            select g
            from GlasgowRespostaPupilar g
            where upper(g.descricao) like concat('%', upper(:q), '%')
               or str(g.pontos) like concat('%', :q, '%')
            order by g.pontos asc
            """)
    List<GlasgowRespostaPupilar> buscarPorFiltro(String q);

    List<GlasgowRespostaPupilar> findAllByOrderByPontosAsc();

    List<GlasgowRespostaPupilar> findByAtivoTrueOrderByPontosAsc();
}
