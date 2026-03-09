package br.com.his.assistencial.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.his.assistencial.model.Entrada;

public interface EntradaRepository extends JpaRepository<Entrada, Long> {

    Optional<Entrada> findByAtendimentoId(Long atendimentoId);
}
