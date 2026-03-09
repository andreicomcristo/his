package br.com.his.assistencial.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.his.assistencial.model.LeitoOcupacaoTipo;

public interface LeitoOcupacaoTipoRepository extends JpaRepository<LeitoOcupacaoTipo, Long> {

    List<LeitoOcupacaoTipo> findByAtivoTrueOrderByDescricaoAsc();

    Optional<LeitoOcupacaoTipo> findByCodigoIgnoreCase(String codigo);
}
