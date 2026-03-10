package br.com.his.patient.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.his.patient.model.lookup.RacaCor;

public interface RacaCorRepository extends JpaRepository<RacaCor, Long> {

    Optional<RacaCor> findByCodigo(String codigo);
}
