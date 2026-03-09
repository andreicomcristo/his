package br.com.his.assistencial.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.his.assistencial.model.ClassificacaoRiscoAvcSinalAlerta;

public interface ClassificacaoRiscoAvcSinalAlertaRepository extends JpaRepository<ClassificacaoRiscoAvcSinalAlerta, Long> {

    void deleteByClassificacaoRiscoId(Long classificacaoRiscoId);
}
