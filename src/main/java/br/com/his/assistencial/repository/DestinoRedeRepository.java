package br.com.his.assistencial.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.his.assistencial.model.DestinoRede;

public interface DestinoRedeRepository extends JpaRepository<DestinoRede, Long> {

    @Query("""
            select d
            from DestinoRede d
            where upper(d.descricao) like concat('%', upper(:q), '%')
            order by d.descricao
            """)
    List<DestinoRede> buscarPorFiltro(String q);

    List<DestinoRede> findAllByOrderByDescricaoAsc();

    List<DestinoRede> findByAtivoTrueOrderByDescricaoAsc();

    Optional<DestinoRede> findByDescricaoIgnoreCase(String descricao);
}
