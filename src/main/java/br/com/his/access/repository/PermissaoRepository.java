package br.com.his.access.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.his.access.model.Permissao;

public interface PermissaoRepository extends JpaRepository<Permissao, Long> {

    List<Permissao> findAllByOrderByNomeAsc();

    List<Permissao> findByIdIn(Collection<Long> ids);
}
