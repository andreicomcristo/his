package br.com.his.access.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.his.access.model.ColaboradorUnidadeAtuacao;
import br.com.his.access.repository.ColaboradorUnidadeAtuacaoRepository;

@Service
public class ColaboradorAtuacaoService {

    private final ColaboradorUnidadeAtuacaoRepository colaboradorUnidadeAtuacaoRepository;

    public ColaboradorAtuacaoService(ColaboradorUnidadeAtuacaoRepository colaboradorUnidadeAtuacaoRepository) {
        this.colaboradorUnidadeAtuacaoRepository = colaboradorUnidadeAtuacaoRepository;
    }

    @Transactional(readOnly = true)
    public List<AtuacaoResumo> listarAtuacoesAtivasDoUsuarioNaUnidade(String keycloakId, Long unidadeId) {
        if (isBlank(keycloakId) || unidadeId == null) {
            return List.of();
        }
        return colaboradorUnidadeAtuacaoRepository.findAtivasByUsuarioAndUnidade(keycloakId, unidadeId)
                .stream()
                .map(this::toResumo)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AtuacaoResumo> listarContextosAtivosDoUsuario(String keycloakId) {
        if (isBlank(keycloakId)) {
            return List.of();
        }
        return colaboradorUnidadeAtuacaoRepository.findAtivasByUsuario(keycloakId)
                .stream()
                .map(this::toResumo)
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<AtuacaoResumo> buscarAtuacaoAtivaDoUsuarioNaUnidade(String keycloakId,
                                                                         Long unidadeId,
                                                                         Long colaboradorUnidadeAtuacaoId) {
        if (isBlank(keycloakId) || unidadeId == null || colaboradorUnidadeAtuacaoId == null) {
            return Optional.empty();
        }
        return colaboradorUnidadeAtuacaoRepository.findAtivaByIdAndUsuarioAndUnidade(
                        colaboradorUnidadeAtuacaoId, keycloakId, unidadeId)
                .map(this::toResumo);
    }

    @Transactional(readOnly = true)
    public boolean usuarioPossuiAtuacaoAtiva(String keycloakId, Long unidadeId, Long colaboradorUnidadeAtuacaoId) {
        if (isBlank(keycloakId) || unidadeId == null || colaboradorUnidadeAtuacaoId == null) {
            return false;
        }
        return colaboradorUnidadeAtuacaoRepository.existsAtivaByIdAndUsuarioAndUnidade(
                colaboradorUnidadeAtuacaoId, keycloakId, unidadeId);
    }

    private AtuacaoResumo toResumo(ColaboradorUnidadeAtuacao entity) {
        String especialidadeDescricao = entity.getEspecialidade() != null
                ? entity.getEspecialidade().getDescricao()
                : null;
        return new AtuacaoResumo(
                entity.getId(),
                entity.getColaboradorUnidadeVinculo().getUnidade().getId(),
                entity.getColaboradorUnidadeVinculo().getUnidade().getNome(),
                entity.getColaboradorUnidadeVinculo().getUnidade().getTipoEstabelecimento(),
                entity.getColaboradorUnidadeVinculo().getUnidade().getCnes(),
                entity.getColaboradorUnidadeVinculo().getColaborador().getNome(),
                entity.getFuncaoUnidade().getDescricao(),
                entity.getPerfil().getNome(),
                especialidadeDescricao);
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    public record AtuacaoResumo(Long id,
                                Long unidadeId,
                                String unidadeNome,
                                String unidadeTipoEstabelecimento,
                                String unidadeCnes,
                                String colaboradorNome,
                                String funcaoDescricao,
                                String perfilNome,
                                String especialidadeDescricao) {

        public String descricaoCompleta() {
            if (especialidadeDescricao == null || especialidadeDescricao.isBlank()) {
                return funcaoDescricao;
            }
            return funcaoDescricao + " / " + especialidadeDescricao;
        }
    }
}
