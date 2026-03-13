package br.com.his.access.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.his.access.model.TipoCargo;

public interface TipoCargoRepository extends JpaRepository<TipoCargo, Long> {

    List<TipoCargo> findAllByOrderByDescricaoAsc();

    List<TipoCargo> findByAtivoOrderByDescricaoAsc(Boolean ativo);

    @Query("""
            select t
            from TipoCargo t
            where upper(t.codigo) like concat('%', upper(?1), '%')
               or upper(t.descricao) like concat('%', upper(?1), '%')
            order by t.descricao
            """)
    List<TipoCargo> listarPorBusca(String q);

    @Query("""
            select t
            from TipoCargo t
            where t.ativo = ?1
              and (
                    upper(t.codigo) like concat('%', upper(?2), '%')
                    or upper(t.descricao) like concat('%', upper(?2), '%')
              )
            order by t.descricao
            """)
    List<TipoCargo> listarPorFiltroComBusca(Boolean ativo, String q);

    @Query("""
            select t
            from TipoCargo t
            where upper(t.codigo) = upper(?1)
            """)
    Optional<TipoCargo> findByCodigoIgnoreCase(String codigo);
}
