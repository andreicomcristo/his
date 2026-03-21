package br.com.his.care.attendance.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.his.care.attendance.model.TipoAtendimentoCadastro;

public interface TipoAtendimentoCadastroRepository extends JpaRepository<TipoAtendimentoCadastro, Long> {

    Optional<TipoAtendimentoCadastro> findByCodigoIgnoreCase(String codigo);

    boolean existsByCodigoIgnoreCase(String codigo);

    @Query("""
            select t
            from TipoAtendimentoCadastro t
            where upper(t.codigo) = upper(:codigo)
              and (:idIgnorar is null or t.id <> :idIgnorar)
            """)
    Optional<TipoAtendimentoCadastro> findDuplicadoCodigo(String codigo, Long idIgnorar);

    @Query("""
            select t
            from TipoAtendimentoCadastro t
            where upper(t.descricao) = upper(:descricao)
              and (:idIgnorar is null or t.id <> :idIgnorar)
            """)
    Optional<TipoAtendimentoCadastro> findDuplicadoDescricao(String descricao, Long idIgnorar);

    @Query("""
            select t
            from TipoAtendimentoCadastro t
            where (
                   upper(t.codigo) like concat('%', upper(:q), '%')
                or upper(t.descricao) like concat('%', upper(:q), '%')
            )
            order by t.ordemExibicao, t.descricao
            """)
    List<TipoAtendimentoCadastro> buscarPorFiltro(String q);

    List<TipoAtendimentoCadastro> findAllByOrderByOrdemExibicaoAscDescricaoAsc();

    List<TipoAtendimentoCadastro> findByAtivoTrueOrderByOrdemExibicaoAscDescricaoAsc();
}
