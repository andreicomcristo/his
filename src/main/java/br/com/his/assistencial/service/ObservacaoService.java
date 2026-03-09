package br.com.his.assistencial.service;

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
import br.com.his.assistencial.dto.ObservacaoForm;
import br.com.his.assistencial.model.Atendimento;
import br.com.his.assistencial.model.Leito;
import br.com.his.assistencial.model.Observacao;
import br.com.his.assistencial.repository.AtendimentoRepository;
import br.com.his.assistencial.repository.InternacaoRepository;
import br.com.his.assistencial.repository.ObservacaoRepository;

@Service
public class ObservacaoService {

    private final ObservacaoRepository observacaoRepository;
    private final AtendimentoRepository atendimentoRepository;
    private final InternacaoRepository internacaoRepository;
    private final LeitoOcupacaoService leitoOcupacaoService;
    private final UsuarioAuditoriaService usuarioAuditoriaService;

    public ObservacaoService(ObservacaoRepository observacaoRepository,
                             AtendimentoRepository atendimentoRepository,
                             InternacaoRepository internacaoRepository,
                             LeitoOcupacaoService leitoOcupacaoService,
                             UsuarioAuditoriaService usuarioAuditoriaService) {
        this.observacaoRepository = observacaoRepository;
        this.atendimentoRepository = atendimentoRepository;
        this.internacaoRepository = internacaoRepository;
        this.leitoOcupacaoService = leitoOcupacaoService;
        this.usuarioAuditoriaService = usuarioAuditoriaService;
    }

    @Transactional(readOnly = true)
    public List<Observacao> listar() {
        return observacaoRepository.findAllDetalhadoOrderByDataHoraInicioDesc();
    }

    @Transactional(readOnly = true)
    public Observacao buscar(Long id) {
        return observacaoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Observacao nao encontrada"));
    }

    @Transactional(readOnly = true)
    public ObservacaoForm toForm(Observacao observacao) {
        ObservacaoForm form = new ObservacaoForm();
        form.setAtendimentoId(observacao.getAtendimento().getId());
        form.setDataHoraInicio(observacao.getDataHoraInicio());
        form.setDataHoraFim(observacao.getDataHoraFim());
        form.setObservacao(observacao.getObservacao());
        leitoOcupacaoService.buscarOcupacaoAbertaPorObservacao(observacao.getId())
                .ifPresent(ocupacao -> form.setLeitoId(ocupacao.getLeito().getId()));
        return form;
    }

    @Transactional
    public Observacao criar(ObservacaoForm form) {
        Observacao observacao = observacaoRepository.findByAtendimentoId(form.getAtendimentoId())
                .filter(item -> item.getDataHoraCancelamento() != null || item.getDataHoraFim() != null)
                .orElse(new Observacao());
        apply(observacao, form);
        return observacao;
    }

    @Transactional
    public Observacao atualizar(Long id, ObservacaoForm form) {
        Observacao observacao = buscar(id);
        apply(observacao, form);
        return observacao;
    }

    @Transactional
    public void excluir(Long id) {
        observacaoRepository.delete(buscar(id));
    }

    @Transactional
    public void cancelar(Long id, String motivoCancelamento) {
        Observacao observacao = buscar(id);
        if (observacao.getDataHoraCancelamento() != null) {
            throw new IllegalArgumentException("Observacao ja cancelada");
        }
        if (observacao.getDataHoraFim() != null) {
            throw new IllegalArgumentException("Observacao encerrada nao pode ser cancelada");
        }
        String motivo = normalize(motivoCancelamento);
        if (motivo == null) {
            throw new IllegalArgumentException("Motivo do cancelamento e obrigatorio");
        }
        LocalDateTime agora = LocalDateTime.now();
        observacao.setDataHoraFim(agora);
        observacao.setDataHoraCancelamento(agora);
        observacao.setCanceladoPor(currentUsername());
        observacao.setCanceladoPorUsuario(currentUsuario());
        observacao.setMotivoCancelamento(motivo);
        observacaoRepository.save(observacao);
        leitoOcupacaoService.encerrarOcupacaoObservacaoAberta(observacao.getId(), agora);
    }

    @Transactional(readOnly = true)
    public List<Atendimento> listarAtendimentosElegiveis(Long unidadeId, Long atendimentoSelecionadoId) {
        if (unidadeId == null) {
            return List.of();
        }
        List<Atendimento> itens = new ArrayList<>(atendimentoRepository.findAbertosSemObservacao(unidadeId, statusesAbertos()));
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
    public List<Leito> listarLeitosDisponiveis(Long unidadeId, Long leitoSelecionadoId) {
        if (unidadeId == null) {
            return List.of();
        }
        return leitoOcupacaoService.listarLeitosDisponiveis(
                unidadeId,
                LeitoOcupacaoService.MODALIDADE_OBSERVACAO,
                leitoSelecionadoId);
    }

    @Transactional(readOnly = true)
    public Map<Long, String> mapaLeitoAtual(List<Observacao> observacoes) {
        if (observacoes == null || observacoes.isEmpty()) {
            return Map.of();
        }
        Map<Long, String> result = new LinkedHashMap<>();
        for (Observacao item : observacoes) {
            String descricao = leitoOcupacaoService.buscarOcupacaoAbertaPorObservacao(item.getId())
                    .map(ocupacao -> ocupacao.getLeito().getArea().getNome() + " - " + ocupacao.getLeito().getCodigo())
                    .orElse("-");
            result.put(item.getId(), descricao);
        }
        return result;
    }

    private Observacao apply(Observacao observacao, ObservacaoForm form) {
        Atendimento atendimento = atendimentoRepository.findById(form.getAtendimentoId())
                .orElseThrow(() -> new IllegalArgumentException("Atendimento nao encontrado"));
        if (isEncerrado(atendimento)) {
            throw new IllegalArgumentException("Atendimento ja encerrado");
        }
        internacaoRepository.findByAtendimentoIdAndDataHoraCancelamentoIsNullAndDataHoraFimInternacaoIsNull(atendimento.getId())
                .ifPresent(existente -> {
                    throw new IllegalArgumentException("Atendimento ja esta em internacao e nao pode entrar em observacao");
                });
        observacaoRepository.findByAtendimentoIdAndDataHoraCancelamentoIsNullAndDataHoraFimIsNull(atendimento.getId())
                .filter(existente -> observacao.getId() == null || !existente.getId().equals(observacao.getId()))
                .ifPresent(existente -> {
                    throw new IllegalArgumentException("Ja existe observacao ativa para este atendimento");
                });

        observacao.setAtendimento(atendimento);
        observacao.setDataHoraInicio(form.getDataHoraInicio());
        observacao.setDataHoraCancelamento(null);
        observacao.setCanceladoPor(null);
        observacao.setCanceladoPorUsuario(null);
        observacao.setMotivoCancelamento(null);
        observacao.setObservacao(normalize(form.getObservacao()));
        Observacao saved = observacaoRepository.save(observacao);
        leitoOcupacaoService.sincronizarObservacao(saved, form.getLeitoId());
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
