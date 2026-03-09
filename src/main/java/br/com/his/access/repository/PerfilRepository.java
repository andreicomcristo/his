package br.com.his.access.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.his.access.model.Perfil;

public interface PerfilRepository extends JpaRepository<Perfil, Long> {

    Optional<Perfil> findByNome(String nome);

    List<Perfil> findAllByOrderByNomeAsc();

    List<Perfil> findByNomeContainingIgnoreCaseOrderByNomeAsc(String nome);
}
