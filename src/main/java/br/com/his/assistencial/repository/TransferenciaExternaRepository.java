package br.com.his.assistencial.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.his.assistencial.model.TransferenciaExterna;

public interface TransferenciaExternaRepository extends JpaRepository<TransferenciaExterna, Long> {

    boolean existsByAtendimentoOrigemIdAndStatusCodigoIn(Long atendimentoOrigemId, Collection<String> statusCodigos);

    List<TransferenciaExterna> findByUnidadeDestinoIdAndStatusCodigoInOrderByDataSolicitacaoAsc(Long unidadeDestinoId,
                                                                                                  Collection<String> statusCodigos);
}

