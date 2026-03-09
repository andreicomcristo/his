package br.com.his.configuracao.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.his.configuracao.model.UnidadeFederativa;

public interface UnidadeFederativaRepository extends JpaRepository<UnidadeFederativa, Long> {

    @Query("""
            select uf
            from UnidadeFederativa uf
            where upper(uf.nome) like concat('%', upper(:q), '%')
                or upper(uf.sigla) like concat('%', upper(:q), '%')
            order by uf.nome
            """)
    List<UnidadeFederativa> buscarPorFiltro(String q);

    List<UnidadeFederativa> findAllByOrderByNomeAsc();

    Optional<UnidadeFederativa> findBySiglaIgnoreCase(String sigla);
}
