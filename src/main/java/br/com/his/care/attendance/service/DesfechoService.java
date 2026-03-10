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

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.his.care.attendance.dto.DesfechoForm;
import br.com.his.care.attendance.model.Desfecho;
import br.com.his.care.attendance.model.Atendimento;
import br.com.his.care.attendance.model.DestinoRede;
import br.com.his.care.attendance.model.MotivoDesfecho;
import br.com.his.care.attendance.model.TipoDesfecho;
import br.com.his.care.attendance.repository.DesfechoRepository;
import br.com.his.care.attendance.repository.AtendimentoRepository;
import br.com.his.care.attendance.repository.DestinoRedeRepository;
import br.com.his.care.attendance.repository.MotivoDesfechoRepository;
import br.com.his.care.attendance.repository.TipoDesfechoRepository;
import br.com.his.patient.model.Paciente;

@Service
public class DesfechoService {

    private final DesfechoRepository desfechoRepository;
    private final AtendimentoRepository atendimentoRepository;
    private final TipoDesfechoRepository tipoDesfechoRepository;
    private final MotivoDesfechoRepository motivoDesfechoRepository;
    private final DestinoRedeRepository destinoRedeRepository;
    private final AssistencialFlowService assistencialFlowService;

    public DesfechoService(DesfechoRepository desfechoRepository,
                       AtendimentoRepository atendimentoRepository,
                       TipoDesfechoRepository tipoDesfechoRepository,
                       MotivoDesfechoRepository motivoDesfechoRepository,
                       DestinoRedeRepository destinoRedeRepository,
                       AssistencialFlowService assistencialFlowService) {
        this.desfechoRepository = desfechoRepository;
        this.atendimentoRepository = atendimentoRepository;
        this.tipoDesfechoRepository = tipoDesfechoRepository;
        this.motivoDesfechoRepository = motivoDesfechoRepository;
        this.destinoRedeRepository = destinoRedeRepository;
        this.assistencialFlowService = assistencialFlowService;
    }

    @Transactional(readOnly = true)
    public List<Desfecho> listar() {
        return desfechoRepository.findAllByOrderByDataHoraDesc();
    }

    @Transactional(readOnly = true)
    public Desfecho buscar(Long id) {
        return desfechoRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Desfecho nao encontrado"));
    }

    @Transactional
    public Desfecho criar(DesfechoForm form) {
        Desfecho desfecho = new Desfecho();
        apply(desfecho, form);
        Desfecho saved = desfechoRepository.save(desfecho);
        assistencialFlowService.aplicarDesfecho(saved.getAtendimento().getId(), saved.getMotivoDesfecho().getDescricao());
        return saved;
    }

    @Transactional
    public Desfecho atualizar(Long id, DesfechoForm form) {
        Desfecho desfecho = buscar(id);
        apply(desfecho, form);
        return desfechoRepository.save(desfecho);
    }

    @Transactional
    public void excluir(Long id) {
        desfechoRepository.delete(buscar(id));
    }

    @Transactional(readOnly = true)
    public DesfechoForm toForm(Desfecho desfecho) {
        DesfechoForm form = new DesfechoForm();
        form.setAtendimentoId(desfecho.getAtendimento().getId());
        form.setTipoDesfechoId(desfecho.getTipoDesfecho().getId());
        form.setMotivoDesfechoId(desfecho.getMotivoDesfecho().getId());
        form.setDestinoRedeId(desfecho.getDestinoRede() == null ? null : desfecho.getDestinoRede().getId());
        form.setDataHora(desfecho.getDataHora());
        form.setObservacao(desfecho.getObservacao());
        return form;
    }

    private void apply(Desfecho desfecho, DesfechoForm form) {
        Atendimento atendimento = atendimentoRepository.findById(form.getAtendimentoId())
                .orElseThrow(() -> new IllegalArgumentException("Atendimento nao encontrado"));
        if (pacienteNaoIdentificado(atendimento)) {
            throw new IllegalArgumentException(
                    "Paciente nao identificado. Identifique o paciente antes de registrar desfecho.");
        }
        TipoDesfecho tipoDesfecho = tipoDesfechoRepository.findById(form.getTipoDesfechoId())
                .orElseThrow(() -> new IllegalArgumentException("Tipo de desfecho nao encontrado"));
        MotivoDesfecho motivoDesfecho = motivoDesfechoRepository.findById(form.getMotivoDesfechoId())
                .orElseThrow(() -> new IllegalArgumentException("Motivo de desfecho nao encontrado"));
        boolean orientadoRede = "ORIENTADO_REDE".equalsIgnoreCase(motivoDesfecho.getDescricao());
        DestinoRede destinoRede = null;
        if (orientadoRede) {
            if (form.getDestinoRedeId() == null) {
                throw new IllegalArgumentException("Destino de rede e obrigatorio para motivo ORIENTADO_REDE");
            }
            destinoRede = destinoRedeRepository.findById(form.getDestinoRedeId())
                    .filter(DestinoRede::isAtivo)
                    .orElseThrow(() -> new IllegalArgumentException("Destino de rede nao encontrado ou inativo"));
        }
        desfechoRepository.findByAtendimentoId(atendimento.getId())
                .filter(existente -> desfecho.getId() == null || !existente.getId().equals(desfecho.getId()))
                .ifPresent(existente -> {
                    throw new IllegalArgumentException("Ja existe desfecho cadastrado para este atendimento");
                });
        desfecho.setAtendimento(atendimento);
        desfecho.setTipoDesfecho(tipoDesfecho);
        desfecho.setMotivoDesfecho(motivoDesfecho);
        desfecho.setDestinoRede(destinoRede);
        desfecho.setDataHora(form.getDataHora());
        desfecho.setObservacao(normalize(form.getObservacao()));
    }

    public boolean pacienteNaoIdentificado(Atendimento atendimento) {
        if (atendimento == null || atendimento.getPaciente() == null) {
            return true;
        }
        Paciente paciente = atendimento.getPaciente();
        return paciente.isTemporario() && paciente.getMergedInto() == null;
    }

    private static String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
