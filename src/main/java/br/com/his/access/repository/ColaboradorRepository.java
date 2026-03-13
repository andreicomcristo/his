package br.com.his.access.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.his.access.model.Colaborador;

public interface ColaboradorRepository extends JpaRepository<Colaborador, Long> {

    @Query("""
            select c
            from Colaborador c
            left join fetch c.cargoColaborador cc
            order by c.nome
            """)
    List<Colaborador> findAllComCargoOrderByNomeAsc();

    @Query("""
            select c
            from Colaborador c
            left join fetch c.cargoColaborador cc
            where c.ativo = ?1
            order by c.nome
            """)
    List<Colaborador> findByAtivoComCargoOrderByNomeAsc(Boolean ativo);

    @Query("""
            select c
            from Colaborador c
            left join fetch c.cargoColaborador cc
            where upper(c.nome) like concat('%', upper(?1), '%')
               or upper(coalesce(c.cpf, '')) like concat('%', upper(?1), '%')
               or upper(coalesce(cc.codigo, '')) like concat('%', upper(?1), '%')
               or upper(coalesce(cc.descricao, '')) like concat('%', upper(?1), '%')
            order by c.nome
            """)
    List<Colaborador> listarPorBusca(String q);

    @Query("""
            select c
            from Colaborador c
            left join fetch c.cargoColaborador cc
            where c.ativo = ?1
              and (
                    upper(c.nome) like concat('%', upper(?2), '%')
                    or upper(coalesce(c.cpf, '')) like concat('%', upper(?2), '%')
                    or upper(coalesce(cc.codigo, '')) like concat('%', upper(?2), '%')
                    or upper(coalesce(cc.descricao, '')) like concat('%', upper(?2), '%')
              )
            order by c.nome
            """)
    List<Colaborador> listarPorFiltroComBusca(Boolean ativo, String q);

    @Query("""
            select c
            from Colaborador c
            where c.ativo = true
            order by c.nome
            """)
    List<Colaborador> findAtivosOrderByNomeAsc();

    @Query("""
            select c
            from Colaborador c
            where upper(c.cpf) = upper(?1)
            """)
    Optional<Colaborador> findByCpfIgnoreCase(String cpf);
}
