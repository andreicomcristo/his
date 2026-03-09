package br.com.his.assistencial.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.his.assistencial.model.AlergiaSubstancia;

public interface AlergiaSubstanciaRepository extends JpaRepository<AlergiaSubstancia, Long> {

    @Query("""
            select a
            from AlergiaSubstancia a
            where upper(a.descricao) like concat('%', upper(:q), '%')
            order by a.descricao
            """)
    List<AlergiaSubstancia> buscarPorFiltro(String q);

    List<AlergiaSubstancia> findAllByOrderByDescricaoAsc();

    List<AlergiaSubstancia> findByAtivoTrueOrderByDescricaoAsc();
}
