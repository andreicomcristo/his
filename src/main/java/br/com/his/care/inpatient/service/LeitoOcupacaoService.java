package br.com.his.care.inpatient.service;

import br.com.his.care.attendance.api.dto.*;
import br.com.his.care.attendance.dto.*;
import br.com.his.care.attendance.model.*;
import br.com.his.care.attendance.repository.*;
import br.com.his.care.attendance.service.*;
import br.com.his.care.admission.dto.*;
import br.com.his.care.admission.model.*;
import br.com.his.care.admission.repository.*;
import br.com.his.care.triage.dto.*;
import br.com.his.care.triage.model.*;
import br.com.his.care.triage.repository.*;
import br.com.his.care.inpatient.dto.*;
import br.com.his.care.inpatient.model.*;
import br.com.his.care.inpatient.repository.*;
import br.com.his.care.inpatient.service.*;
import br.com.his.care.episode.model.*;
import br.com.his.care.episode.repository.*;
import br.com.his.care.timeline.dto.*;
import br.com.his.care.timeline.model.*;
import br.com.his.care.timeline.repository.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.his.care.inpatient.model.Internacao;
import br.com.his.care.inpatient.model.Leito;
import br.com.his.care.inpatient.model.LeitoOcupacao;
import br.com.his.care.inpatient.model.LeitoOcupacaoTipo;
import br.com.his.care.inpatient.model.Observacao;
import br.com.his.care.inpatient.repository.LeitoModalidadeRepository;
import br.com.his.care.inpatient.repository.LeitoOcupacaoRepository;
import br.com.his.care.inpatient.repository.LeitoOcupacaoTipoRepository;
import br.com.his.care.inpatient.repository.LeitoRepository;

@Service
public class LeitoOcupacaoService {

    public static final String MODALIDADE_OBSERVACAO = "OBSERVACAO";
    public static final String MODALIDADE_INTERNACAO = "INTERNACAO";
    public static final String OCUPACAO_PROVISORIA = "PROVISORIA";
    public static final String OCUPACAO_DEFINITIVA = "DEFINITIVA";

    private final LeitoRepository leitoRepository;
    private final LeitoModalidadeRepository leitoModalidadeRepository;
    private final LeitoOcupacaoRepository leitoOcupacaoRepository;
    private final LeitoOcupacaoTipoRepository leitoOcupacaoTipoRepository;

    public LeitoOcupacaoService(LeitoRepository leitoRepository,
                                LeitoModalidadeRepository leitoModalidadeRepository,
                                LeitoOcupacaoRepository leitoOcupacaoRepository,
                                LeitoOcupacaoTipoRepository leitoOcupacaoTipoRepository) {
        this.leitoRepository = leitoRepository;
        this.leitoModalidadeRepository = leitoModalidadeRepository;
        this.leitoOcupacaoRepository = leitoOcupacaoRepository;
        this.leitoOcupacaoTipoRepository = leitoOcupacaoTipoRepository;
    }

    @Transactional(readOnly = true)
    public List<Leito> listarLeitosDisponiveis(Long unidadeId, String modalidadeCodigo, Long leitoSelecionadoId) {
        if (unidadeId == null || modalidadeCodigo == null || modalidadeCodigo.isBlank()) {
            return List.of();
        }
        List<Leito> candidatos = leitoModalidadeRepository
                .findLeitosAtivosPorUnidadeEModalidade(unidadeId, modalidadeCodigo);
        if (candidatos.isEmpty()) {
            return List.of();
        }
        List<Long> ids = candidatos.stream().map(Leito::getId).toList();
        Set<Long> ocupados = new HashSet<>(leitoOcupacaoRepository.findLeitoIdsComOcupacaoAberta(ids));
        return candidatos.stream()
                .filter(leito -> !ocupados.contains(leito.getId())
                        || Objects.equals(leito.getId(), leitoSelecionadoId))
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<LeitoOcupacao> buscarOcupacaoAbertaPorObservacao(Long observacaoId) {
        if (observacaoId == null) {
            return Optional.empty();
        }
        return leitoOcupacaoRepository.findFirstByObservacaoAtendimentoIdAndDataHoraSaidaIsNull(observacaoId);
    }

    @Transactional(readOnly = true)
    public Optional<LeitoOcupacao> buscarOcupacaoAbertaPorInternacao(Long internacaoId) {
        if (internacaoId == null) {
            return Optional.empty();
        }
        return leitoOcupacaoRepository.findFirstByInternacaoIdAndDataHoraSaidaIsNull(internacaoId);
    }

    @Transactional
    public void sincronizarObservacao(Observacao observacao, Long leitoId) {
        Objects.requireNonNull(observacao, "Observacao obrigatoria");
        if (observacao.getId() == null) {
            throw new IllegalArgumentException("Observacao precisa estar persistida antes da ocupacao de leito");
        }
        Optional<LeitoOcupacao> atualOpt = buscarOcupacaoAbertaPorObservacao(observacao.getId());
        LocalDateTime entrada = observacao.getDataHoraInicio() == null ? LocalDateTime.now() : observacao.getDataHoraInicio();
        LocalDateTime saida = observacao.getDataHoraFim();

        if (leitoId == null) {
            atualOpt.ifPresent(atual -> fecharOcupacao(atual, saida != null ? saida : LocalDateTime.now()));
            return;
        }

        Leito leito = validarLeito(leitoId,
                observacao.getAtendimento().getUnidade().getId(),
                MODALIDADE_OBSERVACAO,
                false,
                atualOpt.orElse(null));

        if (atualOpt.isPresent() && Objects.equals(atualOpt.get().getLeito().getId(), leito.getId())) {
            if (saida != null) {
                fecharOcupacao(atualOpt.get(), saida);
            }
            return;
        }

        if (atualOpt.isPresent()) {
            fecharOcupacao(atualOpt.get(), entrada);
        }
        LeitoOcupacao nova = new LeitoOcupacao();
        nova.setLeito(leito);
        nova.setObservacaoAtendimento(observacao);
        nova.setTipoOcupacao(resolveTipoOcupacao(null));
        nova.setDataHoraEntrada(entrada);
        if (saida != null) {
            nova.setDataHoraSaida(saida);
        }
        leitoOcupacaoRepository.save(nova);
    }

    @Transactional
    public void sincronizarInternacao(Internacao internacao, Long leitoId, Long tipoOcupacaoId) {
        Objects.requireNonNull(internacao, "Internacao obrigatoria");
        if (internacao.getId() == null) {
            throw new IllegalArgumentException("Internacao precisa estar persistida antes da ocupacao de leito");
        }
        Optional<LeitoOcupacao> atualOpt = buscarOcupacaoAbertaPorInternacao(internacao.getId());
        LocalDateTime entrada = internacao.getDataHoraInicioInternacao() != null
                ? internacao.getDataHoraInicioInternacao()
                : internacao.getDataHoraDecisaoInternacao();
        if (entrada == null) {
            entrada = LocalDateTime.now();
        }
        LocalDateTime saida = internacao.getDataHoraFimInternacao();

        if (leitoId == null) {
            atualOpt.ifPresent(atual -> fecharOcupacao(atual, saida != null ? saida : LocalDateTime.now()));
            return;
        }

        LeitoOcupacaoTipo tipoOcupacao = resolveTipoOcupacao(tipoOcupacaoId);
        boolean exigeDestinoDefinitivo = OCUPACAO_DEFINITIVA.equalsIgnoreCase(tipoOcupacao.getCodigo());
        Leito leito = validarLeito(leitoId,
                internacao.getAtendimento().getUnidade().getId(),
                MODALIDADE_INTERNACAO,
                exigeDestinoDefinitivo,
                atualOpt.orElse(null));

        if (atualOpt.isPresent() && Objects.equals(atualOpt.get().getLeito().getId(), leito.getId())) {
            LeitoOcupacao atual = atualOpt.get();
            atual.setTipoOcupacao(tipoOcupacao);
            if (saida != null) {
                fecharOcupacao(atual, saida);
            } else {
                leitoOcupacaoRepository.save(atual);
            }
            return;
        }

        if (atualOpt.isPresent()) {
            fecharOcupacao(atualOpt.get(), entrada);
        }
        LeitoOcupacao nova = new LeitoOcupacao();
        nova.setLeito(leito);
        nova.setInternacao(internacao);
        nova.setTipoOcupacao(tipoOcupacao);
        nova.setDataHoraEntrada(entrada);
        if (saida != null) {
            nova.setDataHoraSaida(saida);
        }
        leitoOcupacaoRepository.save(nova);
    }

    @Transactional
    public void encerrarOcupacaoObservacaoAberta(Long observacaoId, LocalDateTime dataHoraSaida) {
        buscarOcupacaoAbertaPorObservacao(observacaoId)
                .ifPresent(ocupacao -> fecharOcupacao(ocupacao, dataHoraSaida == null ? LocalDateTime.now() : dataHoraSaida));
    }

    @Transactional
    public void encerrarOcupacaoInternacaoAberta(Long internacaoId, LocalDateTime dataHoraSaida) {
        buscarOcupacaoAbertaPorInternacao(internacaoId)
                .ifPresent(ocupacao -> fecharOcupacao(ocupacao, dataHoraSaida == null ? LocalDateTime.now() : dataHoraSaida));
    }

    @Transactional(readOnly = true)
    public Optional<LeitoOcupacao> buscarOcupacaoDetalhada(Long ocupacaoId) {
        if (ocupacaoId == null) {
            return Optional.empty();
        }
        return leitoOcupacaoRepository.findByIdDetalhada(ocupacaoId);
    }

    @Transactional(readOnly = true)
    public List<LeitoOcupacao> listarOcupacoesAbertasDetalhadasPorLeitos(List<Long> leitoIds) {
        if (leitoIds == null || leitoIds.isEmpty()) {
            return List.of();
        }
        return leitoOcupacaoRepository.findAbertasDetalhadasByLeitoIds(leitoIds);
    }

    @Transactional
    public LeitoOcupacao transferirInterno(Long ocupacaoId, Long leitoDestinoId, String observacaoTransferencia) {
        LeitoOcupacao ocupacaoAtual = leitoOcupacaoRepository.findById(ocupacaoId)
                .orElseThrow(() -> new IllegalArgumentException("Ocupacao de leito nao encontrada"));
        if (ocupacaoAtual.getDataHoraSaida() != null) {
            throw new IllegalArgumentException("Ocupacao ja encerrada");
        }
        if (leitoDestinoId == null) {
            throw new IllegalArgumentException("Leito de destino e obrigatorio");
        }
        if (Objects.equals(ocupacaoAtual.getLeito().getId(), leitoDestinoId)) {
            throw new IllegalArgumentException("Leito de destino deve ser diferente do atual");
        }

        String modalidade;
        Long unidadeId;
        boolean exigeDestinoDefinitivo = false;
        if (ocupacaoAtual.getObservacaoAtendimento() != null) {
            modalidade = MODALIDADE_OBSERVACAO;
            unidadeId = ocupacaoAtual.getObservacaoAtendimento().getAtendimento().getUnidade().getId();
        } else if (ocupacaoAtual.getInternacao() != null) {
            modalidade = MODALIDADE_INTERNACAO;
            unidadeId = ocupacaoAtual.getInternacao().getAtendimento().getUnidade().getId();
            exigeDestinoDefinitivo = ocupacaoAtual.getTipoOcupacao() != null
                    && OCUPACAO_DEFINITIVA.equalsIgnoreCase(ocupacaoAtual.getTipoOcupacao().getCodigo());
        } else {
            throw new IllegalArgumentException("Ocupacao sem vinculo assistencial");
        }

        Leito destino = validarLeito(leitoDestinoId, unidadeId, modalidade, exigeDestinoDefinitivo, null);
        LocalDateTime agora = LocalDateTime.now();
        fecharOcupacao(ocupacaoAtual, agora);

        LeitoOcupacao nova = new LeitoOcupacao();
        nova.setLeito(destino);
        nova.setObservacaoAtendimento(ocupacaoAtual.getObservacaoAtendimento());
        nova.setInternacao(ocupacaoAtual.getInternacao());
        nova.setTipoOcupacao(ocupacaoAtual.getTipoOcupacao());
        nova.setDataHoraEntrada(agora);
        nova.setObservacao(normalize(observacaoTransferencia));
        return leitoOcupacaoRepository.save(nova);
    }

    private Leito validarLeito(Long leitoId,
                               Long unidadeId,
                               String modalidadeCodigo,
                               boolean exigeDestinoDefinitivo,
                               LeitoOcupacao ocupacaoAtual) {
        Leito leito = leitoRepository.findByIdAndDtCancelamentoIsNull(leitoId)
                .orElseThrow(() -> new IllegalArgumentException("Leito nao encontrado"));
        if (!Objects.equals(leito.getUnidade().getId(), unidadeId)) {
            throw new IllegalArgumentException("Leito nao pertence a unidade do atendimento");
        }
        if (!leitoModalidadeRepository.existsByLeitoIdAndModalidadeCodigo(leitoId, modalidadeCodigo)) {
            throw new IllegalArgumentException("Leito nao habilitado para modalidade " + modalidadeCodigo);
        }
        if (exigeDestinoDefinitivo && !leito.isPermiteDestinoDefinitivo()) {
            throw new IllegalArgumentException("Leito nao permite destino definitivo");
        }
        Optional<LeitoOcupacao> ocupacaoAbertaLeito = leitoOcupacaoRepository.findFirstByLeitoIdAndDataHoraSaidaIsNull(leitoId);
        if (ocupacaoAbertaLeito.isPresent()
                && (ocupacaoAtual == null || !Objects.equals(ocupacaoAbertaLeito.get().getId(), ocupacaoAtual.getId()))) {
            throw new IllegalArgumentException("Leito ja ocupado no momento");
        }
        return leito;
    }

    private LeitoOcupacaoTipo resolveTipoOcupacao(Long tipoOcupacaoId) {
        if (tipoOcupacaoId == null) {
            return leitoOcupacaoTipoRepository.findByCodigoIgnoreCase(OCUPACAO_PROVISORIA)
                    .orElseThrow(() -> new IllegalArgumentException("Tipo de ocupacao PROVISORIA nao encontrado"));
        }
        LeitoOcupacaoTipo tipo = leitoOcupacaoTipoRepository.findById(tipoOcupacaoId)
                .orElseThrow(() -> new IllegalArgumentException("Tipo de ocupacao nao encontrado"));
        if (!tipo.isAtivo()) {
            throw new IllegalArgumentException("Tipo de ocupacao inativo");
        }
        return tipo;
    }

    private void fecharOcupacao(LeitoOcupacao ocupacao, LocalDateTime dataHoraSaida) {
        LocalDateTime saida = dataHoraSaida == null ? LocalDateTime.now() : dataHoraSaida;
        if (saida.isBefore(ocupacao.getDataHoraEntrada())) {
            throw new IllegalArgumentException("Data/hora de saida do leito nao pode ser anterior a entrada");
        }
        ocupacao.setDataHoraSaida(saida);
        leitoOcupacaoRepository.save(ocupacao);
    }

    private static String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
