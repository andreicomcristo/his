package br.com.his.assistencial.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.his.assistencial.model.StatusTransferenciaExterna;

public interface StatusTransferenciaExternaRepository extends JpaRepository<StatusTransferenciaExterna, Long> {

    Optional<StatusTransferenciaExterna> findByCodigo(String codigo);
}

