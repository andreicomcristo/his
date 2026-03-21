package br.com.his.care.attendance.service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.his.care.attendance.dto.TipoAtendimentoOption;
import br.com.his.care.attendance.model.TipoAtendimentoCadastro;
import br.com.his.care.attendance.model.UnidadeTipoAtendimento;
import br.com.his.care.attendance.repository.TipoAtendimentoCadastroRepository;
import br.com.his.care.attendance.repository.UnidadeTipoAtendimentoRepository;

@Service
public class TipoAtendimentoService {

    private final TipoAtendimentoCadastroRepository tipoAtendimentoCadastroRepository;
    private final UnidadeTipoAtendimentoRepository unidadeTipoAtendimentoRepository;

    public TipoAtendimentoService(TipoAtendimentoCadastroRepository tipoAtendimentoCadastroRepository,
                                  UnidadeTipoAtendimentoRepository unidadeTipoAtendimentoRepository) {
        this.tipoAtendimentoCadastroRepository = tipoAtendimentoCadastroRepository;
        this.unidadeTipoAtendimentoRepository = unidadeTipoAtendimentoRepository;
    }

    @Transactional(readOnly = true)
    public List<TipoAtendimentoOption> listarOpcoesAtivasPorUnidade(Long unidadeId) {
        if (unidadeId == null) {
            return List.of();
        }
        return unidadeTipoAtendimentoRepository
                .findByUnidadeIdAndAtivoTrueOrderByTipoAtendimentoOrdemExibicaoAscTipoAtendimentoDescricaoAsc(unidadeId)
                .stream()
                .map(UnidadeTipoAtendimento::getTipoAtendimento)
                .filter(TipoAtendimentoCadastro::isAtivo)
                .map(this::toOption)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TipoAtendimentoOption> listarOpcoesComTriagemObrigatoriaPorUnidade(Long unidadeId) {
        if (unidadeId == null) {
            return List.of();
        }
        return unidadeTipoAtendimentoRepository
                .findByUnidadeIdAndAtivoTrueOrderByTipoAtendimentoOrdemExibicaoAscTipoAtendimentoDescricaoAsc(unidadeId)
                .stream()
                .filter(UnidadeTipoAtendimento::isTriagemObrigatoria)
                .map(UnidadeTipoAtendimento::getTipoAtendimento)
                .filter(item -> item != null && item.isAtivo())
                .map(this::toOption)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TipoAtendimentoOption> listarOpcoesAtivasGlobais() {
        return tipoAtendimentoCadastroRepository.findByAtivoTrueOrderByOrdemExibicaoAscDescricaoAsc().stream()
                .map(this::toOption)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TipoAtendimentoOption> listarOpcoesAtivasPorUnidadeOuGlobal(Long unidadeId) {
        List<TipoAtendimentoOption> porUnidade = listarOpcoesAtivasPorUnidade(unidadeId);
        if (!porUnidade.isEmpty()) {
            return porUnidade;
        }
        return listarOpcoesAtivasGlobais();
    }

    @Transactional(readOnly = true)
    public Set<String> listarCodigosAtivosPorUnidadeOuGlobal(Long unidadeId) {
        return listarOpcoesAtivasPorUnidadeOuGlobal(unidadeId).stream()
                .map(TipoAtendimentoOption::getCodigo)
                .map(TipoAtendimentoService::normalizeCodigo)
                .filter(value -> value != null)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Transactional(readOnly = true)
    public List<TipoAtendimentoOption> listarOpcoesAtivasPorCodigos(Set<String> codigos) {
        if (codigos == null || codigos.isEmpty()) {
            return List.of();
        }
        Set<String> normalizados = codigos.stream()
                .map(TipoAtendimentoService::normalizeCodigo)
                .filter(value -> value != null)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        if (normalizados.isEmpty()) {
            return List.of();
        }
        return tipoAtendimentoCadastroRepository.findByAtivoTrueOrderByOrdemExibicaoAscDescricaoAsc().stream()
                .filter(item -> normalizados.contains(normalizeCodigo(item.getCodigo())))
                .map(this::toOption)
                .toList();
    }

    @Transactional(readOnly = true)
    public TipoAtendimentoCadastro buscarAtivoGlobalPorCodigo(String codigo) {
        String normalizado = normalizeCodigo(codigo);
        if (normalizado == null) {
            throw new IllegalArgumentException("Tipo de atendimento e obrigatorio");
        }
        return tipoAtendimentoCadastroRepository.findByCodigoIgnoreCase(normalizado)
                .filter(TipoAtendimentoCadastro::isAtivo)
                .orElseThrow(() -> new IllegalArgumentException("Tipo de atendimento invalido: " + normalizado));
    }

    @Transactional(readOnly = true)
    public UnidadeTipoAtendimento buscarConfiguracaoAtivaDaUnidade(Long unidadeId, String codigo) {
        String normalizado = normalizeCodigo(codigo);
        if (unidadeId == null) {
            throw new IllegalArgumentException("Unidade obrigatoria para validar tipo de atendimento");
        }
        if (normalizado == null) {
            throw new IllegalArgumentException("Tipo de atendimento e obrigatorio");
        }
        UnidadeTipoAtendimento config = unidadeTipoAtendimentoRepository
                .findByUnidadeIdAndTipoAtendimentoCodigoIgnoreCase(unidadeId, normalizado)
                .orElseThrow(() -> new IllegalArgumentException("Tipo de atendimento nao configurado para a unidade"));
        if (!config.isAtivo() || config.getTipoAtendimento() == null || !config.getTipoAtendimento().isAtivo()) {
            throw new IllegalArgumentException("Tipo de atendimento inativo para a unidade");
        }
        return config;
    }

    @Transactional(readOnly = true)
    public boolean isTriagemObrigatoriaNaUnidade(Long unidadeId, String codigoTipoAtendimento) {
        String codigoNormalizado = normalizeCodigo(codigoTipoAtendimento);
        if (unidadeId == null || codigoNormalizado == null) {
            return false;
        }
        return unidadeTipoAtendimentoRepository
                .findByUnidadeIdAndTipoAtendimentoCodigoIgnoreCase(unidadeId, codigoNormalizado)
                .filter(config -> config.isAtivo()
                        && config.getTipoAtendimento() != null
                        && config.getTipoAtendimento().isAtivo())
                .map(UnidadeTipoAtendimento::isTriagemObrigatoria)
                .orElse(false);
    }

    public static String normalizeCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            return null;
        }
        return codigo.trim().toUpperCase(Locale.ROOT);
    }

    private TipoAtendimentoOption toOption(TipoAtendimentoCadastro item) {
        return new TipoAtendimentoOption(normalizeCodigo(item.getCodigo()), item.getDescricao());
    }
}
