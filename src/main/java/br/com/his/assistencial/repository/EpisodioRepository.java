package br.com.his.assistencial.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.his.assistencial.model.Episodio;

public interface EpisodioRepository extends JpaRepository<Episodio, Long> {
}
