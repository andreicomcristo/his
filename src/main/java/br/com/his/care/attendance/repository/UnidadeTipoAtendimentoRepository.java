package br.com.his.care.attendance.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.his.care.attendance.model.UnidadeTipoAtendimento;

public interface UnidadeTipoAtendimentoRepository extends JpaRepository<UnidadeTipoAtendimento, Long> {

    List<UnidadeTipoAtendimento> findByUnidadeIdOrderByTipoAtendimentoOrdemExibicaoAscTipoAtendimentoDescricaoAsc(Long unidadeId);

    List<UnidadeTipoAtendimento> findByUnidadeIdAndAtivoTrueOrderByTipoAtendimentoOrdemExibicaoAscTipoAtendimentoDescricaoAsc(Long unidadeId);

    Optional<UnidadeTipoAtendimento> findByUnidadeIdAndTipoAtendimentoId(Long unidadeId, Long tipoAtendimentoId);

    Optional<UnidadeTipoAtendimento> findByUnidadeIdAndTipoAtendimentoCodigoIgnoreCase(Long unidadeId, String codigo);

    boolean existsByUnidadeIdAndTipoAtendimentoCodigoIgnoreCaseAndAtivoTrue(Long unidadeId, String codigo);

    boolean existsByTipoAtendimentoId(Long tipoAtendimentoId);
}
