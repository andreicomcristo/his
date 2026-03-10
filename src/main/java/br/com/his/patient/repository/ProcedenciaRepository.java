package br.com.his.patient.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.his.patient.model.lookup.Procedencia;

public interface ProcedenciaRepository extends JpaRepository<Procedencia, Long> {

    @Query("""
            select p
            from Procedencia p
            where p.unidade is null or p.unidade.id = :unidadeId
            order by p.descricao
            """)
    List<Procedencia> findAllGlobaisEPorUnidade(Long unidadeId);

    @Query("""
            select p
            from Procedencia p
            where p.unidade is null
            order by p.descricao
            """)
    List<Procedencia> findAllGlobais();

    Optional<Procedencia> findById(Long id);
}
