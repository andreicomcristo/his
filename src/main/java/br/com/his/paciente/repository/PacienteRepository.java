package br.com.his.paciente.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import br.com.his.paciente.model.Paciente;

public interface PacienteRepository extends JpaRepository<Paciente, Long>, JpaSpecificationExecutor<Paciente> {

    boolean existsByCpfAndAtivoTrueAndMergedIntoIsNullAndIdNot(String cpf, Long id);

    Optional<Paciente> findFirstByCpfAndAtivoTrueAndMergedIntoIsNull(String cpf);

    Optional<Paciente> findFirstByCpfAndAtivoTrueAndMergedIntoIsNullAndTemporarioFalse(String cpf);

    Optional<Paciente> findFirstByCnsIgnoreCaseAndAtivoTrueAndMergedIntoIsNull(String cns);

    long countByTemporarioTrueAndSexo_CodigoIgnoreCase(String sexo);

    List<Paciente> findTop200ByAtivoTrueAndMergedIntoIsNullOrderByNomeAsc();
}
