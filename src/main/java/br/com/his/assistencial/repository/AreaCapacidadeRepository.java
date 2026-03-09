package br.com.his.assistencial.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.his.assistencial.model.AreaCapacidade;

public interface AreaCapacidadeRepository extends JpaRepository<AreaCapacidade, Long> {

    List<AreaCapacidade> findByAreaIdOrderByCapacidadeAreaNomeAsc(Long areaId);

    boolean existsByAreaIdAndCapacidadeAreaNomeIgnoreCase(Long areaId, String nome);

    void deleteByAreaId(Long areaId);
}
