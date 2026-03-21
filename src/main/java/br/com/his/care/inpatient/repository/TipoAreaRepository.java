package br.com.his.care.inpatient.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.his.care.inpatient.model.TipoArea;

public interface TipoAreaRepository extends JpaRepository<TipoArea, Long> {

    Optional<TipoArea> findByCodigoIgnoreCase(String codigo);

    @Query("""
            select t
            from TipoArea t
            where upper(t.codigo) = upper(:codigo)
              and (:idIgnorar is null or t.id <> :idIgnorar)
            """)
    Optional<TipoArea> findDuplicadoCodigo(String codigo, Long idIgnorar);

    @Query("""
            select t
            from TipoArea t
            where upper(t.descricao) = upper(:descricao)
              and (:idIgnorar is null or t.id <> :idIgnorar)
            """)
    Optional<TipoArea> findDuplicadoDescricao(String descricao, Long idIgnorar);

    @Query("""
            select t
            from TipoArea t
            where (
                   upper(t.codigo) like concat('%', upper(:q), '%')
                or upper(t.descricao) like concat('%', upper(:q), '%')
            )
            order by t.ordemExibicao, t.descricao
            """)
    List<TipoArea> buscarPorFiltro(String q);

    List<TipoArea> findAllByOrderByOrdemExibicaoAscDescricaoAsc();

    List<TipoArea> findByAtivoTrueOrderByOrdemExibicaoAscDescricaoAsc();
}
