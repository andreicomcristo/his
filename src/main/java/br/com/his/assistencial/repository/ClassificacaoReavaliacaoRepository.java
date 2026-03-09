package br.com.his.assistencial.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.his.assistencial.model.ClassificacaoReavaliacao;

public interface ClassificacaoReavaliacaoRepository extends JpaRepository<ClassificacaoReavaliacao, Long> {

    @Query("""
            select cr
            from ClassificacaoReavaliacao cr
            join fetch cr.classificacaoRisco r
            left join fetch cr.classificacaoCor
            where r.atendimento.id in :atendimentoIds
            order by cr.dataHora desc
            """)
    List<ClassificacaoReavaliacao> findByAtendimentoIdsOrderByDataHoraDesc(@Param("atendimentoIds") List<Long> atendimentoIds);

    @Query("""
            select cr
            from ClassificacaoReavaliacao cr
            join fetch cr.classificacaoCor
            where cr.classificacaoRisco.id = :classificacaoRiscoId
            order by cr.dataHora desc
            """)
    List<ClassificacaoReavaliacao> findByClassificacaoRiscoIdOrderByDataHoraDesc(@Param("classificacaoRiscoId") Long classificacaoRiscoId);

    ClassificacaoReavaliacao findFirstByClassificacaoRiscoIdOrderByDataHoraDesc(Long classificacaoRiscoId);
}
