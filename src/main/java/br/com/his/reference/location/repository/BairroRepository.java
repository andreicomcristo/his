package br.com.his.reference.location.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.his.reference.location.model.Bairro;

public interface BairroRepository extends JpaRepository<Bairro, Long> {

    @Query("""
            select b
            from Bairro b
            join fetch b.Municipio c
            join fetch c.unidadeFederativa uf
            where upper(b.nome) like concat('%', upper(:q), '%')
                or upper(c.nome) like concat('%', upper(:q), '%')
                or upper(uf.descricao) like concat('%', upper(:q), '%')
                or upper(uf.sigla) like concat('%', upper(:q), '%')
            order by uf.sigla, c.nome, b.nome
            """)
    List<Bairro> buscarPorFiltro(String q);

    @Query("""
            select b
            from Bairro b
            join fetch b.Municipio c
            join fetch c.unidadeFederativa uf
            order by uf.sigla, c.nome, b.nome
            """)
    List<Bairro> findAllWithMunicipioOrderByNome();

    @Query("""
            select b
            from Bairro b
            join fetch b.Municipio c
            join fetch c.unidadeFederativa uf
            where c.id = :municipioId
            order by b.nome
            """)
    List<Bairro> findByMunicipioIdOrderByNome(Long municipioId);

    @Query("""
            select b
            from Bairro b
            join fetch b.Municipio c
            join fetch c.unidadeFederativa uf
            where c.id = :municipioId
              and b.ativo = true
            order by b.nome
            """)
    List<Bairro> findAtivosByMunicipioIdOrderByNome(Long municipioId);
}


