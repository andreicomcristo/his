package br.com.his.care.scheduling.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.his.care.scheduling.model.CargoColaboradorEspecialidade;
import br.com.his.care.scheduling.model.Especialidade;

public interface CargoColaboradorEspecialidadeRepository extends JpaRepository<CargoColaboradorEspecialidade, Long> {

    @Query("""
            select count(cce) > 0
            from CargoColaboradorEspecialidade cce
            where cce.ativo = true
              and cce.cargoColaborador.id = ?1
              and cce.especialidade.id = ?2
            """)
    boolean existsAtivaByCargoEEspecialidade(Long cargoColaboradorId, Long especialidadeId);

    @Query("""
            select count(cce) > 0
            from CargoColaboradorEspecialidade cce
            join cce.especialidade e
            where cce.cargoColaborador.id = ?1
              and upper(e.descricao) = upper(?2)
              and (?3 is null or e.id <> ?3)
            """)
    boolean existsByCargoAndDescricaoIgnoreCase(Long cargoColaboradorId, String descricao, Long especialidadeIdIgnorar);

    @Query("""
            select e
            from CargoColaboradorEspecialidade cce
            join cce.especialidade e
            join cce.cargoColaborador c
            where cce.ativo = true
              and c.ativo = true
              and e.ativo = true
              and c.id = ?1
            order by e.descricao
            """)
    List<Especialidade> listarEspecialidadesAtivasPorCargo(Long cargoColaboradorId);

    @Query("""
            select cce
            from CargoColaboradorEspecialidade cce
            join fetch cce.cargoColaborador c
            join fetch c.tipoCargo t
            where cce.especialidade.id = ?1
            order by c.descricao
            """)
    List<CargoColaboradorEspecialidade> findByEspecialidadeIdWithCargo(Long especialidadeId);

    @Query("""
            select cce.cargoColaborador.id
            from CargoColaboradorEspecialidade cce
            where cce.ativo = true
              and cce.especialidade.id = ?1
            """)
    List<Long> listarCargoIdsAtivosPorEspecialidade(Long especialidadeId);

    void deleteByEspecialidadeId(Long especialidadeId);
}
