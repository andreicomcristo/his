package br.com.his.care.attendance.service;

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
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.his.access.model.Usuario;
import br.com.his.access.model.Unidade;
import br.com.his.access.repository.UnidadeRepository;
import br.com.his.access.service.UsuarioAuditoriaService;
import br.com.his.care.attendance.dto.TransferenciaExternaForm;
import br.com.his.care.attendance.model.Atendimento;
import br.com.his.care.episode.model.Episodio;
import br.com.his.care.attendance.model.StatusTransferenciaExterna;
import br.com.his.care.attendance.model.TransferenciaExterna;
import br.com.his.care.attendance.repository.StatusTransferenciaExternaRepository;
import br.com.his.care.attendance.repository.TransferenciaExternaRepository;

@Service
public class TransferenciaExternaService {

    private static final String STATUS_SOLICITADA = "SOLICITADA";
    private static final String STATUS_EM_TRANSPORTE = "EM_TRANSPORTE";
    private static final String STATUS_RECEBIDA = "RECEBIDA";
    private static final Set<String> STATUS_ABERTAS = Set.of(STATUS_SOLICITADA, STATUS_EM_TRANSPORTE);

    private final AssistencialFlowService assistencialFlowService;
    private final UnidadeRepository unidadeRepository;
    private final TransferenciaExternaRepository transferenciaExternaRepository;
    private final StatusTransferenciaExternaRepository statusTransferenciaExternaRepository;
    private final UsuarioAuditoriaService usuarioAuditoriaService;

    public TransferenciaExternaService(AssistencialFlowService assistencialFlowService,
                                       UnidadeRepository unidadeRepository,
                                       TransferenciaExternaRepository transferenciaExternaRepository,
                                       StatusTransferenciaExternaRepository statusTransferenciaExternaRepository,
                                       UsuarioAuditoriaService usuarioAuditoriaService) {
        this.assistencialFlowService = assistencialFlowService;
        this.unidadeRepository = unidadeRepository;
        this.transferenciaExternaRepository = transferenciaExternaRepository;
        this.statusTransferenciaExternaRepository = statusTransferenciaExternaRepository;
        this.usuarioAuditoriaService = usuarioAuditoriaService;
    }

    @Transactional(readOnly = true)
    public List<TransferenciaExterna> listarRecebidasPendentes(Long unidadeDestinoId) {
        return transferenciaExternaRepository.findByUnidadeDestinoIdAndStatusCodigoInOrderByDataSolicitacaoAsc(
                unidadeDestinoId, STATUS_ABERTAS);
    }

    @Transactional
    public TransferenciaExterna solicitar(Long unidadeOrigemAtualId, Long atendimentoOrigemId, TransferenciaExternaForm form) {
        Atendimento atendimentoOrigem = assistencialFlowService.buscarAtendimento(atendimentoOrigemId);
        if (!atendimentoOrigem.getUnidade().getId().equals(unidadeOrigemAtualId)) {
            throw new IllegalArgumentException("Atendimento nao pertence a unidade atual");
        }
        if (form.getUnidadeDestinoId().equals(unidadeOrigemAtualId)) {
            throw new IllegalArgumentException("Unidade destino deve ser diferente da unidade origem");
        }
        if (transferenciaExternaRepository.existsByAtendimentoOrigemIdAndStatusCodigoIn(atendimentoOrigemId, STATUS_ABERTAS)) {
            throw new IllegalArgumentException("Ja existe transferencia aberta para este atendimento");
        }

        Episodio episodio = assistencialFlowService.buscarEpisodioPorAtendimento(atendimentoOrigemId)
                .orElseGet(() -> assistencialFlowService.abrirEpisodio(atendimentoOrigemId));

        Unidade unidadeDestino = unidadeRepository.findById(form.getUnidadeDestinoId())
                .orElseThrow(() -> new IllegalArgumentException("Unidade destino nao encontrada"));
        if (!unidadeDestino.isAtivo()) {
            throw new IllegalArgumentException("Unidade destino inativa");
        }

        LocalDateTime now = LocalDateTime.now();
        String usuario = currentUsername();
        Usuario usuarioAtual = currentUsuario();

        TransferenciaExterna transferencia = new TransferenciaExterna();
        transferencia.setEpisodio(episodio);
        transferencia.setAtendimentoOrigem(atendimentoOrigem);
        transferencia.setUnidadeOrigem(atendimentoOrigem.getUnidade());
        transferencia.setUnidadeDestino(unidadeDestino);
        transferencia.setStatus(status(STATUS_EM_TRANSPORTE));
        transferencia.setMotivo(normalizeRequired(form.getMotivo(), "Motivo e obrigatorio"));
        transferencia.setObservacao(normalize(form.getObservacao()));
        transferencia.setDataSolicitacao(now);
        transferencia.setDataSaida(now);
        transferencia.setUsuarioSolicitacao(usuario);
        transferencia.setUsuarioSolicitacaoUsuario(usuarioAtual);
        transferencia.setUsuarioSaida(usuario);
        transferencia.setUsuarioSaidaUsuario(usuarioAtual);
        transferencia = transferenciaExternaRepository.save(transferencia);

        String metadata = "{\"transferenciaExternaId\":" + transferencia.getId() + "}";
        assistencialFlowService.registrarEventoTransferenciaSolicitada(atendimentoOrigemId, metadata);
        assistencialFlowService.marcarAtendimentoTransferido(atendimentoOrigemId, metadata);
        return transferencia;
    }

    @Transactional
    public TransferenciaExterna acolher(Long unidadeDestinoAtualId, Long transferenciaId) {
        TransferenciaExterna transferencia = transferenciaExternaRepository.findById(transferenciaId)
                .orElseThrow(() -> new IllegalArgumentException("Transferencia externa nao encontrada"));
        if (!transferencia.getUnidadeDestino().getId().equals(unidadeDestinoAtualId)) {
            throw new IllegalArgumentException("Transferencia nao pertence a unidade destino atual");
        }
        String codigoStatus = transferencia.getStatus().getCodigo();
        if (!STATUS_ABERTAS.contains(codigoStatus)) {
            throw new IllegalArgumentException("Transferencia nao esta pendente para acolhimento");
        }

        if (transferencia.getAtendimentoDestino() == null) {
            Atendimento novoAtendimento = assistencialFlowService.criarAtendimentoTransferencia(
                    transferencia.getEpisodio(),
                    unidadeDestinoAtualId,
                    transferencia.getAtendimentoOrigem().getTipoAtendimentoCodigo(),
                    LocalDateTime.now());
            transferencia.setAtendimentoDestino(novoAtendimento);
        }
        transferencia.setStatus(status(STATUS_RECEBIDA));
        transferencia.setDataChegada(LocalDateTime.now());
        transferencia.setUsuarioAcolhimento(currentUsername());
        transferencia.setUsuarioAcolhimentoUsuario(currentUsuario());
        return transferenciaExternaRepository.save(transferencia);
    }

    private StatusTransferenciaExterna status(String codigo) {
        return statusTransferenciaExternaRepository.findByCodigo(codigo)
                .orElseThrow(() -> new IllegalArgumentException("Status transferencia externa nao encontrado: " + codigo));
    }

    private String currentUsername() {
        return usuarioAuditoriaService.usernameAtualOuSistema();
    }

    private Usuario currentUsuario() {
        return usuarioAuditoriaService.usuarioAtual().orElse(null);
    }

    private static String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private static String normalizeRequired(String value, String errorMessage) {
        String normalized = normalize(value);
        if (normalized == null) {
            throw new IllegalArgumentException(errorMessage);
        }
        return normalized;
    }
}
