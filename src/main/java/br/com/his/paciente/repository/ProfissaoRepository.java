package br.com.his.paciente.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.his.paciente.model.lookup.Profissao;

public interface ProfissaoRepository extends JpaRepository<Profissao, Long> {

    List<Profissao> findAllByOrderByNomeAsc();
}
