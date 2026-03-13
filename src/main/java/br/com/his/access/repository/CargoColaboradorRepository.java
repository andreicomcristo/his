package br.com.his.access.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.his.access.model.CargoColaborador;

public interface CargoColaboradorRepository extends JpaRepository<CargoColaborador, Long> {

    @Query("""
            select c
            from CargoColaborador c
            join fetch c.tipoCargo t
            order by c.descricao
            """)
    List<CargoColaborador> findAllByOrderByDescricaoAsc();

    @Query("""
            select c
            from CargoColaborador c
            join fetch c.tipoCargo t
            where c.ativo = ?1
            order by c.descricao
            """)
    List<CargoColaborador> findByAtivoOrderByDescricaoAsc(Boolean ativo);

    @Query("""
            select c
            from CargoColaborador c
            join fetch c.tipoCargo t
            where upper(c.codigo) like concat('%', upper(?1), '%')
               or upper(c.descricao) like concat('%', upper(?1), '%')
            order by c.descricao
            """)
    List<CargoColaborador> listarPorBusca(String q);

    @Query("""
            select c
            from CargoColaborador c
            join fetch c.tipoCargo t
            where c.ativo = ?1
              and (
                    upper(c.codigo) like concat('%', upper(?2), '%')
                    or upper(c.descricao) like concat('%', upper(?2), '%')
              )
            order by c.descricao
            """)
    List<CargoColaborador> listarPorFiltroComBusca(Boolean ativo, String q);

    @Query("""
            select c
            from CargoColaborador c
            join fetch c.tipoCargo t
            where c.ativo = true
              and t.ativo = true
              and upper(t.codigo) = 'ASSISTENCIAL'
            order by c.descricao
            """)
    List<CargoColaborador> findAssistenciaisAtivosOrderByDescricaoAsc();

    @Query("""
            select c
            from CargoColaborador c
            where upper(c.codigo) = upper(?1)
            """)
    Optional<CargoColaborador> findByCodigoIgnoreCase(String codigo);
}
