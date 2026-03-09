package br.com.his.assistencial.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.his.assistencial.model.ClassificacaoRiscoAlergia;

public interface ClassificacaoRiscoAlergiaRepository extends JpaRepository<ClassificacaoRiscoAlergia, Long> {

    void deleteByClassificacaoRiscoId(Long classificacaoRiscoId);
}
