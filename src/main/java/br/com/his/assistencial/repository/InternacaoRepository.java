package br.com.his.assistencial.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.his.assistencial.model.Internacao;

public interface InternacaoRepository extends JpaRepository<Internacao, Long> {

    Optional<Internacao> findByAtendimentoId(Long atendimentoId);

    Optional<Internacao> findByAtendimentoIdAndDataHoraCancelamentoIsNullAndDataHoraFimInternacaoIsNull(Long atendimentoId);

    @Query("""
            select i
            from Internacao i
            join fetch i.atendimento a
            join fetch a.paciente p
            join fetch a.unidade u
            left join fetch i.origemDemanda od
            left join fetch i.perfilInternacao pi
            where i.dataHoraCancelamento is null
            order by i.dataHoraDecisaoInternacao desc
            """)
    List<Internacao> findAllDetalhadoOrderByDataHoraDecisaoDesc();

    @Query("""
            select distinct i.atendimento.id
            from Internacao i
            where i.atendimento.id in :atendimentoIds
              and i.dataHoraCancelamento is null
              and i.dataHoraFimInternacao is null
            """)
    List<Long> findAtendimentoIdsComInternacao(@Param("atendimentoIds") List<Long> atendimentoIds);
}
