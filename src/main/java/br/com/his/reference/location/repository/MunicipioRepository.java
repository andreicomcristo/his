package br.com.his.reference.location.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.his.reference.location.model.Municipio;

public interface MunicipioRepository extends JpaRepository<Municipio, Long> {

    Optional<Municipio> findByIdAndDtCancelamentoIsNull(Long id);
    Optional<Municipio> findByIdAndDtCancelamentoIsNotNull(Long id);

    @Query("""
            select m
            from Municipio m
            join fetch m.unidadeFederativa uf
            where m.dtCancelamento is null
              and (
                    upper(m.nome) like concat('%', upper(:q), '%')
                    or upper(uf.descricao) like concat('%', upper(:q), '%')
                    or upper(uf.sigla) like concat('%', upper(:q), '%')
              )
            order by uf.sigla, m.nome
            """)
    List<Municipio> buscarAtivosPorFiltro(String q);

    @Query("""
            select m
            from Municipio m
            join fetch m.unidadeFederativa uf
            where m.dtCancelamento is null
              and (
                    upper(m.nome) like concat('%', upper(:q), '%')
                    or upper(uf.descricao) like concat('%', upper(:q), '%')
                    or upper(uf.sigla) like concat('%', upper(:q), '%')
              )
            order by uf.sigla, m.nome
            """)
    List<Municipio> buscarPorFiltro(String q);

    @Query("""
            select m
            from Municipio m
            join fetch m.unidadeFederativa uf
            where m.dtCancelamento is null
            order by uf.sigla, m.nome
            """)
    List<Municipio> findAtivosWithUnidadeFederativaOrderByNome();

    @Query("""
            select m
            from Municipio m
            join fetch m.unidadeFederativa uf
            where m.dtCancelamento is null
            order by uf.sigla, m.nome
            """)
    List<Municipio> findAllWithUnidadeFederativaOrderByNome();

    @Query("""
            select m
            from Municipio m
            join fetch m.unidadeFederativa uf
            where m.dtCancelamento is not null
              and (
                    upper(m.nome) like concat('%', upper(:q), '%')
                    or upper(uf.descricao) like concat('%', upper(:q), '%')
                    or upper(uf.sigla) like concat('%', upper(:q), '%')
              )
            order by m.dtCancelamento desc, uf.sigla, m.nome
            """)
    List<Municipio> buscarCanceladosPorFiltro(String q);

    @Query("""
            select m
            from Municipio m
            join fetch m.unidadeFederativa uf
            where m.dtCancelamento is not null
            order by m.dtCancelamento desc, uf.sigla, m.nome
            """)
    List<Municipio> findCanceladosWithUnidadeFederativaOrderByNome();

    @Query("""
            select m
            from Municipio m
            join fetch m.unidadeFederativa uf
            where uf.id = :unidadeFederativaId
              and m.dtCancelamento is null
            order by m.nome
            """)
    List<Municipio> findByUnidadeFederativaIdOrderByNome(Long unidadeFederativaId);
}

