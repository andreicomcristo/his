package br.com.his.assistencial.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.his.assistencial.model.TipoAtendimento;
import br.com.his.assistencial.model.UnidadeRegraTriagem;

public interface UnidadeRegraTriagemRepository extends JpaRepository<UnidadeRegraTriagem, Long> {

    Optional<UnidadeRegraTriagem> findByUnidadeIdAndTipoAtendimento(Long unidadeId, TipoAtendimento tipoAtendimento);

    List<UnidadeRegraTriagem> findByUnidadeId(Long unidadeId);
}
