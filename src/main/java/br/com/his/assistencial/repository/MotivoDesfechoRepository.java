package br.com.his.assistencial.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.his.assistencial.model.MotivoDesfecho;

public interface MotivoDesfechoRepository extends JpaRepository<MotivoDesfecho, Long> {

    @Query("""
            select m
            from MotivoDesfecho m
            where upper(m.descricao) like concat('%', upper(?1), '%')
            order by m.descricao
            """)
    List<MotivoDesfecho> buscarPorFiltro(String q);

    List<MotivoDesfecho> findAllByOrderByDescricaoAsc();

    List<MotivoDesfecho> findByAtivoTrueOrderByDescricaoAsc();

    Optional<MotivoDesfecho> findByDescricaoIgnoreCase(String descricao);
}
