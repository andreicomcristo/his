package br.com.his.assistencial.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.his.assistencial.model.AlergiaSeveridade;

public interface AlergiaSeveridadeRepository extends JpaRepository<AlergiaSeveridade, Long> {

    @Query("""
            select a
            from AlergiaSeveridade a
            where upper(a.descricao) like concat('%', upper(:q), '%')
            order by a.descricao
            """)
    List<AlergiaSeveridade> buscarPorFiltro(String q);

    List<AlergiaSeveridade> findAllByOrderByDescricaoAsc();

    List<AlergiaSeveridade> findByAtivoTrueOrderByDescricaoAsc();
}
