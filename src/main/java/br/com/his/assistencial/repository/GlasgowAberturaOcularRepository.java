package br.com.his.assistencial.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.his.assistencial.model.GlasgowAberturaOcular;

public interface GlasgowAberturaOcularRepository extends JpaRepository<GlasgowAberturaOcular, Long> {

    @Query("""
            select g
            from GlasgowAberturaOcular g
            where upper(g.descricao) like concat('%', upper(:q), '%')
               or str(g.pontos) like concat('%', :q, '%')
            order by g.pontos desc
            """)
    List<GlasgowAberturaOcular> buscarPorFiltro(String q);

    List<GlasgowAberturaOcular> findAllByOrderByPontosDesc();

    List<GlasgowAberturaOcular> findByAtivoTrueOrderByPontosDesc();
}
