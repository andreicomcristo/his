package br.com.his.assistencial.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.his.assistencial.model.CapacidadeArea;

public interface CapacidadeAreaRepository extends JpaRepository<CapacidadeArea, Long> {

    @Query("""
            select c
            from CapacidadeArea c
            where upper(c.nome) like concat('%', upper(:q), '%')
            order by c.nome
            """)
    List<CapacidadeArea> buscarPorFiltro(String q);

    List<CapacidadeArea> findAllByOrderByNomeAsc();
}
