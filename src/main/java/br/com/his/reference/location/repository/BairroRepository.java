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
            join fetch b.municipio c
            join fetch c.unidadeFederativa uf
            where b.dtCancelamento is null
              and (
                    upper(b.descricao) like concat('%', upper(:q), '%')
                 or upper(c.descricao) like concat('%', upper(:q), '%')
                 or upper(uf.descricao) like concat('%', upper(:q), '%')
                 or upper(uf.sigla) like concat('%', upper(:q), '%')
              )
            order by uf.sigla, c.descricao, b.descricao
            """)
    List<Bairro> buscarPorFiltro(String q);

    @Query("""
            select b
            from Bairro b
            join fetch b.municipio c
            join fetch c.unidadeFederativa uf
            where b.dtCancelamento is null
            order by uf.sigla, c.descricao, b.descricao
            """)
    List<Bairro> findAllWithMunicipioOrderByDescricao();

    @Query("""
            select b
            from Bairro b
            join fetch b.municipio c
            join fetch c.unidadeFederativa uf
            where b.dtCancelamento is not null
              and (
                    upper(b.descricao) like concat('%', upper(:q), '%')
                 or upper(c.descricao) like concat('%', upper(:q), '%')
                 or upper(uf.descricao) like concat('%', upper(:q), '%')
                 or upper(uf.sigla) like concat('%', upper(:q), '%')
              )
            order by b.dtCancelamento desc, uf.sigla, c.descricao, b.descricao
            """)
    List<Bairro> buscarCanceladosPorFiltro(String q);

    @Query("""
            select b
            from Bairro b
            join fetch b.municipio c
            join fetch c.unidadeFederativa uf
            where b.dtCancelamento is not null
            order by b.dtCancelamento desc, uf.sigla, c.descricao, b.descricao
            """)
    List<Bairro> findAllCanceladosWithMunicipioOrderByDescricao();

    @Query("""
            select b
            from Bairro b
            join fetch b.municipio c
            join fetch c.unidadeFederativa uf
            where c.id = :municipioId
              and b.dtCancelamento is null
            order by b.descricao
            """)
    List<Bairro> findByMunicipioIdOrderByDescricao(Long municipioId);

    @Query("""
            select b
            from Bairro b
            join fetch b.municipio c
            join fetch c.unidadeFederativa uf
            where c.id = :municipioId
              and b.dtCancelamento is null
            order by b.descricao
            """)
    List<Bairro> findAtivosByMunicipioIdOrderByDescricao(Long municipioId);
}
