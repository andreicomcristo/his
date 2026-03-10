package br.com.his.patient.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.his.patient.model.lookup.Profissao;

public interface ProfissaoRepository extends JpaRepository<Profissao, Long> {

    List<Profissao> findAllByOrderByNomeAsc();
}
