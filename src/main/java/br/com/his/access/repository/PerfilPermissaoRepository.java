package br.com.his.access.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.his.access.model.PerfilPermissao;
import br.com.his.access.model.PerfilPermissaoId;

public interface PerfilPermissaoRepository extends JpaRepository<PerfilPermissao, PerfilPermissaoId> {

    @Query("""
            SELECT pp.permissao.id
            FROM PerfilPermissao pp
            WHERE pp.perfil.id = :perfilId
            """)
    List<Long> findPermissaoIdsByPerfilId(@Param("perfilId") Long perfilId);

    @Modifying
    @Query("DELETE FROM PerfilPermissao pp WHERE pp.perfil.id = :perfilId")
    void deleteByPerfilId(@Param("perfilId") Long perfilId);
}
