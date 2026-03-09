package br.com.his.assistencial.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.his.assistencial.model.ClassificacaoRiscoComorbidade;

public interface ClassificacaoRiscoComorbidadeRepository extends JpaRepository<ClassificacaoRiscoComorbidade, Long> {

    void deleteByClassificacaoRiscoId(Long classificacaoRiscoId);
}
