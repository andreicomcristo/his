package br.com.his.access.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.his.access.model.ColaboradorUnidadeAtuacao;
import br.com.his.access.model.Perfil;

public interface ColaboradorUnidadeAtuacaoRepository extends JpaRepository<ColaboradorUnidadeAtuacao, Long> {

    long countByPerfil(Perfil perfil);

    @Query("""
            select cua
            from ColaboradorUnidadeAtuacao cua
            join fetch cua.colaboradorUnidadeVinculo cuv
            join fetch cuv.colaborador c
            join fetch cuv.unidade u
            join fetch cua.funcaoUnidade fu
            join fetch cua.perfil p
            left join fetch cua.especialidade e
            where c.id = ?1
            order by u.nome, fu.descricao, p.nome, e.descricao
            """)
    List<ColaboradorUnidadeAtuacao> findByColaboradorIdComDetalhesOrderByContextoAsc(Long colaboradorId);

    @Query("""
            select cua
            from ColaboradorUnidadeAtuacao cua
            join fetch cua.colaboradorUnidadeVinculo cuv
            join fetch cuv.colaborador c
            join fetch cuv.unidade u
            join fetch cua.funcaoUnidade fu
            join fetch cua.perfil p
            left join fetch cua.especialidade e
            where cua.id = :atuacaoId
              and c.id = :colaboradorId
            """)
    Optional<ColaboradorUnidadeAtuacao> findByIdAndColaboradorIdComDetalhes(@Param("atuacaoId") Long atuacaoId,
                                                                             @Param("colaboradorId") Long colaboradorId);

    @Query("""
            select cua
            from ColaboradorUnidadeAtuacao cua
            join fetch cua.colaboradorUnidadeVinculo cuv
            join fetch cuv.colaborador c
            join fetch cuv.unidade u
            join fetch cua.funcaoUnidade fu
            join fetch cua.perfil p
            left join fetch cua.especialidade e
            order by u.nome, c.nome, fu.descricao, p.nome, e.descricao
            """)
    List<ColaboradorUnidadeAtuacao> findAllComDetalhesOrderByContextoAsc();

    @Query("""
            select cua
            from ColaboradorUnidadeAtuacao cua
            join fetch cua.colaboradorUnidadeVinculo cuv
            join fetch cuv.colaborador c
            join fetch cuv.unidade u
            join fetch cua.funcaoUnidade fu
            join fetch cua.perfil p
            left join fetch cua.especialidade e
            where cua.ativo = ?1
            order by u.nome, c.nome, fu.descricao, p.nome, e.descricao
            """)
    List<ColaboradorUnidadeAtuacao> findByAtivoComDetalhesOrderByContextoAsc(Boolean ativo);

    @Query("""
            select cua
            from ColaboradorUnidadeAtuacao cua
            join fetch cua.colaboradorUnidadeVinculo cuv
            join fetch cuv.colaborador c
            join fetch cuv.unidade u
            join fetch cua.funcaoUnidade fu
            join fetch cua.perfil p
            left join fetch cua.especialidade e
            where upper(c.nome) like concat('%', upper(?1), '%')
               or upper(u.nome) like concat('%', upper(?1), '%')
               or upper(fu.descricao) like concat('%', upper(?1), '%')
               or upper(p.nome) like concat('%', upper(?1), '%')
               or upper(coalesce(e.descricao, '')) like concat('%', upper(?1), '%')
            order by u.nome, c.nome, fu.descricao, p.nome, e.descricao
            """)
    List<ColaboradorUnidadeAtuacao> listarPorBusca(String q);

    @Query("""
            select cua
            from ColaboradorUnidadeAtuacao cua
            join fetch cua.colaboradorUnidadeVinculo cuv
            join fetch cuv.colaborador c
            join fetch cuv.unidade u
            join fetch cua.funcaoUnidade fu
            join fetch cua.perfil p
            left join fetch cua.especialidade e
            where cua.ativo = ?1
              and (
                    upper(c.nome) like concat('%', upper(?2), '%')
                    or upper(u.nome) like concat('%', upper(?2), '%')
                    or upper(fu.descricao) like concat('%', upper(?2), '%')
                    or upper(p.nome) like concat('%', upper(?2), '%')
                    or upper(coalesce(e.descricao, '')) like concat('%', upper(?2), '%')
              )
            order by u.nome, c.nome, fu.descricao, p.nome, e.descricao
            """)
    List<ColaboradorUnidadeAtuacao> listarPorFiltroComBusca(Boolean ativo, String q);

    @Query("""
            select case when count(cua) > 0 then true else false end
            from ColaboradorUnidadeAtuacao cua
            where cua.colaboradorUnidadeVinculo.id = ?1
              and cua.funcaoUnidade.id = ?2
              and cua.especialidade is null
              and cua.perfil.id = ?3
              and (?4 is null or cua.id <> ?4)
            """)
    boolean existsContextoSemEspecialidade(Long colaboradorUnidadeVinculoId,
                                           Long funcaoUnidadeId,
                                           Long perfilId,
                                           Long atuacaoIdIgnorar);

    @Query("""
            select case when count(cua) > 0 then true else false end
            from ColaboradorUnidadeAtuacao cua
            where cua.colaboradorUnidadeVinculo.id = ?1
              and cua.funcaoUnidade.id = ?2
              and cua.especialidade.id = ?3
              and cua.perfil.id = ?4
              and (?5 is null or cua.id <> ?5)
            """)
    boolean existsContextoComEspecialidade(Long colaboradorUnidadeVinculoId,
                                           Long funcaoUnidadeId,
                                           Long especialidadeId,
                                           Long perfilId,
                                           Long atuacaoIdIgnorar);

    @Query("""
            SELECT cua
            FROM ColaboradorUnidadeAtuacao cua
            JOIN FETCH cua.colaboradorUnidadeVinculo cuv
            JOIN FETCH cuv.colaborador c
            JOIN FETCH cuv.unidade u
            JOIN FETCH cua.funcaoUnidade fu
            JOIN FETCH cua.perfil p
            LEFT JOIN FETCH cua.especialidade e
            JOIN UsuarioColaborador uc ON uc.colaborador = c
            JOIN uc.usuario usr
            WHERE usr.keycloakId = :keycloakId
              AND usr.ativo = true
              AND uc.ativo = true
              AND cuv.ativo = true
              AND cua.ativo = true
            ORDER BY u.nome, fu.descricao, p.nome, e.descricao
            """)
    List<ColaboradorUnidadeAtuacao> findAtivasByUsuario(@Param("keycloakId") String keycloakId);

    @Query("""
            SELECT cua
            FROM ColaboradorUnidadeAtuacao cua
            JOIN FETCH cua.colaboradorUnidadeVinculo cuv
            JOIN FETCH cuv.colaborador c
            JOIN FETCH cuv.unidade u
            JOIN FETCH cua.funcaoUnidade fu
            JOIN FETCH cua.perfil p
            LEFT JOIN FETCH cua.especialidade e
            JOIN UsuarioColaborador uc ON uc.colaborador = c
            JOIN uc.usuario usr
            WHERE usr.keycloakId = :keycloakId
              AND usr.ativo = true
              AND uc.ativo = true
              AND cuv.unidade.id = :unidadeId
              AND cuv.ativo = true
              AND cua.ativo = true
            ORDER BY fu.descricao, p.nome, e.descricao
            """)
    List<ColaboradorUnidadeAtuacao> findAtivasByUsuarioAndUnidade(@Param("keycloakId") String keycloakId,
                                                                   @Param("unidadeId") Long unidadeId);

    @Query("""
            SELECT cua
            FROM ColaboradorUnidadeAtuacao cua
            JOIN FETCH cua.colaboradorUnidadeVinculo cuv
            JOIN FETCH cuv.colaborador c
            JOIN FETCH cuv.unidade u
            JOIN FETCH cua.funcaoUnidade fu
            JOIN FETCH cua.perfil p
            LEFT JOIN FETCH cua.especialidade e
            JOIN UsuarioColaborador uc ON uc.colaborador = c
            JOIN uc.usuario usr
            WHERE cua.id = :atuacaoId
              AND usr.keycloakId = :keycloakId
              AND cuv.unidade.id = :unidadeId
              AND usr.ativo = true
              AND uc.ativo = true
              AND cuv.ativo = true
              AND cua.ativo = true
            """)
    Optional<ColaboradorUnidadeAtuacao> findAtivaByIdAndUsuarioAndUnidade(@Param("atuacaoId") Long atuacaoId,
                                                                           @Param("keycloakId") String keycloakId,
                                                                           @Param("unidadeId") Long unidadeId);

    @Query("""
            SELECT CASE WHEN COUNT(cua) > 0 THEN true ELSE false END
            FROM ColaboradorUnidadeAtuacao cua
            JOIN cua.colaboradorUnidadeVinculo cuv
            JOIN UsuarioColaborador uc ON uc.colaborador = cuv.colaborador
            JOIN uc.usuario usr
            WHERE cua.id = :atuacaoId
              AND usr.keycloakId = :keycloakId
              AND cuv.unidade.id = :unidadeId
              AND usr.ativo = true
              AND uc.ativo = true
              AND cuv.ativo = true
              AND cua.ativo = true
            """)
    boolean existsAtivaByIdAndUsuarioAndUnidade(@Param("atuacaoId") Long atuacaoId,
                                                @Param("keycloakId") String keycloakId,
                                                @Param("unidadeId") Long unidadeId);

    @Query(value = """
            SELECT EXISTS (
                SELECT 1
                FROM colaborador_unidade_atuacao cua
                JOIN colaborador_unidade_vinculo cuv ON cuv.id = cua.colaborador_unidade_vinculo_id
                JOIN usuario_colaborador uc ON uc.colaborador_id = cuv.colaborador_id
                JOIN usuario u ON u.id = uc.usuario_id
                JOIN unidade un ON un.id = cuv.unidade_id
                JOIN perfil_permissao pp ON pp.perfil_id = cua.perfil_id
                JOIN permissao p ON p.id = pp.permissao_id
                WHERE u.keycloak_id = :keycloakId
                  AND cuv.unidade_id = :unidadeId
                  AND cua.id = :colaboradorUnidadeAtuacaoId
                  AND u.ativo = true
                  AND uc.ativo = true
                  AND cuv.ativo = true
                  AND cua.ativo = true
                  AND un.ativo = true
                  AND p.nome IN (:permissionNames)
            )
            """, nativeQuery = true)
    boolean hasAnyPermissionAtAtuacao(@Param("keycloakId") String keycloakId,
                                      @Param("unidadeId") Long unidadeId,
                                      @Param("colaboradorUnidadeAtuacaoId") Long colaboradorUnidadeAtuacaoId,
                                      @Param("permissionNames") Collection<String> permissionNames);
}
