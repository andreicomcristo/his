package br.com.his.patient.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.his.patient.model.lookup.Sexo;

public interface SexoRepository extends JpaRepository<Sexo, Long> {

    List<Sexo> findByAtivoTrueOrderByIdAsc();

    List<Sexo> findAllByOrderByDescricaoAsc();

    Optional<Sexo> findByCodigoIgnoreCase(String codigo);

    Optional<Sexo> findByDescricaoIgnoreCase(String descricao);

    @Query("""
            select s
            from Sexo s
            where upper(s.codigo) like concat('%', upper(:q), '%')
               or upper(s.descricao) like concat('%', upper(:q), '%')
            order by s.descricao
            """)
    List<Sexo> buscarPorFiltro(String q);
}
