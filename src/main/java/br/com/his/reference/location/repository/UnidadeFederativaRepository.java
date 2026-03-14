package br.com.his.reference.location.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.his.reference.location.model.UnidadeFederativa;

public interface UnidadeFederativaRepository extends JpaRepository<UnidadeFederativa, Long> {

    @Query("""
            select uf
            from UnidadeFederativa uf
            where upper(uf.descricao) like concat('%', upper(:q), '%')
                or upper(uf.sigla) like concat('%', upper(:q), '%')
            order by uf.descricao
            """)
    List<UnidadeFederativa> buscarPorFiltro(String q);

    List<UnidadeFederativa> findAllByOrderByDescricaoAsc();

    Optional<UnidadeFederativa> findBySiglaIgnoreCase(String sigla);
}
