package br.com.his.care.attendance.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.his.care.attendance.model.TipoDestinoAssistencial;

public interface TipoDestinoAssistencialRepository extends JpaRepository<TipoDestinoAssistencial, Long> {

    Optional<TipoDestinoAssistencial> findByCodigoIgnoreCase(String codigo);

    @Query("""
            select t
            from TipoDestinoAssistencial t
            where upper(t.codigo) = upper(:codigo)
              and (:idIgnorar is null or t.id <> :idIgnorar)
            """)
    Optional<TipoDestinoAssistencial> findDuplicadoCodigo(String codigo, Long idIgnorar);

    @Query("""
            select t
            from TipoDestinoAssistencial t
            where upper(t.descricao) = upper(:descricao)
              and (:idIgnorar is null or t.id <> :idIgnorar)
            """)
    Optional<TipoDestinoAssistencial> findDuplicadoDescricao(String descricao, Long idIgnorar);

    @Query("""
            select t
            from TipoDestinoAssistencial t
            where (
                   upper(t.codigo) like concat('%', upper(:q), '%')
                or upper(t.descricao) like concat('%', upper(:q), '%')
            )
            order by t.ordemExibicao, t.descricao
            """)
    List<TipoDestinoAssistencial> buscarPorFiltro(String q);

    List<TipoDestinoAssistencial> findAllByOrderByOrdemExibicaoAscDescricaoAsc();

    List<TipoDestinoAssistencial> findByAtivoTrueOrderByOrdemExibicaoAscDescricaoAsc();
}
