package br.com.his.assistencial.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.his.assistencial.model.ClassificacaoCor;

public interface ClassificacaoCorRepository extends JpaRepository<ClassificacaoCor, Long> {

    @Query("""
            select c
            from ClassificacaoCor c
            where upper(c.descricao) like concat('%', upper(:q), '%')
            order by c.ordemExibicao, c.descricao
            """)
    List<ClassificacaoCor> buscarPorFiltro(String q);

    List<ClassificacaoCor> findAllByOrderByOrdemExibicaoAscDescricaoAsc();

    List<ClassificacaoCor> findByAtivoTrueOrderByOrdemExibicaoAscDescricaoAsc();
}
