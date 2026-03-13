package br.com.his.access.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.his.access.model.Unidade;

public interface UnidadeRepository extends JpaRepository<Unidade, Long> {

    List<Unidade> findByAtivoTrueOrderByNomeAsc();

    List<Unidade> findAllByOrderByNomeAsc();

    List<Unidade> findByNomeContainingIgnoreCaseOrSiglaContainingIgnoreCaseOrCnesContainingIgnoreCaseOrderByNomeAsc(
            String nome, String sigla, String cnes);

    Optional<Unidade> findByCnes(String cnes);
}
