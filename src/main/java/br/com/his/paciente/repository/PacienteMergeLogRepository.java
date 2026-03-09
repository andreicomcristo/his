package br.com.his.paciente.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.his.paciente.model.PacienteMergeLog;

public interface PacienteMergeLogRepository extends JpaRepository<PacienteMergeLog, Long> {
}
