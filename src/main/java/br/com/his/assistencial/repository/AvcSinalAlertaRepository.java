package br.com.his.assistencial.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.his.assistencial.model.AvcSinalAlerta;

public interface AvcSinalAlertaRepository extends JpaRepository<AvcSinalAlerta, Long> {

    @Query("""
            select a
            from AvcSinalAlerta a
            where upper(a.codigo) like concat('%', upper(:q), '%')
               or upper(a.descricao) like concat('%', upper(:q), '%')
            order by a.ordemExibicao, a.descricao
            """)
    List<AvcSinalAlerta> buscarPorFiltro(String q);

    List<AvcSinalAlerta> findAllByOrderByIdAsc();

    List<AvcSinalAlerta> findByAtivoTrueOrderByIdAsc();

    List<AvcSinalAlerta> findAllByOrderByOrdemExibicaoAscDescricaoAsc();

    List<AvcSinalAlerta> findByAtivoTrueOrderByOrdemExibicaoAscDescricaoAsc();

    boolean existsByCodigoIgnoreCase(String codigo);

    boolean existsByCodigoIgnoreCaseAndIdNot(String codigo, Long id);
}
