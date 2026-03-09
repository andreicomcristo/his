package br.com.his.assistencial.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.his.assistencial.model.ClassificacaoGlasgow;

public interface ClassificacaoGlasgowRepository extends JpaRepository<ClassificacaoGlasgow, Long> {

    Optional<ClassificacaoGlasgow> findByClassificacaoRiscoId(Long classificacaoRiscoId);
}
