package br.com.his.assistencial.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.his.assistencial.model.SituacaoOcupacional;

public interface SituacaoOcupacionalRepository extends JpaRepository<SituacaoOcupacional, Long> {

    List<SituacaoOcupacional> findAllByOrderByDescricaoAsc();

    List<SituacaoOcupacional> findByAtivoTrueOrderByDescricaoAsc();
}
