package br.com.his.patient.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.his.patient.model.lookup.Procedencia;

public interface ProcedenciaRepository extends JpaRepository<Procedencia, Long> {

    List<Procedencia> findAllByOrderByDescricaoAsc();

    List<Procedencia> findByAtivoOrderByDescricaoAsc(boolean ativo);

    @Query("""
            select p
            from Procedencia p
            left join p.tipoProcedencia tp
            left join p.unidade u
            left join p.bairro b
            left join p.municipio m
            where upper(coalesce(p.descricao, '')) like concat('%', upper(:q), '%')
               or upper(coalesce(tp.descricao, '')) like concat('%', upper(:q), '%')
               or upper(coalesce(u.nome, '')) like concat('%', upper(:q), '%')
               or upper(coalesce(b.descricao, '')) like concat('%', upper(:q), '%')
               or upper(coalesce(m.descricao, '')) like concat('%', upper(:q), '%')
            order by p.descricao, p.id
            """)
    List<Procedencia> buscarPorFiltro(@Param("q") String q);

    @Query("""
            select p
            from Procedencia p
            left join p.tipoProcedencia tp
            left join p.unidade u
            left join p.bairro b
            left join p.municipio m
            where p.ativo = :ativo
              and (
                   upper(coalesce(p.descricao, '')) like concat('%', upper(:q), '%')
                or upper(coalesce(tp.descricao, '')) like concat('%', upper(:q), '%')
                or upper(coalesce(u.nome, '')) like concat('%', upper(:q), '%')
                or upper(coalesce(b.descricao, '')) like concat('%', upper(:q), '%')
                or upper(coalesce(m.descricao, '')) like concat('%', upper(:q), '%')
              )
            order by p.descricao, p.id
            """)
    List<Procedencia> buscarPorFiltroComAtivo(@Param("q") String q, @Param("ativo") boolean ativo);

    @Query("""
            select p
            from Procedencia p
            where p.ativo = true
              and (p.unidade is null or p.unidade.id = :unidadeId)
            order by coalesce(p.descricao, ''), p.id
            """)
    List<Procedencia> findAllGlobaisEPorUnidade(Long unidadeId);

    @Query("""
            select p
            from Procedencia p
            where p.ativo = true
              and p.unidade is null
            order by coalesce(p.descricao, ''), p.id
            """)
    List<Procedencia> findAllGlobais();

    @Query("""
            select p
            from Procedencia p
            where p.ativo = true
              and p.tipoProcedencia.id = :tipoProcedenciaId
              and p.bairro.id = :bairroId
              and (p.unidade is null or p.unidade.id = :unidadeId)
            order by case when p.unidade is not null and p.unidade.id = :unidadeId then 0 else 1 end, p.id
            """)
    List<Procedencia> findAtivasByTipoEPorBairro(@Param("tipoProcedenciaId") Long tipoProcedenciaId,
                                                 @Param("bairroId") Long bairroId,
                                                 @Param("unidadeId") Long unidadeId);

    @Query("""
            select p
            from Procedencia p
            where p.ativo = true
              and p.tipoProcedencia.id = :tipoProcedenciaId
              and p.municipio.id = :municipioId
              and (p.unidade is null or p.unidade.id = :unidadeId)
            order by case when p.unidade is not null and p.unidade.id = :unidadeId then 0 else 1 end, p.id
            """)
    List<Procedencia> findAtivasByTipoEPorMunicipio(@Param("tipoProcedenciaId") Long tipoProcedenciaId,
                                                    @Param("municipioId") Long municipioId,
                                                    @Param("unidadeId") Long unidadeId);

    @Query("""
            select p
            from Procedencia p
            where p.ativo = true
              and p.tipoProcedencia.id = :tipoProcedenciaId
              and upper(coalesce(p.descricao, '')) = upper(:descricao)
              and p.bairro is null
              and p.municipio is null
              and (p.unidade is null or p.unidade.id = :unidadeId)
            order by case when p.unidade is not null and p.unidade.id = :unidadeId then 0 else 1 end, p.id
            """)
    List<Procedencia> findAtivasByTipoEDescricao(@Param("tipoProcedenciaId") Long tipoProcedenciaId,
                                                 @Param("descricao") String descricao,
                                                 @Param("unidadeId") Long unidadeId);

    @Query("""
            select count(p) > 0
            from Procedencia p
            where p.tipoProcedencia.id = :tipoProcedenciaId
              and p.bairro.id = :bairroId
              and ((:unidadeId is null and p.unidade is null) or p.unidade.id = :unidadeId)
              and (:idIgnorar is null or p.id <> :idIgnorar)
            """)
    boolean existsDuplicadaPorTipoEBairro(@Param("tipoProcedenciaId") Long tipoProcedenciaId,
                                          @Param("bairroId") Long bairroId,
                                          @Param("unidadeId") Long unidadeId,
                                          @Param("idIgnorar") Long idIgnorar);

    @Query("""
            select count(p) > 0
            from Procedencia p
            where p.tipoProcedencia.id = :tipoProcedenciaId
              and p.municipio.id = :municipioId
              and ((:unidadeId is null and p.unidade is null) or p.unidade.id = :unidadeId)
              and (:idIgnorar is null or p.id <> :idIgnorar)
            """)
    boolean existsDuplicadaPorTipoEMunicipio(@Param("tipoProcedenciaId") Long tipoProcedenciaId,
                                             @Param("municipioId") Long municipioId,
                                             @Param("unidadeId") Long unidadeId,
                                             @Param("idIgnorar") Long idIgnorar);

    @Query("""
            select count(p) > 0
            from Procedencia p
            where p.tipoProcedencia.id = :tipoProcedenciaId
              and p.bairro is null
              and p.municipio is null
              and upper(coalesce(p.descricao, '')) = upper(:descricao)
              and ((:unidadeId is null and p.unidade is null) or p.unidade.id = :unidadeId)
              and (:idIgnorar is null or p.id <> :idIgnorar)
            """)
    boolean existsDuplicadaPorTipoEDescricao(@Param("tipoProcedenciaId") Long tipoProcedenciaId,
                                             @Param("descricao") String descricao,
                                             @Param("unidadeId") Long unidadeId,
                                             @Param("idIgnorar") Long idIgnorar);

    Optional<Procedencia> findById(Long id);

    default Optional<Procedencia> findPrimeiraAtivaByTipoEPorBairro(Long tipoProcedenciaId, Long bairroId, Long unidadeId) {
        return findAtivasByTipoEPorBairro(tipoProcedenciaId, bairroId, unidadeId).stream().findFirst();
    }

    default Optional<Procedencia> findPrimeiraAtivaByTipoEPorMunicipio(Long tipoProcedenciaId, Long municipioId, Long unidadeId) {
        return findAtivasByTipoEPorMunicipio(tipoProcedenciaId, municipioId, unidadeId).stream().findFirst();
    }

    default Optional<Procedencia> findPrimeiraAtivaByTipoEDescricao(Long tipoProcedenciaId, String descricao, Long unidadeId) {
        return findAtivasByTipoEDescricao(tipoProcedenciaId, descricao, unidadeId).stream().findFirst();
    }
}
