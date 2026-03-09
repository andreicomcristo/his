package br.com.his.assistencial.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.his.assistencial.model.Comorbidade;

public interface ComorbidadeRepository extends JpaRepository<Comorbidade, Long> {

    @Query("""
            select c
            from Comorbidade c
            where upper(c.descricao) like concat('%', upper(:q), '%')
            order by c.descricao
            """)
    List<Comorbidade> buscarPorFiltro(String q);

    List<Comorbidade> findAllByOrderByDescricaoAsc();

    List<Comorbidade> findByAtivoTrueOrderByDescricaoAsc();
}
