package br.com.his.care.attendance.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.his.care.attendance.model.DestinoAssistencial;

public interface DestinoAssistencialRepository extends JpaRepository<DestinoAssistencial, Long> {

    @Query("""
            select d
            from DestinoAssistencial d
            join fetch d.unidade u
            join fetch d.tipoDestinoAssistencial t
            where (
                   upper(d.codigo) like concat('%', upper(:q), '%')
                or upper(d.descricao) like concat('%', upper(:q), '%')
                or upper(u.nome) like concat('%', upper(:q), '%')
                or upper(t.descricao) like concat('%', upper(:q), '%')
            )
            order by u.nome, d.ordemExibicao, d.descricao
            """)
    List<DestinoAssistencial> buscarPorFiltro(String q);

    @Query("""
            select d
            from DestinoAssistencial d
            join fetch d.unidade u
            join fetch d.tipoDestinoAssistencial t
            order by u.nome, d.ordemExibicao, d.descricao
            """)
    List<DestinoAssistencial> listarTodosOrdenado();

    @Query("""
            select d
            from DestinoAssistencial d
            join fetch d.unidade u
            join fetch d.tipoDestinoAssistencial t
            where d.id = :id
            """)
    Optional<DestinoAssistencial> buscarComRelacionamentos(Long id);

    Optional<DestinoAssistencial> findByUnidadeIdAndCodigoIgnoreCase(Long unidadeId, String codigo);

    @Query("""
            select d
            from DestinoAssistencial d
            where d.unidade.id = :unidadeId
              and upper(d.codigo) = upper(:codigo)
              and (:idIgnorar is null or d.id <> :idIgnorar)
            """)
    Optional<DestinoAssistencial> findDuplicadoCodigo(Long unidadeId, String codigo, Long idIgnorar);

    @Query("""
            select d
            from DestinoAssistencial d
            where d.unidade.id = :unidadeId
              and d.tipoDestinoAssistencial.id = :tipoDestinoAssistencialId
              and upper(d.descricao) = upper(:descricao)
              and (:idIgnorar is null or d.id <> :idIgnorar)
            """)
    Optional<DestinoAssistencial> findDuplicadoDescricao(Long unidadeId,
                                                         Long tipoDestinoAssistencialId,
                                                         String descricao,
                                                         Long idIgnorar);

    boolean existsByTipoDestinoAssistencialId(Long tipoDestinoAssistencialId);
}
