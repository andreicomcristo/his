package br.com.his.assistencial.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.his.assistencial.model.LeitoModalidadeTipo;

public interface LeitoModalidadeTipoRepository extends JpaRepository<LeitoModalidadeTipo, Long> {

    List<LeitoModalidadeTipo> findByAtivoTrueOrderByDescricaoAsc();
}
