package br.com.his.assistencial.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.his.assistencial.model.GrauParentesco;

public interface GrauParentescoRepository extends JpaRepository<GrauParentesco, Long> {

    @Query("""
            select g
            from GrauParentesco g
            where upper(g.descricao) like concat('%', upper(:q), '%')
            order by g.descricao
            """)
    List<GrauParentesco> buscarPorFiltro(String q);

    List<GrauParentesco> findAllByOrderByDescricaoAsc();

    List<GrauParentesco> findByAtivoTrueOrderByDescricaoAsc();
}
