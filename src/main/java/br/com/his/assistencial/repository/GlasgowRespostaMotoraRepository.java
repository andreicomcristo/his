package br.com.his.assistencial.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.his.assistencial.model.GlasgowRespostaMotora;

public interface GlasgowRespostaMotoraRepository extends JpaRepository<GlasgowRespostaMotora, Long> {

    @Query("""
            select g
            from GlasgowRespostaMotora g
            where upper(g.descricao) like concat('%', upper(:q), '%')
               or str(g.pontos) like concat('%', :q, '%')
            order by g.pontos desc
            """)
    List<GlasgowRespostaMotora> buscarPorFiltro(String q);

    List<GlasgowRespostaMotora> findAllByOrderByPontosDesc();

    List<GlasgowRespostaMotora> findByAtivoTrueOrderByPontosDesc();
}
