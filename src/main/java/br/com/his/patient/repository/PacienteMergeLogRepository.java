package br.com.his.patient.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.his.patient.model.PacienteMergeLog;

public interface PacienteMergeLogRepository extends JpaRepository<PacienteMergeLog, Long> {
}
