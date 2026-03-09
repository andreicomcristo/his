package br.com.his.assistencial.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.his.assistencial.model.Leito;
import br.com.his.assistencial.model.LeitoModalidade;

public interface LeitoModalidadeRepository extends JpaRepository<LeitoModalidade, Long> {

    @Query("""
            select lm
            from LeitoModalidade lm
            join fetch lm.modalidadeTipo mt
            where lm.leito.id = :leitoId
            order by mt.descricao
            """)
    List<LeitoModalidade> findByLeitoIdWithTipoOrderByDescricaoAsc(Long leitoId);

    @Query("""
            select lm
            from LeitoModalidade lm
            join fetch lm.modalidadeTipo mt
            where lm.leito.id in :leitoIds
            order by lm.leito.id, mt.descricao
            """)
    List<LeitoModalidade> findByLeitoIdInWithTipoOrderByLeitoIdAscDescricaoAsc(List<Long> leitoIds);

    void deleteByLeitoId(Long leitoId);

    @Query("""
            select count(lm) > 0
            from LeitoModalidade lm
            where lm.leito.id = :leitoId
              and upper(lm.modalidadeTipo.codigo) = upper(:codigo)
              and lm.modalidadeTipo.ativo = true
            """)
    boolean existsByLeitoIdAndModalidadeCodigo(Long leitoId, String codigo);

    @Query("""
            select distinct l
            from LeitoModalidade lm
            join lm.leito l
            join fetch l.area a
            join fetch l.unidade u
            where l.unidade.id = :unidadeId
              and l.ativo = true
              and lm.modalidadeTipo.ativo = true
              and upper(lm.modalidadeTipo.codigo) = upper(:modalidadeCodigo)
            order by a.nome, l.codigo
            """)
    List<Leito> findLeitosAtivosPorUnidadeEModalidade(Long unidadeId, String modalidadeCodigo);
}
