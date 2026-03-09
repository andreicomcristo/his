package br.com.his.assistencial.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.his.assistencial.model.AtendimentoPeriodo;
import br.com.his.assistencial.model.AtendimentoPeriodoTipo;

public interface AtendimentoPeriodoRepository extends JpaRepository<AtendimentoPeriodo, Long> {

    List<AtendimentoPeriodo> findByAtendimentoIdAndFimEmIsNullOrderByInicioEmAsc(Long atendimentoId);

    List<AtendimentoPeriodo> findByAtendimentoIdOrderByInicioEmAsc(Long atendimentoId);

    Optional<AtendimentoPeriodo> findFirstByAtendimentoIdAndTipoAndFimEmIsNull(Long atendimentoId,
                                                                                AtendimentoPeriodoTipo tipo);
}
