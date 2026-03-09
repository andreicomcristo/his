package br.com.his.access.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.his.access.model.Perfil;
import br.com.his.access.model.Unidade;
import br.com.his.access.model.UsuarioUnidadePerfil;

public interface UsuarioUnidadePerfilRepository extends JpaRepository<UsuarioUnidadePerfil, Long> {

    @Query("""
            SELECT DISTINCT uup.unidade
            FROM UsuarioUnidadePerfil uup
            WHERE uup.usuario.keycloakId = :keycloakId
              AND uup.ativo = true
              AND uup.unidade.ativo = true
            ORDER BY uup.unidade.nome
            """)
    List<Unidade> findUnidadesAtivasByKeycloakId(@Param("keycloakId") String keycloakId);

    @Query("""
            SELECT CASE WHEN COUNT(uup) > 0 THEN true ELSE false END
            FROM UsuarioUnidadePerfil uup
            WHERE uup.usuario.keycloakId = :keycloakId
              AND uup.unidade.id = :unidadeId
              AND uup.ativo = true
              AND uup.unidade.ativo = true
            """)
    boolean existsVinculoAtivo(@Param("keycloakId") String keycloakId,
                               @Param("unidadeId") Long unidadeId);

    @Query(value = """
            SELECT EXISTS (
                SELECT 1
                FROM usuario_unidade_perfil uup
                JOIN usuario u ON u.id = uup.usuario_id
                JOIN unidade un ON un.id = uup.unidade_id
                JOIN perfil_permissao pp ON pp.perfil_id = uup.perfil_id
                JOIN permissao p ON p.id = pp.permissao_id
                WHERE u.keycloak_id = :keycloakId
                  AND uup.unidade_id = :unidadeId
                  AND uup.ativo = true
                  AND un.ativo = true
                  AND p.nome IN (:permissionNames)
            )
            """, nativeQuery = true)
    boolean hasAnyPermissionAtUnidade(@Param("keycloakId") String keycloakId,
                                      @Param("unidadeId") Long unidadeId,
                                      @Param("permissionNames") Collection<String> permissionNames);

    @Query("""
            SELECT uup
            FROM UsuarioUnidadePerfil uup
            JOIN FETCH uup.unidade u
            JOIN FETCH uup.perfil p
            WHERE uup.usuario.id = :usuarioId
            ORDER BY u.nome, p.nome
            """)
    List<UsuarioUnidadePerfil> findByUsuarioIdComDetalhes(@Param("usuarioId") Long usuarioId);

    @Query("""
            SELECT CASE WHEN COUNT(uup) > 0 THEN true ELSE false END
            FROM UsuarioUnidadePerfil uup
            WHERE uup.usuario.id = :usuarioId
              AND uup.unidade.id = :unidadeId
              AND uup.perfil.id = :perfilId
              AND (:ignoreId IS NULL OR uup.id <> :ignoreId)
            """)
    boolean existsByTripla(@Param("usuarioId") Long usuarioId,
                           @Param("unidadeId") Long unidadeId,
                           @Param("perfilId") Long perfilId,
                           @Param("ignoreId") Long ignoreId);

    Optional<UsuarioUnidadePerfil> findByUsuarioIdAndUnidadeIdAndPerfilId(Long usuarioId, Long unidadeId, Long perfilId);

    long countByPerfil(Perfil perfil);
}
