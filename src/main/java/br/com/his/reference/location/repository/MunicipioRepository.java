package br.com.his.reference.location.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.his.reference.location.model.Municipio;

public interface MunicipioRepository extends JpaRepository<Municipio, Long> {

    @Query("""
            select c
            from Municipio c
            join fetch c.unidadeFederativa uf
            where upper(c.nome) like concat('%', upper(:q), '%')
                or upper(uf.descricao) like concat('%', upper(:q), '%')
                or upper(uf.sigla) like concat('%', upper(:q), '%')
            order by uf.sigla, c.nome
            """)
    List<Municipio> buscarPorFiltro(String q);

    @Query("""
            select c
            from Municipio c
            join fetch c.unidadeFederativa uf
            order by uf.sigla, c.nome
            """)
    List<Municipio> findAllWithUnidadeFederativaOrderByNome();

    @Query("""
            select c
            from Municipio c
            join fetch c.unidadeFederativa uf
            where uf.id = :unidadeFederativaId
            order by c.nome
            """)
    List<Municipio> findByUnidadeFederativaIdOrderByNome(Long unidadeFederativaId);
}

