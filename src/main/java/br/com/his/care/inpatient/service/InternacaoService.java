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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.his.access.model.Usuario;
import br.com.his.access.service.UsuarioAuditoriaService;
import br.com.his.care.inpatient.dto.InternacaoForm;
import br.com.his.care.attendance.model.Atendimento;
import br.com.his.care.inpatient.model.Internacao;
import br.com.his.care.inpatient.model.InternacaoOrigemDemanda;
import br.com.his.care.inpatient.model.InternacaoPerfil;
import br.com.his.care.inpatient.model.Leito;
import br.com.his.care.inpatient.model.LeitoOcupacaoTipo;
import br.com.his.care.inpatient.model.Observacao;
import br.com.his.care.attendance.repository.AtendimentoRepository;
import br.com.his.care.inpatient.repository.InternacaoOrigemDemandaRepository;
import br.com.his.care.inpatient.repository.InternacaoPerfilRepository;
import br.com.his.care.inpatient.repository.InternacaoRepository;
import br.com.his.care.inpatient.repository.LeitoOcupacaoTipoRepository;
import br.com.his.care.inpatient.repository.LeitoRepository;
import br.com.his.care.inpatient.repository.ObservacaoRepository;

@Service
public class InternacaoService {

    private final InternacaoRepository internacaoRepository;
    private final AtendimentoRepository atendimentoRepository;
    private final ObservacaoRepository observacaoRepository;
    private final InternacaoOrigemDemandaRepository internacaoOrigemDemandaRepository;
    private final InternacaoPerfilRepository internacaoPerfilRepository;
    private final LeitoOcupacaoTipoRepository leitoOcupacaoTipoRepository;
    private final LeitoRepository leitoRepository;
    private final LeitoOcupacaoService leitoOcupacaoService;
    private final UsuarioAuditoriaService usuarioAuditoriaService;

    public InternacaoService(InternacaoRepository internacaoRepository,
                             AtendimentoRepository atendimentoRepository,
                             ObservacaoRepository observacaoRepository,
                             InternacaoOrigemDemandaRepository internacaoOrigemDemandaRepository,
                             InternacaoPerfilRepository internacaoPerfilRepository,
                             LeitoOcupacaoTipoRepository leitoOcupacaoTipoRepository,
                             LeitoRepository leitoRepository,
                             LeitoOcupacaoService leitoOcupacaoService,
                             UsuarioAuditoriaService usuarioAuditoriaService) {
        this.internacaoRepository = internacaoRepository;
        this.atendimentoRepository = atendimentoRepository;
        this.observacaoRepository = observacaoRepository;
        this.internacaoOrigemDemandaRepository = internacaoOrigemDemandaRepository;
        this.internacaoPerfilRepository = internacaoPerfilRepository;
        this.leitoOcupacaoTipoRepository = leitoOcupacaoTipoRepository;
        this.leitoRepository = leitoRepository;
        this.leitoOcupacaoService = leitoOcupacaoService;
        this.usuarioAuditoriaService = usuarioAuditoriaService;
    }

    @Transactional(readOnly = true)
    public List<Internacao> listar() {
        return internacaoRepository.findAllDetalhadoOrderByDataHoraDecisaoDesc();
    }

    @Transactional(readOnly = true)
    public Internacao buscar(Long id) {
        return internacaoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Internacao nao encontrada"));
    }

    @Transactional(readOnly = true)
    public Observacao buscarObservacao(Long id) {
        return observacaoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Observacao de origem nao encontrada"));
    }

    @Transactional(readOnly = true)
    public InternacaoForm toForm(Internacao internacao) {
        InternacaoForm form = new InternacaoForm();
        form.setAtendimentoId(internacao.getAtendimento().getId());
        form.setOrigemDemandaId(internacao.getOrigemDemanda() == null ? null : internacao.getOrigemDemanda().getId());
        form.setPerfilInternacaoId(internacao.getPerfilInternacao() == null ? null : internacao.getPerfilInternacao().getId());
        form.setDataHoraDecisaoInternacao(internacao.getDataHoraDecisaoInternacao());
        form.setDataHoraInicioInternacao(internacao.getDataHoraInicioInternacao());
        form.setDataHoraFimInternacao(internacao.getDataHoraFimInternacao());
        form.setObservacao(internacao.getObservacao());
        leitoOcupacaoService.buscarOcupacaoAbertaPorInternacao(internacao.getId())
                .ifPresent(ocupacao -> {
                    form.setLeitoId(ocupacao.getLeito().getId());
                    form.setTipoOcupacaoId(ocupacao.getTipoOcupacao().getId());
                });
        return form;
    }

    @Transactional
    public Internacao criar(InternacaoForm form) {
        Internacao internacao = internacaoRepository.findByAtendimentoId(form.getAtendimentoId())
                .filter(item -> item.getDataHoraCancelamento() != null || item.getDataHoraFimInternacao() != null)
                .orElse(new Internacao());
        apply(internacao, form);
        return internacao;
    }

    @Transactional
    public Internacao atualizar(Long id, InternacaoForm form) {
        Internacao internacao = buscar(id);
        apply(internacao, form);
        return internacao;
    }

    @Transactional
    public void excluir(Long id) {
        internacaoRepository.delete(buscar(id));
    }

    @Transactional
    public void cancelar(Long id, String motivoCancelamento) {
        Internacao internacao = buscar(id);
        if (internacao.getDataHoraCancelamento() != null) {
            throw new IllegalArgumentException("Internacao ja cancelada");
        }
        if (internacao.getDataHoraFimInternacao() != null) {
            throw new IllegalArgumentException("Internacao encerrada nao pode ser cancelada");
        }
        String motivo = normalize(motivoCancelamento);
        if (motivo == null) {
            throw new IllegalArgumentException("Motivo do cancelamento e obrigatorio");
        }
        Long leitoAtualId = leitoOcupacaoService.buscarOcupacaoAbertaPorInternacao(internacao.getId())
                .map(ocupacao -> ocupacao.getLeito().getId())
                .orElse(null);
        LocalDateTime agora = LocalDateTime.now();
        internacao.setDataHoraFimInternacao(agora);
        internacao.setDataHoraCancelamento(agora);
        internacao.setCanceladoPor(currentUsername());
        internacao.setCanceladoPorUsuario(currentUsuario());
        internacao.setMotivoCancelamento(motivo);
        internacaoRepository.save(internacao);
        leitoOcupacaoService.encerrarOcupacaoInternacaoAberta(internacao.getId(), agora);

        observacaoRepository.findByAtendimentoId(internacao.getAtendimento().getId())
                .filter(obs -> obs.getDataHoraCancelamento() == null && obs.getDataHoraFim() != null)
                .ifPresent(obs -> {
                    obs.setDataHoraFim(null);
                    observacaoRepository.save(obs);
                    leitoOcupacaoService.sincronizarObservacao(obs, leitoAtualId);
                });
    }

    @Transactional(readOnly = true)
    public List<Atendimento> listarAtendimentosElegiveis(Long unidadeId, Long atendimentoSelecionadoId) {
        if (unidadeId == null) {
            return List.of();
        }
        List<Atendimento> itens = new ArrayList<>(atendimentoRepository.findAbertosSemInternacao(unidadeId, statusesAbertos()));
        if (atendimentoSelecionadoId != null) {
            atendimentoRepository.findById(atendimentoSelecionadoId)
                    .filter(atendimento -> Objects.equals(atendimento.getUnidade().getId(), unidadeId))
                    .ifPresent(atendimento -> {
                        boolean exists = itens.stream().anyMatch(item -> Objects.equals(item.getId(), atendimento.getId()));
                        if (!exists) {
                            itens.add(0, atendimento);
                        }
                    });
        }
        return itens;
    }

    @Transactional(readOnly = true)
    public List<InternacaoOrigemDemanda> listarOrigensAtivas() {
        return internacaoOrigemDemandaRepository.findByAtivoTrueOrderByDescricaoAsc();
    }

    @Transactional(readOnly = true)
    public List<InternacaoPerfil> listarPerfisAtivos() {
        return internacaoPerfilRepository.findByAtivoTrueOrderByDescricaoAsc();
    }

    @Transactional(readOnly = true)
    public List<LeitoOcupacaoTipo> listarTiposOcupacaoAtivos() {
        return leitoOcupacaoTipoRepository.findByAtivoTrueOrderByDescricaoAsc();
    }

    @Transactional(readOnly = true)
    public List<Leito> listarLeitosDisponiveis(Long unidadeId, Long leitoSelecionadoId) {
        if (unidadeId == null) {
            return List.of();
        }
        return leitoOcupacaoService.listarLeitosDisponiveis(
                unidadeId,
                LeitoOcupacaoService.MODALIDADE_INTERNACAO,
                leitoSelecionadoId);
    }

    @Transactional(readOnly = true)
    public Map<Long, String> mapaLeitoAtual(List<Internacao> internacoes) {
        if (internacoes == null || internacoes.isEmpty()) {
            return Map.of();
        }
        Map<Long, String> result = new LinkedHashMap<>();
        for (Internacao item : internacoes) {
            String descricao = leitoOcupacaoService.buscarOcupacaoAbertaPorInternacao(item.getId())
                    .map(ocupacao -> ocupacao.getLeito().getArea().getDescricao() + " - " + ocupacao.getLeito().getCodigo())
                    .orElse("-");
            result.put(item.getId(), descricao);
        }
        return result;
    }

    @Transactional(readOnly = true)
    public Long leitoAtualObservacao(Long observacaoId) {
        return leitoOcupacaoService.buscarOcupacaoAbertaPorObservacao(observacaoId)
                .map(ocupacao -> ocupacao.getLeito().getId())
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public Long resolverTipoOcupacaoPadraoPorLeito(Long unidadeId, Long leitoId) {
        if (unidadeId == null || leitoId == null) {
            return null;
        }
        Leito leito = leitoRepository.findById(leitoId)
                .orElseThrow(() -> new IllegalArgumentException("Leito nao encontrado"));
        if (!Objects.equals(leito.getUnidade().getId(), unidadeId)) {
            throw new IllegalArgumentException("Leito nao pertence a unidade atual");
        }
        String codigoPadrao = leito.isPermiteDestinoDefinitivo()
                ? LeitoOcupacaoService.OCUPACAO_DEFINITIVA
                : LeitoOcupacaoService.OCUPACAO_PROVISORIA;
        return leitoOcupacaoTipoRepository.findByCodigoIgnoreCase(codigoPadrao)
                .filter(LeitoOcupacaoTipo::isAtivo)
                .map(LeitoOcupacaoTipo::getId)
                .orElseThrow(() -> new IllegalArgumentException("Tipo de ocupacao padrao nao configurado: " + codigoPadrao));
    }

    private Internacao apply(Internacao internacao, InternacaoForm form) {
        Observacao observacaoOrigem = form.getObservacaoOrigemId() == null
                ? null
                : buscarObservacao(form.getObservacaoOrigemId());
        if (observacaoOrigem != null
                && (observacaoOrigem.getDataHoraCancelamento() != null || observacaoOrigem.getDataHoraFim() != null)) {
            throw new IllegalArgumentException("Observacao de origem nao esta mais ativa para conversao");
        }

        Long atendimentoId = form.getAtendimentoId();
        if (observacaoOrigem != null) {
            if (atendimentoId != null && !Objects.equals(atendimentoId, observacaoOrigem.getAtendimento().getId())) {
                throw new IllegalArgumentException("Atendimento informado nao corresponde a observacao selecionada");
            }
            atendimentoId = observacaoOrigem.getAtendimento().getId();
        }
        if (atendimentoId == null) {
            throw new IllegalArgumentException("Atendimento e obrigatorio");
        }

        Atendimento atendimento = atendimentoRepository.findById(atendimentoId)
                .orElseThrow(() -> new IllegalArgumentException("Atendimento nao encontrado"));
        if (isEncerrado(atendimento)) {
            throw new IllegalArgumentException("Atendimento ja encerrado");
        }

        internacaoRepository.findByAtendimentoIdAndDataHoraCancelamentoIsNullAndDataHoraFimInternacaoIsNull(atendimento.getId())
                .filter(existente -> internacao.getId() == null || !Objects.equals(existente.getId(), internacao.getId()))
                .ifPresent(existente -> {
                    throw new IllegalArgumentException("Ja existe internacao ativa para este atendimento");
                });

        Observacao observacaoAtiva = observacaoRepository
                .findByAtendimentoIdAndDataHoraCancelamentoIsNullAndDataHoraFimIsNull(atendimento.getId())
                .orElse(null);
        if (observacaoAtiva != null && observacaoOrigem == null) {
            throw new IllegalArgumentException(
                    "Atendimento em observacao deve ser convertido para internacao pelo mapa de leitos");
        }

        if (form.getDataHoraInicioInternacao() != null
                && form.getDataHoraInicioInternacao().isBefore(form.getDataHoraDecisaoInternacao())) {
            throw new IllegalArgumentException("Inicio da internacao nao pode ser anterior a decisao");
        }

        InternacaoOrigemDemanda origemDemanda = null;
        if (form.getOrigemDemandaId() != null) {
            origemDemanda = internacaoOrigemDemandaRepository.findById(form.getOrigemDemandaId())
                    .orElseThrow(() -> new IllegalArgumentException("Origem da demanda nao encontrada"));
            if (!origemDemanda.isAtivo()) {
                throw new IllegalArgumentException("Origem da demanda inativa");
            }
        }

        InternacaoPerfil perfil = null;
        if (form.getPerfilInternacaoId() != null) {
            perfil = internacaoPerfilRepository.findById(form.getPerfilInternacaoId())
                    .orElseThrow(() -> new IllegalArgumentException("Perfil de internacao nao encontrado"));
            if (!perfil.isAtivo()) {
                throw new IllegalArgumentException("Perfil de internacao inativo");
            }
        }

        internacao.setAtendimento(atendimento);
        internacao.setOrigemDemanda(origemDemanda);
        internacao.setPerfilInternacao(perfil);
        internacao.setDataHoraDecisaoInternacao(form.getDataHoraDecisaoInternacao());
        internacao.setDataHoraInicioInternacao(form.getDataHoraInicioInternacao());
        internacao.setDataHoraCancelamento(null);
        internacao.setCanceladoPor(null);
        internacao.setCanceladoPorUsuario(null);
        internacao.setMotivoCancelamento(null);
        internacao.setObservacao(normalize(form.getObservacao()));
        Internacao saved = internacaoRepository.save(internacao);

        if (observacaoOrigem != null) {
            LocalDateTime referenciaConversao = form.getDataHoraInicioInternacao() != null
                    ? form.getDataHoraInicioInternacao()
                    : form.getDataHoraDecisaoInternacao();
            Long leitoObservacaoAtual = leitoAtualObservacao(observacaoOrigem.getId());
            if (leitoObservacaoAtual == null) {
                throw new IllegalArgumentException("Observacao de origem sem leito ativo para conversao");
            }
            form.setLeitoId(leitoObservacaoAtual);
            form.setTipoOcupacaoId(resolverTipoOcupacaoPadraoPorLeito(
                    atendimento.getUnidade().getId(),
                    leitoObservacaoAtual));
            if (observacaoOrigem.getDataHoraFim() == null) {
                observacaoOrigem.setDataHoraFim(referenciaConversao);
                observacaoRepository.save(observacaoOrigem);
            }
            leitoOcupacaoService.encerrarOcupacaoObservacaoAberta(observacaoOrigem.getId(), referenciaConversao);
        }

        leitoOcupacaoService.sincronizarInternacao(saved, form.getLeitoId(), form.getTipoOcupacaoId());
        return saved;
    }

    private static boolean isEncerrado(Atendimento atendimento) {
        if (atendimento.getStatus() == null || atendimento.getStatus().getCodigo() == null) {
            return false;
        }
        String codigo = atendimento.getStatus().getCodigo();
        return "FINALIZADO".equalsIgnoreCase(codigo)
                || "EVADIU".equalsIgnoreCase(codigo)
                || "ABANDONO".equalsIgnoreCase(codigo)
                || "TRANSFERIDO".equalsIgnoreCase(codigo);
    }

    private static List<String> statusesAbertos() {
        return List.of(
                "AGUARDANDO",
                "EM_TRIAGEM",
                "AGUARDANDO_RECEPCAO",
                "AGUARDANDO_TRIAGEM",
                "AGUARDANDO_MEDICO",
                "EM_ATENDIMENTO");
    }

    private static String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private String currentUsername() {
        return usuarioAuditoriaService.usernameAtualOuSistema();
    }

    private Usuario currentUsuario() {
        return usuarioAuditoriaService.usuarioAtual().orElse(null);
    }
}
