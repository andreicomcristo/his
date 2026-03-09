package br.com.his.assistencial.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.his.assistencial.model.GlasgowRespostaVerbal;

public interface GlasgowRespostaVerbalRepository extends JpaRepository<GlasgowRespostaVerbal, Long> {

    @Query("""
            select g
            from GlasgowRespostaVerbal g
            where upper(g.descricao) like concat('%', upper(:q), '%')
               or str(g.pontos) like concat('%', :q, '%')
            order by g.pontos desc
            """)
    List<GlasgowRespostaVerbal> buscarPorFiltro(String q);

    List<GlasgowRespostaVerbal> findAllByOrderByPontosDesc();

    List<GlasgowRespostaVerbal> findByAtivoTrueOrderByPontosDesc();
}
