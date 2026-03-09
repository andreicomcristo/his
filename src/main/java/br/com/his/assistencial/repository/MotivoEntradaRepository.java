package br.com.his.assistencial.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.his.assistencial.model.MotivoEntrada;

public interface MotivoEntradaRepository extends JpaRepository<MotivoEntrada, Long> {

    @Query("""
            select m
            from MotivoEntrada m
            where upper(m.descricao) like concat('%', upper(:q), '%')
            order by m.descricao
            """)
    List<MotivoEntrada> buscarPorFiltro(String q);

    List<MotivoEntrada> findAllByOrderByDescricaoAsc();

    List<MotivoEntrada> findByAtivoTrueOrderByDescricaoAsc();
}
