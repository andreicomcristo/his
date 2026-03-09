package br.com.his.assistencial.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.his.assistencial.model.Observacao;

public interface ObservacaoRepository extends JpaRepository<Observacao, Long> {

    Optional<Observacao> findByAtendimentoId(Long atendimentoId);

    Optional<Observacao> findByAtendimentoIdAndDataHoraCancelamentoIsNullAndDataHoraFimIsNull(Long atendimentoId);

    @Query("""
            select distinct o.atendimento.id
            from Observacao o
            where o.atendimento.id in :atendimentoIds
              and o.dataHoraCancelamento is null
              and o.dataHoraFim is null
            """)
    List<Long> findAtendimentoIdsComObservacaoAtiva(@Param("atendimentoIds") List<Long> atendimentoIds);

    @Query("""
            select o
            from Observacao o
            join fetch o.atendimento a
            join fetch a.paciente p
            join fetch a.unidade u
            where o.dataHoraCancelamento is null
            order by o.dataHoraInicio desc
            """)
    List<Observacao> findAllDetalhadoOrderByDataHoraInicioDesc();
}
