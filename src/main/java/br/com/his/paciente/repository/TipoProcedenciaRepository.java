package br.com.his.paciente.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.his.paciente.model.lookup.TipoProcedencia;

public interface TipoProcedenciaRepository extends JpaRepository<TipoProcedencia, Long> {
}
