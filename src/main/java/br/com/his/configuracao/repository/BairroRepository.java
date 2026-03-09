package br.com.his.configuracao.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.his.configuracao.model.Bairro;

public interface BairroRepository extends JpaRepository<Bairro, Long> {

    @Query("""
            select b
            from Bairro b
            join fetch b.cidade c
            join fetch c.unidadeFederativa uf
            where upper(b.nome) like concat('%', upper(:q), '%')
                or upper(c.nome) like concat('%', upper(:q), '%')
                or upper(uf.nome) like concat('%', upper(:q), '%')
                or upper(uf.sigla) like concat('%', upper(:q), '%')
            order by uf.sigla, c.nome, b.nome
            """)
    List<Bairro> buscarPorFiltro(String q);

    @Query("""
            select b
            from Bairro b
            join fetch b.cidade c
            join fetch c.unidadeFederativa uf
            order by uf.sigla, c.nome, b.nome
            """)
    List<Bairro> findAllWithCidadeOrderByNome();

    @Query("""
            select b
            from Bairro b
            join fetch b.cidade c
            join fetch c.unidadeFederativa uf
            where c.id = :cidadeId
            order by b.nome
            """)
    List<Bairro> findByCidadeIdOrderByNome(Long cidadeId);

    @Query("""
            select b
            from Bairro b
            join fetch b.cidade c
            join fetch c.unidadeFederativa uf
            where c.id = :cidadeId
              and b.ativo = true
            order by b.nome
            """)
    List<Bairro> findAtivosByCidadeIdOrderByNome(Long cidadeId);
}
