package br.com.his.reference.location.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.his.reference.location.model.Bairro;

public interface BairroRepository extends JpaRepository<Bairro, Long> {

    Optional<Bairro> findByIdAndDtCancelamentoIsNull(Long id);
    Optional<Bairro> findByIdAndDtCancelamentoIsNotNull(Long id);

    @Query("""
            select b
            from Bairro b
            join fetch b.Municipio c
            join fetch c.unidadeFederativa uf
            where b.dtCancelamento is null
              and (
                    upper(b.nome) like concat('%', upper(:q), '%')
                 or upper(c.nome) like concat('%', upper(:q), '%')
                 or upper(uf.descricao) like concat('%', upper(:q), '%')
                 or upper(uf.sigla) like concat('%', upper(:q), '%')
              )
            order by uf.sigla, c.nome, b.nome
            """)
    List<Bairro> buscarPorFiltro(String q);

    @Query("""
            select b
            from Bairro b
            join fetch b.Municipio c
            join fetch c.unidadeFederativa uf
            where b.dtCancelamento is null
            order by uf.sigla, c.nome, b.nome
            """)
    List<Bairro> findAllWithMunicipioOrderByNome();

    @Query("""
            select b
            from Bairro b
            join fetch b.Municipio c
            join fetch c.unidadeFederativa uf
            where b.dtCancelamento is not null
              and (
                    upper(b.nome) like concat('%', upper(:q), '%')
                 or upper(c.nome) like concat('%', upper(:q), '%')
                 or upper(uf.descricao) like concat('%', upper(:q), '%')
                 or upper(uf.sigla) like concat('%', upper(:q), '%')
              )
            order by b.dtCancelamento desc, uf.sigla, c.nome, b.nome
            """)
    List<Bairro> buscarCanceladosPorFiltro(String q);

    @Query("""
            select b
            from Bairro b
            join fetch b.Municipio c
            join fetch c.unidadeFederativa uf
            where b.dtCancelamento is not null
            order by b.dtCancelamento desc, uf.sigla, c.nome, b.nome
            """)
    List<Bairro> findAllCanceladosWithMunicipioOrderByNome();

    @Query("""
            select b
            from Bairro b
            join fetch b.Municipio c
            join fetch c.unidadeFederativa uf
            where c.id = :municipioId
              and b.dtCancelamento is null
            order by b.nome
            """)
    List<Bairro> findByMunicipioIdOrderByNome(Long municipioId);

    @Query("""
            select b
            from Bairro b
            join fetch b.Municipio c
            join fetch c.unidadeFederativa uf
            where c.id = :municipioId
              and b.dtCancelamento is null
            order by b.nome
            """)
    List<Bairro> findAtivosByMunicipioIdOrderByNome(Long municipioId);
}
