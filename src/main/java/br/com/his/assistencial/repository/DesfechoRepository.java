package br.com.his.assistencial.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.his.assistencial.model.Desfecho;

public interface DesfechoRepository extends JpaRepository<Desfecho, Long> {

    List<Desfecho> findAllByOrderByDataHoraDesc();

    Optional<Desfecho> findByAtendimentoId(Long atendimentoId);

    List<Desfecho> findByAtendimentoIdIn(List<Long> atendimentoIds);
}
