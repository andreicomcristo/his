package br.com.his.assistencial.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.his.assistencial.model.PerfilLeito;

public interface PerfilLeitoRepository extends JpaRepository<PerfilLeito, Long> {

    List<PerfilLeito> findByAtivoTrueOrderByDescricaoAsc();
}
