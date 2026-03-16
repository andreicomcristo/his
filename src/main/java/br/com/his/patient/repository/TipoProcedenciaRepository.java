package br.com.his.patient.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.his.patient.model.lookup.TipoProcedencia;

public interface TipoProcedenciaRepository extends JpaRepository<TipoProcedencia, Long> {

    List<TipoProcedencia> findAllByOrderByDescricaoAsc();

    Optional<TipoProcedencia> findByDescricaoIgnoreCase(String descricao);

    @Query("""
            select t
            from TipoProcedencia t
            where upper(coalesce(t.descricao, '')) like concat('%', upper(:q), '%')
            order by t.descricao, t.id
            """)
    List<TipoProcedencia> buscarPorFiltro(@Param("q") String q);
}
