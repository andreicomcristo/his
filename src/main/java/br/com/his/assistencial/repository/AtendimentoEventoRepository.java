package br.com.his.assistencial.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.his.assistencial.model.AtendimentoEvento;

public interface AtendimentoEventoRepository extends JpaRepository<AtendimentoEvento, Long> {

    List<AtendimentoEvento> findByAtendimentoIdOrderByDataHoraAsc(Long atendimentoId);
}
