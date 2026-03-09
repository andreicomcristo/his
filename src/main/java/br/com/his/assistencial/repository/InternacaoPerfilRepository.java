package br.com.his.assistencial.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.his.assistencial.model.InternacaoPerfil;

public interface InternacaoPerfilRepository extends JpaRepository<InternacaoPerfil, Long> {

    List<InternacaoPerfil> findByAtivoTrueOrderByDescricaoAsc();
}
