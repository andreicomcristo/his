package br.com.his.assistencial.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.his.assistencial.model.StatusAtendimento;

public interface StatusAtendimentoRepository extends JpaRepository<StatusAtendimento, Long> {

    @Query("""
            select s
            from StatusAtendimento s
            where upper(s.codigo) like concat('%', upper(?1), '%')
               or upper(s.descricao) like concat('%', upper(?1), '%')
            order by s.descricao
            """)
    List<StatusAtendimento> buscarPorFiltro(String q);

    List<StatusAtendimento> findAllByOrderByDescricaoAsc();

    List<StatusAtendimento> findByAtivoTrueOrderByDescricaoAsc();

    Optional<StatusAtendimento> findByCodigoIgnoreCase(String codigo);
}
