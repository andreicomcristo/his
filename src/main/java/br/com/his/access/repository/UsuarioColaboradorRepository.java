package br.com.his.access.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.his.access.model.UsuarioColaborador;

public interface UsuarioColaboradorRepository extends JpaRepository<UsuarioColaborador, Long> {

    @Query("""
            select uc
            from UsuarioColaborador uc
            join fetch uc.colaborador c
            where uc.usuario.id = ?1
            """)
    Optional<UsuarioColaborador> findByUsuarioIdComColaborador(Long usuarioId);

    Optional<UsuarioColaborador> findByUsuarioId(Long usuarioId);

    @Query("""
            select uc
            from UsuarioColaborador uc
            join fetch uc.usuario u
            where uc.colaborador.id = ?1
            """)
    Optional<UsuarioColaborador> findByColaboradorIdComUsuario(Long colaboradorId);

    @Query("""
            select uc
            from UsuarioColaborador uc
            join fetch uc.usuario u
            where uc.colaborador.id in ?1
            """)
    List<UsuarioColaborador> findByColaboradorIdsComUsuario(Collection<Long> colaboradorIds);
}
