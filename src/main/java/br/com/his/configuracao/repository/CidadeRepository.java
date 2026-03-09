package br.com.his.configuracao.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.his.configuracao.model.Cidade;

public interface CidadeRepository extends JpaRepository<Cidade, Long> {

    @Query("""
            select c
            from Cidade c
            join fetch c.unidadeFederativa uf
            where upper(c.nome) like concat('%', upper(:q), '%')
                or upper(uf.nome) like concat('%', upper(:q), '%')
                or upper(uf.sigla) like concat('%', upper(:q), '%')
            order by uf.sigla, c.nome
            """)
    List<Cidade> buscarPorFiltro(String q);

    @Query("""
            select c
            from Cidade c
            join fetch c.unidadeFederativa uf
            order by uf.sigla, c.nome
            """)
    List<Cidade> findAllWithUnidadeFederativaOrderByNome();

    @Query("""
            select c
            from Cidade c
            join fetch c.unidadeFederativa uf
            where uf.id = :unidadeFederativaId
            order by c.nome
            """)
    List<Cidade> findByUnidadeFederativaIdOrderByNome(Long unidadeFederativaId);
}
