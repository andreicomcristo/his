package br.com.his.assistencial.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.his.assistencial.model.NaturezaOperacionalLeito;

public interface NaturezaOperacionalLeitoRepository extends JpaRepository<NaturezaOperacionalLeito, Long> {

    List<NaturezaOperacionalLeito> findAllByOrderByDescricaoAsc();

    List<NaturezaOperacionalLeito> findByAtivoTrueOrderByDescricaoAsc();

    @Query("""
            select n
            from NaturezaOperacionalLeito n
            where upper(coalesce(n.codigo, '')) like concat('%', upper(:q), '%')
               or upper(coalesce(n.descricao, '')) like concat('%', upper(:q), '%')
            order by n.descricao
            """)
    List<NaturezaOperacionalLeito> buscarPorFiltro(String q);

    boolean existsByCodigoIgnoreCase(String codigo);

    boolean existsByCodigoIgnoreCaseAndIdNot(String codigo, Long id);
}
