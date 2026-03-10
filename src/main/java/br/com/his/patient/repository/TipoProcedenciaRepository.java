package br.com.his.patient.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.his.patient.model.lookup.TipoProcedencia;

public interface TipoProcedenciaRepository extends JpaRepository<TipoProcedencia, Long> {
}
