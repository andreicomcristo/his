package br.com.his.assistencial.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.his.assistencial.model.ReguaDor;

public interface ReguaDorRepository extends JpaRepository<ReguaDor, Long> {

    @Query("""
            select r
            from ReguaDor r
            where upper(r.descricao) like concat('%', upper(:q), '%')
               or str(r.valor) like concat('%', :q, '%')
            order by r.valor
            """)
    List<ReguaDor> buscarPorFiltro(String q);

    List<ReguaDor> findAllByOrderByValorAsc();

    List<ReguaDor> findByAtivoTrueOrderByValorAsc();
}
