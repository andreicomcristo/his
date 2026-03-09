package br.com.his.paciente.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.his.paciente.model.lookup.RacaCor;

public interface RacaCorRepository extends JpaRepository<RacaCor, Long> {

    Optional<RacaCor> findByCodigo(String codigo);
}
