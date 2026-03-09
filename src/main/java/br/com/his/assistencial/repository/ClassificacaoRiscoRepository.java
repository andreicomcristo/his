package br.com.his.assistencial.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.his.assistencial.model.ClassificacaoRisco;

public interface ClassificacaoRiscoRepository extends JpaRepository<ClassificacaoRisco, Long> {

    Optional<ClassificacaoRisco> findFirstByAtendimentoIdAndDataFimIsNull(Long atendimentoId);

    Optional<ClassificacaoRisco> findTopByAtendimentoIdAndDataFimIsNotNullOrderByDataFimDesc(Long atendimentoId);

    Optional<ClassificacaoRisco> findTopByAtendimentoIdOrderByDataInicioDesc(Long atendimentoId);

    List<ClassificacaoRisco> findByAtendimentoIdInOrderByDataInicioDesc(List<Long> atendimentoIds);
}
