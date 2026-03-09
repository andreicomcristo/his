package br.com.his.access.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.his.access.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByKeycloakId(String keycloakId);

    Optional<Usuario> findByUsername(String username);

    List<Usuario> findAllByOrderByUsernameAsc();

    List<Usuario> findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrderByUsernameAsc(String username,
                                                                                                   String email);
}
