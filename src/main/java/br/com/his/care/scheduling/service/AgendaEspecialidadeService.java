package br.com.his.care.scheduling.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.DateTimeException;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.his.access.model.CargoColaborador;
import br.com.his.access.model.Unidade;
import br.com.his.access.model.Usuario;
import br.com.his.access.repository.CargoColaboradorRepository;
import br.com.his.access.repository.UnidadeRepository;
import br.com.his.access.service.UsuarioAuditoriaService;
import br.com.his.care.attendance.model.UnidadeConfigFluxo;
import br.com.his.care.attendance.repository.UnidadeConfigFluxoRepository;
import br.com.his.care.scheduling.dto.AgendaCalendarioLivreDto;
import br.com.his.care.scheduling.dto.AgendaCalendarioEventoDto;
import br.com.his.care.scheduling.dto.AgendaCalendarioSessaoDto;
import br.com.his.care.scheduling.dto.AgendaEspecialidadeForm;
import br.com.his.care.scheduling.dto.AgendaMapaMensalItemDto;
import br.com.his.care.scheduling.dto.AgendaPacienteForm;
import br.com.his.care.scheduling.dto.AgendaPacienteVinculoResultado;
import br.com.his.care.scheduling.dto.AgendaReagendamentoForm;
import br.com.his.care.scheduling.dto.PeriodicidadeRecorrenciaAgendamento;
import br.com.his.care.scheduling.model.AcaoAgendamentoHistorico;
import br.com.his.care.scheduling.model.AgendaEspecialidade;
import br.com.his.care.scheduling.model.AgendaEspecialidadePaciente;
import br.com.his.care.scheduling.model.AgendaEspecialidadePacienteHistorico;
import br.com.his.care.scheduling.model.AgendaEspecialidadeSlot;
import br.com.his.care.scheduling.model.Especialidade;
import br.com.his.care.scheduling.model.ModoAgendaEspecialidade;
import br.com.his.care.scheduling.model.StatusAgendaSlot;
import br.com.his.care.scheduling.model.StatusAgendamentoPaciente;
import br.com.his.care.scheduling.model.TipoVagaAgenda;
import br.com.his.care.scheduling.repository.AgendaEspecialidadePacienteHistoricoRepository;
import br.com.his.care.scheduling.repository.AgendaEspecialidadePacienteRepository;
import br.com.his.care.scheduling.repository.AgendaEspecialidadeRepository;
import br.com.his.care.scheduling.repository.AgendaEspecialidadeSlotRepository;
import br.com.his.care.scheduling.repository.CargoColaboradorEspecialidadeRepository;
import br.com.his.care.scheduling.repository.EspecialidadeRepository;
import br.com.his.patient.model.Paciente;
import br.com.his.patient.repository.PacienteRepository;

@Service
public class AgendaEspecialidadeService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private final AgendaEspecialidadeRepository agendaRepository;
    private final AgendaEspecialidadePacienteRepository agendaPacienteRepository;
    private final AgendaEspecialidadeSlotRepository agendaSlotRepository;
    private final AgendaEspecialidadePacienteHistoricoRepository agendaPacienteHistoricoRepository;
    private final CargoColaboradorRepository cargoColaboradorRepository;
    private final CargoColaboradorEspecialidadeRepository cargoColaboradorEspecialidadeRepository;
    private final EspecialidadeRepository especialidadeRepository;
    private final PacienteRepository pacienteRepository;
    private final UnidadeRepository unidadeRepository;
    private final UnidadeConfigFluxoRepository unidadeConfigFluxoRepository;
    private final UsuarioAuditoriaService usuarioAuditoriaService;

    public AgendaEspecialidadeService(AgendaEspecialidadeRepository agendaRepository,
                                      AgendaEspecialidadePacienteRepository agendaPacienteRepository,
                                      AgendaEspecialidadeSlotRepository agendaSlotRepository,
                                      AgendaEspecialidadePacienteHistoricoRepository agendaPacienteHistoricoRepository,
                                      CargoColaboradorRepository cargoColaboradorRepository,
                                      CargoColaboradorEspecialidadeRepository cargoColaboradorEspecialidadeRepository,
                                      EspecialidadeRepository especialidadeRepository,
                                      PacienteRepository pacienteRepository,
                                      UnidadeRepository unidadeRepository,
                                      UnidadeConfigFluxoRepository unidadeConfigFluxoRepository,
                                      UsuarioAuditoriaService usuarioAuditoriaService) {
        this.agendaRepository = agendaRepository;
        this.agendaPacienteRepository = agendaPacienteRepository;
        this.agendaSlotRepository = agendaSlotRepository;
        this.agendaPacienteHistoricoRepository = agendaPacienteHistoricoRepository;
        this.cargoColaboradorRepository = cargoColaboradorRepository;
        this.cargoColaboradorEspecialidadeRepository = cargoColaboradorEspecialidadeRepository;
        this.especialidadeRepository = especialidadeRepository;
        this.pacienteRepository = pacienteRepository;
        this.unidadeRepository = unidadeRepository;
        this.unidadeConfigFluxoRepository = unidadeConfigFluxoRepository;
        this.usuarioAuditoriaService = usuarioAuditoriaService;
    }

    @Transactional(readOnly = true)
    public List<AgendaEspecialidade> listar(Long unidadeId,
                                            Long cargoColaboradorId,
                                            Long especialidadeId,
                                            LocalDate dataInicio,
                                            LocalDate dataFim) {
        validarUnidadeComAgendamento(unidadeId);
        return agendaRepository.listarPorFiltros(unidadeId, cargoColaboradorId, especialidadeId, dataInicio, dataFim);
    }

    @Transactional(readOnly = true)
    public Map<Long, Long> calcularVagasDisponiveisPorAgendas(List<AgendaEspecialidade> agendas) {
        if (agendas == null || agendas.isEmpty()) {
            return Map.of();
        }
        Map<Long, Long> agendadosPorAgendaId = calcularAgendadosAtivosPorAgenda(agendas);

        Map<Long, Long> vagasDisponiveis = new LinkedHashMap<>();
        for (AgendaEspecialidade agenda : agendas) {
            long totalAgendado = agendadosPorAgendaId.getOrDefault(agenda.getId(), 0L);
            long disponiveis = Math.max(agenda.getVagasTotais() - totalAgendado, 0);
            vagasDisponiveis.put(agenda.getId(), disponiveis);
        }
        return vagasDisponiveis;
    }

    private Map<Long, Long> calcularAgendadosAtivosPorAgenda(List<AgendaEspecialidade> agendas) {
        if (agendas == null || agendas.isEmpty()) {
            return Map.of();
        }
        List<Long> agendaIds = agendas.stream()
                .map(AgendaEspecialidade::getId)
                .toList();
        return agendaPacienteRepository.contarAgendadosPorAgendaIds(agendaIds).stream()
                .collect(Collectors.toMap(
                        linha -> (Long) linha[0],
                        linha -> ((Number) linha[1]).longValue()));
    }

    @Transactional(readOnly = true)
    public List<Especialidade> listarEspecialidadesAtivasPorCargo(Long cargoColaboradorId) {
        if (cargoColaboradorId == null) {
            return List.of();
        }
        return cargoColaboradorEspecialidadeRepository.listarEspecialidadesAtivasPorCargo(cargoColaboradorId);
    }

    @Transactional(readOnly = true)
    public AgendaEspecialidade buscar(Long unidadeId, Long id) {
        validarUnidadeComAgendamento(unidadeId);
        return agendaRepository.findByIdAndUnidadeId(id, unidadeId)
                .orElseThrow(() -> new IllegalArgumentException("Agenda nao encontrada"));
    }

    @Transactional
    public int criar(Long unidadeId, AgendaEspecialidadeForm form) {
        validarUnidadeComAgendamento(unidadeId);
        CargoColaborador cargoColaborador = resolveCargoColaborador(form.getCargoColaboradorId());
        Especialidade especialidade = resolveEspecialidadeOpcional(form.getEspecialidadeId());
        validarCompatibilidadeCargoEspecialidade(cargoColaborador, especialidade);
        List<LocalDate> datasAgenda = calcularDatasParaCriacaoMensal(form);
        for (LocalDate dataAgenda : datasAgenda) {
            validarFormulario(unidadeId, form, cargoColaborador, especialidade, null, dataAgenda);
        }

        Unidade unidade = unidadeRepository.findById(unidadeId)
                .orElseThrow(() -> new IllegalArgumentException("Unidade nao encontrada"));

        LocalDateTime now = LocalDateTime.now();
        Usuario usuarioAtual = usuarioAuditoriaService.usuarioAtual().orElse(null);
        String usernameAtual = usuarioAuditoriaService.usernameAtualOuSistema();
        int totalCriadas = 0;
        for (LocalDate dataAgenda : datasAgenda) {
            AgendaEspecialidade item = new AgendaEspecialidade();
            item.setUnidade(unidade);
            item.setCargoColaborador(cargoColaborador);
            item.setEspecialidade(especialidade);
            apply(item, form, dataAgenda);
            item.setCriadoEm(now);
            item.setCriadoPor(usernameAtual);
            item.setCriadoPorUsuario(usuarioAtual);
            item.setAtualizadoEm(now);
            item.setAtualizadoPor(usernameAtual);
            item.setAtualizadoPorUsuario(usuarioAtual);

            AgendaEspecialidade agendaSalva = agendaRepository.save(item);
            sincronizarSlots(agendaSalva);
            totalCriadas++;
        }
        return totalCriadas;
    }

    @Transactional
    public AgendaEspecialidade atualizar(Long unidadeId, Long id, AgendaEspecialidadeForm form) {
        validarUnidadeComAgendamento(unidadeId);
        CargoColaborador cargoColaborador = resolveCargoColaborador(form.getCargoColaboradorId());
        Especialidade especialidade = resolveEspecialidadeOpcional(form.getEspecialidadeId());
        validarCompatibilidadeCargoEspecialidade(cargoColaborador, especialidade);
        validarFormulario(unidadeId, form, cargoColaborador, especialidade, id, form.getDataAgenda());

        AgendaEspecialidade item = buscar(unidadeId, id);
        ModoAgendaEspecialidade modoAtual = modoAgenda(item);
        ModoAgendaEspecialidade modoNovo = modoAgenda(form.getModoAgenda());
        boolean gradeAlterada = !item.getDataAgenda().equals(form.getDataAgenda())
                || !item.getHoraInicio().equals(form.getHoraInicio())
                || !item.getHoraFim().equals(form.getHoraFim())
                || !item.getIntervaloMinutos().equals(form.getIntervaloMinutos())
                || !item.getVagasTotais().equals(form.getVagasTotais())
                || modoAtual != modoNovo;
        if (gradeAlterada && agendaPacienteRepository.countByAgendaEspecialidadeId(id) > 0) {
            throw new IllegalArgumentException("Nao e permitido alterar tipo/horario/capacidade com agendamentos ativos. Reagende ou cancele antes.");
        }

        item.setCargoColaborador(cargoColaborador);
        item.setEspecialidade(especialidade);
        apply(item, form, form.getDataAgenda());

        Usuario usuarioAtual = usuarioAuditoriaService.usuarioAtual().orElse(null);
        item.setAtualizadoEm(LocalDateTime.now());
        item.setAtualizadoPor(usuarioAuditoriaService.usernameAtualOuSistema());
        item.setAtualizadoPorUsuario(usuarioAtual);
        AgendaEspecialidade agendaAtualizada = agendaRepository.save(item);
        sincronizarSlots(agendaAtualizada);
        return agendaAtualizada;
    }

    @Transactional
    public void excluir(Long unidadeId, Long id) {
        AgendaEspecialidade item = buscar(unidadeId, id);
        if (agendaPacienteRepository.existsByAgendaEspecialidadeId(id)) {
            throw new IllegalArgumentException("Agenda possui historico de pacientes vinculados e nao pode ser excluida");
        }
        agendaRepository.delete(item);
    }

    @Transactional(readOnly = true)
    public List<AgendaEspecialidadePaciente> listarPacientesAgendados(Long unidadeId, Long agendaId) {
        AgendaEspecialidade agenda = buscar(unidadeId, agendaId);
        return agendaPacienteRepository.listarPorAgenda(agenda.getId());
    }

    @Transactional(readOnly = true)
    public List<AgendaEspecialidadePaciente> listarAgendamentosRecepcao(Long unidadeId,
                                                                         Long cargoColaboradorId,
                                                                         Long especialidadeId,
                                                                         StatusAgendamentoPaciente status,
                                                                         LocalDate dataInicio,
                                                                         LocalDate dataFim) {
        validarUnidadeComAgendamento(unidadeId);
        LocalDate inicio = dataInicio == null ? LocalDate.now() : dataInicio;
        LocalDate fim = dataFim == null ? inicio : dataFim;
        if (fim.isBefore(inicio)) {
            LocalDate tmp = inicio;
            inicio = fim;
            fim = tmp;
        }
        LocalDateTime inicioDt = inicio.atStartOfDay();
        LocalDateTime fimDt = fim.plusDays(1).atStartOfDay();
        return agendaPacienteRepository.listarParaRecepcao(
                unidadeId,
                cargoColaboradorId,
                especialidadeId,
                status,
                inicioDt,
                fimDt);
    }

    @Transactional(readOnly = true)
    public AgendaEspecialidadePaciente buscarAgendamentoPaciente(Long unidadeId, Long agendaPacienteId) {
        AgendaEspecialidadePaciente item = agendaPacienteRepository.buscarPorIdComRelacionamentos(agendaPacienteId)
                .orElseThrow(() -> new IllegalArgumentException("Agendamento de paciente nao encontrado"));
        Long unidadeAgendaId = item.getAgendaEspecialidade().getUnidade().getId();
        if (!unidadeAgendaId.equals(unidadeId)) {
            throw new IllegalArgumentException("Agendamento nao pertence a unidade atual");
        }
        return item;
    }

    @Transactional
    public void registrarCheckinComAtendimento(Long unidadeId, Long agendaPacienteId, Long atendimentoId) {
        AgendaEspecialidadePaciente item = buscarAgendamentoPaciente(unidadeId, agendaPacienteId);
        StatusAgendamentoPaciente statusAtual = item.getStatus();
        if (statusAtual == StatusAgendamentoPaciente.CANCELADO
                || statusAtual == StatusAgendamentoPaciente.FALTOU
                || statusAtual == StatusAgendamentoPaciente.ATENDIDO) {
            throw new IllegalArgumentException("Status atual do agendamento nao permite check-in");
        }
        if (statusAtual == StatusAgendamentoPaciente.CONFIRMADO) {
            return;
        }

        AgendaEspecialidade agenda = item.getAgendaEspecialidade();
        AgendaEspecialidadeSlot slot = item.getAgendaSlot();
        item.setStatus(StatusAgendamentoPaciente.CONFIRMADO);
        agendaPacienteRepository.save(item);
        ocuparSlot(slot);
        registrarHistorico(
                item,
                AcaoAgendamentoHistorico.CONFIRMACAO,
                statusAtual,
                StatusAgendamentoPaciente.CONFIRMADO,
                agenda,
                agenda,
                slot,
                slot,
                item.getHoraAtendimento(),
                item.getHoraAtendimento(),
                "Check-in na recepcao. Atendimento #" + atendimentoId);
    }

    @Transactional
    public AgendaPacienteVinculoResultado vincularPaciente(Long unidadeId, Long agendaId, AgendaPacienteForm form) {
        AgendaEspecialidade agendaBase = buscar(unidadeId, agendaId);
        Paciente paciente = pacienteRepository.findById(form.getPacienteId())
                .orElseThrow(() -> new IllegalArgumentException("Paciente nao encontrado"));
        if (!paciente.isAtivo() || paciente.getMergedInto() != null) {
            throw new IllegalArgumentException("Paciente inativo ou mergeado nao pode ser agendado");
        }

        LocalTime horarioAtendimento = form.getHoraAtendimento();
        TipoVagaAgenda tipoVaga = form.getTipoVaga() == null ? TipoVagaAgenda.NORMAL : form.getTipoVaga();
        int totalOcorrencias = quantidadeOcorrenciasSolicitadas(form);
        List<String> avisos = new ArrayList<>();
        int totalCriado = 0;

        for (int i = 0; i < totalOcorrencias; i++) {
            LocalDate dataOcorrencia = agendaBase.getDataAgenda().plusWeeks(i);
            AgendaEspecialidade agendaOcorrencia = resolverAgendaParaOcorrencia(
                    unidadeId,
                    agendaBase,
                    dataOcorrencia,
                    horarioAtendimento,
                    avisos);
            if (agendaOcorrencia == null) {
                continue;
            }
            try {
                vincularPacienteEmAgenda(
                        agendaOcorrencia,
                        paciente,
                        tipoVaga,
                        horarioAtendimento,
                        form.getObservacao(),
                        i > 0);
                totalCriado++;
            } catch (IllegalArgumentException ex) {
                avisos.add("Data " + dataOcorrencia.format(DATE_FORMATTER) + ": " + ex.getMessage());
            }
        }

        if (totalCriado == 0) {
            String detalhe = avisos.isEmpty() ? "" : " " + avisos.get(0);
            throw new IllegalArgumentException("Nenhuma sessao foi vinculada." + detalhe);
        }
        return new AgendaPacienteVinculoResultado(totalOcorrencias, totalCriado, avisos);
    }

    private int quantidadeOcorrenciasSolicitadas(AgendaPacienteForm form) {
        PeriodicidadeRecorrenciaAgendamento periodicidade = form.getPeriodicidadeRecorrencia();
        if (periodicidade == null || periodicidade == PeriodicidadeRecorrenciaAgendamento.NENHUMA) {
            return 1;
        }
        if (periodicidade != PeriodicidadeRecorrenciaAgendamento.SEMANAL) {
            throw new IllegalArgumentException("Periodicidade de recorrencia invalida");
        }
        Integer quantidadeSessoes = form.getQuantidadeSessoes();
        if (quantidadeSessoes == null || quantidadeSessoes < 1) {
            throw new IllegalArgumentException("Quantidade de sessoes deve ser maior que zero");
        }
        if (quantidadeSessoes > 52) {
            throw new IllegalArgumentException("Quantidade de sessoes deve ser no maximo 52");
        }
        return quantidadeSessoes;
    }

    private AgendaEspecialidade resolverAgendaParaOcorrencia(Long unidadeId,
                                                             AgendaEspecialidade agendaBase,
                                                             LocalDate dataOcorrencia,
                                                             LocalTime horarioAtendimento,
                                                             List<String> avisos) {
        if (agendaBase.getDataAgenda().equals(dataOcorrencia)) {
            return agendaBase;
        }
        LocalTime horarioBase = horarioAtendimento == null ? agendaBase.getHoraInicio() : horarioAtendimento;
        Long especialidadeId = agendaBase.getEspecialidade() == null ? null : agendaBase.getEspecialidade().getId();
        return agendaRepository.listarAtivasPorContextoDataEHorario(
                        unidadeId,
                        agendaBase.getCargoColaborador().getId(),
                        especialidadeId,
                        dataOcorrencia,
                        horarioBase)
                .stream()
                .findFirst()
                .orElseGet(() -> {
                    avisos.add("Data " + dataOcorrencia.format(DATE_FORMATTER)
                            + ": nao existe agenda ativa para este cargo/especialidade/sessao.");
                    return null;
                });
    }

    private AgendaEspecialidadePaciente vincularPacienteEmAgenda(AgendaEspecialidade agenda,
                                                                 Paciente paciente,
                                                                 TipoVagaAgenda tipoVaga,
                                                                 LocalTime horarioAtendimento,
                                                                 String observacao,
                                                                 boolean recorrente) {
        ModoAgendaEspecialidade modoAgenda = modoAgenda(agenda);
        if (agendaPacienteRepository.existsByAgendaEspecialidadeIdAndPacienteIdAndStatusNot(
                agenda.getId(), paciente.getId(), StatusAgendamentoPaciente.CANCELADO)) {
            throw new IllegalArgumentException("Paciente ja vinculado nesta agenda");
        }
        if (modoAgenda == ModoAgendaEspecialidade.HORARIO_SESSAO && horarioAtendimento == null) {
            throw new IllegalArgumentException("Selecione um horario disponivel para agenda por horario");
        }
        AgendaEspecialidadeSlot slot = horarioAtendimento == null
                ? obterPrimeiroSlotLivre(agenda)
                : obterSlotLivre(agenda, horarioAtendimento);

        long totalAgendado = agendaPacienteRepository.countByAgendaEspecialidadeId(agenda.getId());
        if (totalAgendado >= agenda.getVagasTotais()) {
            throw new IllegalArgumentException("Nao ha mais vagas disponiveis nesta agenda");
        }

        AgendaEspecialidadePaciente item = new AgendaEspecialidadePaciente();
        item.setAgendaEspecialidade(agenda);
        item.setPaciente(paciente);
        item.setAgendaSlot(slot);
        item.setTipoVaga(tipoVaga);
        item.setStatus(StatusAgendamentoPaciente.PENDENTE);
        item.setHoraAtendimento(slot.getDataHoraInicio().toLocalTime());
        item.setObservacao(normalizeUpper(observacao));
        item.setCriadoEm(LocalDateTime.now());
        item.setCriadoPor(usuarioAuditoriaService.usernameAtualOuSistema());
        item.setCriadoPorUsuario(usuarioAuditoriaService.usuarioAtual().orElse(null));
        AgendaEspecialidadePaciente salvo = agendaPacienteRepository.save(item);

        ocuparSlot(slot);
        registrarHistorico(
                salvo,
                recorrente ? AcaoAgendamentoHistorico.VINCULO_RECORRENTE : AcaoAgendamentoHistorico.VINCULO,
                null,
                StatusAgendamentoPaciente.PENDENTE,
                null,
                agenda,
                null,
                slot,
                null,
                slot.getDataHoraInicio().toLocalTime(),
                recorrente ? "Vinculo recorrente semanal" : "Vinculo inicial");
        return salvo;
    }

    @Transactional
    public void removerPaciente(Long unidadeId, Long agendaId, Long agendaPacienteId) {
        cancelarPaciente(unidadeId, agendaId, agendaPacienteId, "Cancelado manualmente");
    }

    @Transactional
    public void confirmarPaciente(Long unidadeId, Long agendaId, Long agendaPacienteId) {
        alterarStatus(unidadeId, agendaId, agendaPacienteId, StatusAgendamentoPaciente.CONFIRMADO, AcaoAgendamentoHistorico.CONFIRMACAO, null);
    }

    @Transactional
    public void marcarFalta(Long unidadeId, Long agendaId, Long agendaPacienteId) {
        alterarStatus(unidadeId, agendaId, agendaPacienteId, StatusAgendamentoPaciente.FALTOU, AcaoAgendamentoHistorico.FALTA, null);
    }

    @Transactional
    public void marcarAtendido(Long unidadeId, Long agendaId, Long agendaPacienteId) {
        alterarStatus(unidadeId, agendaId, agendaPacienteId, StatusAgendamentoPaciente.ATENDIDO, AcaoAgendamentoHistorico.ATENDIMENTO, null);
    }

    @Transactional
    public void cancelarPaciente(Long unidadeId, Long agendaId, Long agendaPacienteId, String observacao) {
        alterarStatus(unidadeId, agendaId, agendaPacienteId, StatusAgendamentoPaciente.CANCELADO, AcaoAgendamentoHistorico.CANCELAMENTO, observacao);
    }

    @Transactional
    public void reagendarPaciente(Long unidadeId, Long agendaId, Long agendaPacienteId, AgendaReagendamentoForm form) {
        AgendaEspecialidade agendaOrigem = buscar(unidadeId, agendaId);
        AgendaEspecialidadePaciente item = buscarAgendaPacienteDaAgenda(agendaOrigem, agendaPacienteId);
        if (item.getStatus() == StatusAgendamentoPaciente.CANCELADO) {
            throw new IllegalArgumentException("Nao e possivel reagendar um agendamento cancelado");
        }

        AgendaEspecialidade agendaDestino = buscar(unidadeId, form.getAgendaDestinoId());
        if (!mesmoContextoAssistencial(agendaOrigem, agendaDestino)) {
            throw new IllegalArgumentException("Reagendamento so e permitido para agenda do mesmo cargo/especialidade");
        }

        LocalTime horarioDestino = form.getHorarioDestino();
        if (horarioDestino == null) {
            throw new IllegalArgumentException("Horario de destino e obrigatorio");
        }
        AgendaEspecialidadeSlot slotDestino = obterSlotLivre(agendaDestino, horarioDestino);
        AgendaEspecialidadeSlot slotOrigem = item.getAgendaSlot();

        if (slotOrigem.getId().equals(slotDestino.getId())) {
            throw new IllegalArgumentException("Selecione um horario diferente do atual");
        }

        StatusAgendamentoPaciente statusAnterior = item.getStatus();
        AgendaEspecialidade agendaAnterior = item.getAgendaEspecialidade();
        LocalTime horarioAnterior = item.getHoraAtendimento();

        item.setAgendaEspecialidade(agendaDestino);
        item.setAgendaSlot(slotDestino);
        item.setHoraAtendimento(horarioDestino);
        item.setStatus(StatusAgendamentoPaciente.PENDENTE);
        if (form.getObservacao() != null && !form.getObservacao().isBlank()) {
            item.setObservacao(normalizeUpper(form.getObservacao()));
        }
        agendaPacienteRepository.save(item);

        ocuparSlot(slotDestino);
        atualizarStatusSlot(slotOrigem);

        registrarHistorico(
                item,
                AcaoAgendamentoHistorico.REAGENDAMENTO,
                statusAnterior,
                StatusAgendamentoPaciente.PENDENTE,
                agendaAnterior,
                agendaDestino,
                slotOrigem,
                slotDestino,
                horarioAnterior,
                horarioDestino,
                form.getObservacao());
    }

    @Transactional(readOnly = true)
    public long contarPacientesAgendados(Long unidadeId, Long agendaId) {
        buscar(unidadeId, agendaId);
        return agendaPacienteRepository.countByAgendaEspecialidadeId(agendaId);
    }

    @Transactional(readOnly = true)
    public long contarPacientesRetornoAgendados(Long unidadeId, Long agendaId) {
        buscar(unidadeId, agendaId);
        return agendaPacienteRepository.countByAgendaEspecialidadeIdAndTipoVaga(agendaId, TipoVagaAgenda.RETORNO);
    }

    @Transactional(readOnly = true)
    public List<LocalTime> listarHorariosDisponiveis(Long unidadeId, Long agendaId) {
        buscar(unidadeId, agendaId);
        List<AgendaEspecialidadeSlot> slots = agendaSlotRepository.findByAgendaEspecialidadeIdOrderByDataHoraInicioAsc(agendaId);
        if (slots.isEmpty()) {
            return List.of();
        }
        Set<Long> slotsOcupados = new LinkedHashSet<>(agendaPacienteRepository.listarSlotsOcupadosPorAgenda(agendaId));
        return slots.stream()
                .filter(slot -> slot.getStatus() == StatusAgendaSlot.LIVRE)
                .filter(slot -> !slotsOcupados.contains(slot.getId()))
                .map(slot -> slot.getDataHoraInicio().toLocalTime())
                .toList();
    }

    @Transactional(readOnly = true)
    public List<LocalTime> listarHorariosGrade(Long unidadeId, Long agendaId) {
        AgendaEspecialidade agenda = buscar(unidadeId, agendaId);
        List<AgendaEspecialidadeSlot> slots = agendaSlotRepository.findByAgendaEspecialidadeIdOrderByDataHoraInicioAsc(agendaId);
        if (slots.isEmpty()) {
            return gerarHorariosDisponiveis(agenda.getHoraInicio(), agenda.getHoraFim(), agenda.getIntervaloMinutos());
        }
        return slots.stream()
                .filter(slot -> slot.getStatus() != StatusAgendaSlot.BLOQUEADO)
                .map(slot -> slot.getDataHoraInicio().toLocalTime())
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AgendaEspecialidadePacienteHistorico> listarHistoricoAgendamento(Long agendaPacienteId) {
        return agendaPacienteHistoricoRepository.listarPorAgendaPaciente(agendaPacienteId);
    }

    @Transactional(readOnly = true)
    public List<AgendaEspecialidade> listarAgendasDoContexto(Long unidadeId,
                                                              Long cargoColaboradorId,
                                                              Long especialidadeId,
                                                              LocalDate dataInicio,
                                                              LocalDate dataFim) {
        return agendaRepository.listarPorFiltros(unidadeId, cargoColaboradorId, especialidadeId, dataInicio, dataFim);
    }

    @Transactional(readOnly = true)
    public List<AgendaMapaMensalItemDto> listarMapaMensal(Long unidadeId,
                                                           Long cargoColaboradorId,
                                                           Long especialidadeId,
                                                           YearMonth competencia) {
        validarUnidadeComAgendamento(unidadeId);
        if (cargoColaboradorId == null || competencia == null) {
            return List.of();
        }
        LocalDate inicio = competencia.atDay(1);
        LocalDate fim = competencia.atEndOfMonth();
        return agendaRepository.listarPorContextoNoPeriodo(unidadeId, cargoColaboradorId, especialidadeId, inicio, fim)
                .stream()
                .map(agenda -> new AgendaMapaMensalItemDto(
                        agenda.getId(),
                        agenda.getDataAgenda().getDayOfMonth(),
                        agenda.getHoraInicio().format(TIME_FORMATTER),
                        agenda.getHoraFim().format(TIME_FORMATTER),
                        agenda.getModoAgenda() == null ? ModoAgendaEspecialidade.CAPACIDADE_TURNO.name() : agenda.getModoAgenda().name(),
                        agenda.getVagasTotais(),
                        agenda.isAtivo()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AgendaCalendarioEventoDto> listarEventosCalendario(Long unidadeId,
                                                                    Long cargoColaboradorId,
                                                                    Long especialidadeId,
                                                                    LocalDate dataInicio,
                                                                    LocalDate dataFim) {
        validarUnidadeComAgendamento(unidadeId);
        LocalDate inicio = dataInicio == null ? LocalDate.now() : dataInicio;
        LocalDate fim = dataFim == null ? inicio.plusDays(30) : dataFim;
        if (fim.isBefore(inicio)) {
            LocalDate tmp = inicio;
            inicio = fim;
            fim = tmp;
        }
        LocalDateTime inicioDt = inicio.atStartOfDay();
        LocalDateTime fimDt = fim.plusDays(1).atStartOfDay();

        return agendaPacienteRepository.listarParaCalendario(unidadeId, cargoColaboradorId, especialidadeId, inicioDt, fimDt)
                .stream()
                .map(item -> {
                    ModoAgendaEspecialidade modoAgenda = modoAgenda(item.getAgendaEspecialidade());
                    LocalDateTime inicioSessao;
                    LocalDateTime fimSessao;
                    if (modoAgenda == ModoAgendaEspecialidade.HORARIO_SESSAO) {
                        inicioSessao = item.getAgendaSlot().getDataHoraInicio();
                        fimSessao = item.getAgendaSlot().getDataHoraFim();
                    } else {
                        inicioSessao = LocalDateTime.of(
                                item.getAgendaEspecialidade().getDataAgenda(),
                                item.getAgendaEspecialidade().getHoraInicio());
                        fimSessao = inicioSessao.plusMinutes(30);
                    }
                    return new AgendaCalendarioEventoDto(
                            item.getId(),
                            item.getAgendaEspecialidade().getId(),
                            item.getAgendaEspecialidade().getCargoColaborador().getDescricao(),
                            descricaoEspecialidade(item.getAgendaEspecialidade().getEspecialidade()),
                            item.getPaciente().getNomeExibicao(),
                            item.getTipoVaga().name(),
                            item.getStatus(),
                            inicioSessao,
                            fimSessao,
                            item.getObservacao());
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AgendaCalendarioLivreDto> listarHorariosLivresParaCalendario(Long unidadeId,
                                                                              Long cargoColaboradorId,
                                                                              Long especialidadeId,
                                                                              LocalDate dataInicio,
                                                                              LocalDate dataFim) {
        validarUnidadeComAgendamento(unidadeId);
        if (cargoColaboradorId == null) {
            return List.of();
        }
        LocalDate inicio = dataInicio == null ? LocalDate.now() : dataInicio;
        LocalDate fim = dataFim == null ? inicio.plusDays(30) : dataFim;
        if (fim.isBefore(inicio)) {
            LocalDate tmp = inicio;
            inicio = fim;
            fim = tmp;
        }
        LocalDateTime inicioDt = inicio.atStartOfDay();
        LocalDateTime fimDt = fim.plusDays(1).atStartOfDay();

        return agendaSlotRepository.listarCalendario(unidadeId, cargoColaboradorId, especialidadeId, inicioDt, fimDt)
                .stream()
                .filter(slot -> slot.getStatus() == StatusAgendaSlot.LIVRE)
                .map(slot -> new AgendaCalendarioLivreDto(
                        slot.getId(),
                        slot.getAgendaEspecialidade().getId(),
                        slot.getAgendaEspecialidade().getCargoColaborador().getDescricao(),
                        descricaoEspecialidade(slot.getAgendaEspecialidade().getEspecialidade()),
                        slot.getDataHoraInicio(),
                        slot.getDataHoraFim()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AgendaCalendarioSessaoDto> listarSessoesCalendario(Long unidadeId,
                                                                    Long cargoColaboradorId,
                                                                    Long especialidadeId,
                                                                    LocalDate dataInicio,
                                                                    LocalDate dataFim) {
        validarUnidadeComAgendamento(unidadeId);
        LocalDate inicio = dataInicio == null ? LocalDate.now() : dataInicio;
        LocalDate fim = dataFim == null ? inicio.plusDays(30) : dataFim;
        if (fim.isBefore(inicio)) {
            LocalDate tmp = inicio;
            inicio = fim;
            fim = tmp;
        }

        List<AgendaEspecialidade> agendas = agendaRepository.listarPorFiltros(unidadeId, cargoColaboradorId, especialidadeId, inicio, fim);
        if (agendas.isEmpty()) {
            return List.of();
        }
        Map<Long, Long> agendadosPorAgendaId = calcularAgendadosAtivosPorAgenda(agendas);
        return agendas.stream()
                .map(agenda -> {
                    long ocupadas = agendadosPorAgendaId.getOrDefault(agenda.getId(), 0L);
                    long livres = Math.max(agenda.getVagasTotais() - ocupadas, 0);
                    LocalDateTime inicioSessao = LocalDateTime.of(agenda.getDataAgenda(), agenda.getHoraInicio());
                    LocalDateTime fimSessao = modoAgenda(agenda) == ModoAgendaEspecialidade.HORARIO_SESSAO
                            ? LocalDateTime.of(agenda.getDataAgenda(), agenda.getHoraFim())
                            : inicioSessao.plusMinutes(30);
                    return new AgendaCalendarioSessaoDto(
                            agenda.getId(),
                            agenda.getCargoColaborador().getDescricao(),
                            descricaoEspecialidade(agenda.getEspecialidade()),
                            inicioSessao,
                            fimSessao,
                            modoAgenda(agenda).name(),
                            agenda.getVagasTotais(),
                            ocupadas,
                            livres);
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public AgendaEspecialidadeForm toForm(AgendaEspecialidade item) {
        AgendaEspecialidadeForm form = new AgendaEspecialidadeForm();
        form.setCargoColaboradorId(item.getCargoColaborador().getId());
        form.setEspecialidadeId(item.getEspecialidade() == null ? null : item.getEspecialidade().getId());
        form.setDataAgenda(item.getDataAgenda());
        form.setCompetencia(item.getDataAgenda().format(DateTimeFormatter.ofPattern("yyyy-MM")));
        form.setDiasSelecionados(Set.of(item.getDataAgenda().getDayOfMonth()));
        form.setModoAgenda(modoAgenda(item));
        form.setHoraInicio(item.getHoraInicio());
        form.setHoraFim(item.getHoraFim());
        form.setIntervaloMinutos(item.getIntervaloMinutos());
        form.setVagasTotais(item.getVagasTotais());
        form.setObservacao(item.getObservacao());
        form.setAtivo(item.isAtivo());
        return form;
    }

    @Transactional(readOnly = true)
    public boolean unidadePermiteAgendamento(Long unidadeId) {
        return unidadeConfigFluxoRepository.findById(unidadeId)
                .map(UnidadeConfigFluxo::isPermiteAgendamento)
                .orElse(false);
    }

    private CargoColaborador resolveCargoColaborador(Long cargoColaboradorId) {
        if (cargoColaboradorId == null) {
            throw new IllegalArgumentException("Cargo assistencial e obrigatorio");
        }
        CargoColaborador cargo = cargoColaboradorRepository.findById(cargoColaboradorId)
                .orElseThrow(() -> new IllegalArgumentException("Cargo de colaborador nao encontrado"));
        if (!cargo.isAtivo()) {
            throw new IllegalArgumentException("Cargo de colaborador inativo");
        }
        String tipoCargo = cargo.getTipoCargo() == null ? null : cargo.getTipoCargo().getCodigo();
        if (tipoCargo == null || !"ASSISTENCIAL".equalsIgnoreCase(tipoCargo)) {
            throw new IllegalArgumentException("Agenda so pode ser criada para cargo assistencial");
        }
        return cargo;
    }

    private Especialidade resolveEspecialidadeOpcional(Long especialidadeId) {
        if (especialidadeId == null) {
            return null;
        }
        Especialidade especialidade = especialidadeRepository.findById(especialidadeId)
                .orElseThrow(() -> new IllegalArgumentException("Especialidade nao encontrada"));
        if (!especialidade.isAtivo()) {
            throw new IllegalArgumentException("Especialidade inativa");
        }
        return especialidade;
    }

    private void validarCompatibilidadeCargoEspecialidade(CargoColaborador cargoColaborador, Especialidade especialidade) {
        boolean exigeEspecialidade = cargoColaborador.isExigeEspecialidadeAgendamento()
                || "MEDICO".equalsIgnoreCase(cargoColaborador.getCodigo());
        if (exigeEspecialidade && especialidade == null) {
            throw new IllegalArgumentException("Especialidade obrigatoria para o cargo selecionado");
        }
        if (especialidade == null) {
            return;
        }
        boolean permitido = cargoColaboradorEspecialidadeRepository.existsAtivaByCargoEEspecialidade(
                cargoColaborador.getId(),
                especialidade.getId());
        if (!permitido) {
            throw new IllegalArgumentException("Especialidade nao permitida para o cargo selecionado");
        }
    }

    private static boolean mesmoContextoAssistencial(AgendaEspecialidade origem, AgendaEspecialidade destino) {
        Long cargoOrigemId = origem.getCargoColaborador() == null ? null : origem.getCargoColaborador().getId();
        Long cargoDestinoId = destino.getCargoColaborador() == null ? null : destino.getCargoColaborador().getId();
        if (cargoOrigemId == null || cargoDestinoId == null || !cargoOrigemId.equals(cargoDestinoId)) {
            return false;
        }
        Long especialidadeOrigemId = origem.getEspecialidade() == null ? null : origem.getEspecialidade().getId();
        Long especialidadeDestinoId = destino.getEspecialidade() == null ? null : destino.getEspecialidade().getId();
        if (especialidadeOrigemId == null && especialidadeDestinoId == null) {
            return true;
        }
        return especialidadeOrigemId != null && especialidadeOrigemId.equals(especialidadeDestinoId);
    }

    private static String descricaoEspecialidade(Especialidade especialidade) {
        return especialidade == null ? "NAO SE APLICA" : especialidade.getDescricao();
    }

    private void validarFormulario(Long unidadeId,
                                   AgendaEspecialidadeForm form,
                                   CargoColaborador cargoColaborador,
                                   Especialidade especialidade,
                                   Long idIgnorar,
                                   LocalDate dataAgenda) {
        if (dataAgenda == null) {
            throw new IllegalArgumentException("Data da agenda e obrigatoria");
        }
        ModoAgendaEspecialidade modoAgenda = modoAgenda(form.getModoAgenda());
        if (form.getHoraInicio() == null) {
            throw new IllegalArgumentException("Hora inicial e obrigatoria");
        }

        LocalTime horaFimValidacao;
        Integer vagasTotais;
        Integer intervaloMinutos;
        if (modoAgenda == ModoAgendaEspecialidade.HORARIO_SESSAO) {
            if (form.getIntervaloMinutos() == null || form.getIntervaloMinutos() <= 0) {
                throw new IllegalArgumentException("Duracao da sessao e obrigatoria para agenda por horario");
            }
            if (form.getVagasTotais() == null || form.getVagasTotais() <= 0) {
                throw new IllegalArgumentException("Quantidade de vagas deve ser maior que zero");
            }
            intervaloMinutos = form.getIntervaloMinutos();
            vagasTotais = form.getVagasTotais();
            horaFimValidacao = calcularHoraFimPorDuracao(form.getHoraInicio(), intervaloMinutos, vagasTotais);
            if (!horaFimValidacao.isAfter(form.getHoraInicio())) {
                throw new IllegalArgumentException("Configuracao de vagas e duracao excede o mesmo dia");
            }
            long duracaoTotal = ChronoUnit.MINUTES.between(form.getHoraInicio(), horaFimValidacao);
            if (duracaoTotal != (long) vagasTotais * intervaloMinutos) {
                throw new IllegalArgumentException("Configuracao invalida para calcular horario final");
            }
        } else {
            if (form.getVagasTotais() == null || form.getVagasTotais() <= 0) {
                throw new IllegalArgumentException("Quantidade de vagas deve ser maior que zero");
            }
            vagasTotais = form.getVagasTotais();
            intervaloMinutos = 1;
            horaFimValidacao = calcularHoraFimTecnica(form.getHoraInicio(), vagasTotais);
        }

        form.setModoAgenda(modoAgenda);
        form.setVagasTotais(vagasTotais);
        form.setIntervaloMinutos(intervaloMinutos);
        form.setHoraFim(horaFimValidacao);

        if (agendaRepository.existsConflitoHorario(
                unidadeId,
                cargoColaborador.getId(),
                especialidade == null ? null : especialidade.getId(),
                dataAgenda,
                idIgnorar,
                form.getHoraInicio(),
                horaFimValidacao)) {
            throw new IllegalArgumentException("Ja existe agenda com conflito de horario para este cargo/especialidade/data");
        }
        if (idIgnorar != null) {
            long totalAgendado = agendaPacienteRepository.countByAgendaEspecialidadeId(idIgnorar);
            if (vagasTotais < totalAgendado) {
                throw new IllegalArgumentException("Vagas totais nao pode ser menor que pacientes ja vinculados");
            }
        }
    }

    private void apply(AgendaEspecialidade item, AgendaEspecialidadeForm form, LocalDate dataAgenda) {
        ModoAgendaEspecialidade modoAgenda = modoAgenda(form.getModoAgenda());
        item.setDataAgenda(dataAgenda);
        item.setModoAgenda(modoAgenda);
        item.setHoraInicio(form.getHoraInicio());
        if (modoAgenda == ModoAgendaEspecialidade.HORARIO_SESSAO) {
            item.setHoraFim(calcularHoraFimPorDuracao(form.getHoraInicio(), form.getIntervaloMinutos(), form.getVagasTotais()));
            item.setIntervaloMinutos(form.getIntervaloMinutos());
            item.setVagasTotais(form.getVagasTotais());
        } else {
            item.setHoraFim(calcularHoraFimTecnica(form.getHoraInicio(), form.getVagasTotais()));
            item.setIntervaloMinutos(1);
            item.setVagasTotais(form.getVagasTotais());
        }
        item.setVagasRetorno(0);
        item.setObservacao(normalizeUpper(form.getObservacao()));
        item.setAtivo(form.isAtivo());
    }

    private List<LocalDate> calcularDatasParaCriacaoMensal(AgendaEspecialidadeForm form) {
        YearMonth competencia = parseCompetencia(form.getCompetencia());
        if (form.getDiasSelecionados() == null || form.getDiasSelecionados().isEmpty()) {
            throw new IllegalArgumentException("Selecione pelo menos um dia do mes para criar as agendas");
        }
        List<LocalDate> datas = new ArrayList<>();
        for (Integer dia : form.getDiasSelecionados()) {
            if (dia == null) {
                continue;
            }
            if (dia < 1 || dia > competencia.lengthOfMonth()) {
                throw new IllegalArgumentException("Dia invalido para a competencia selecionada: " + dia);
            }
            datas.add(competencia.atDay(dia));
        }
        if (datas.isEmpty()) {
            throw new IllegalArgumentException("Nenhum dia valido para criar agenda no periodo informado");
        }
        datas.sort(LocalDate::compareTo);
        return datas;
    }

    private static LocalTime calcularHoraFimTecnica(LocalTime horaInicio, Integer vagasTotais) {
        if (horaInicio == null || vagasTotais == null || vagasTotais <= 0) {
            return horaInicio;
        }
        return horaInicio.plusMinutes(vagasTotais);
    }

    private static LocalTime calcularHoraFimPorDuracao(LocalTime horaInicio,
                                                       Integer intervaloMinutos,
                                                       Integer vagasTotais) {
        if (horaInicio == null
                || intervaloMinutos == null || intervaloMinutos <= 0
                || vagasTotais == null || vagasTotais <= 0) {
            return horaInicio;
        }
        long totalMinutos = (long) intervaloMinutos * vagasTotais;
        return horaInicio.plusMinutes(totalMinutos);
    }

    private static ModoAgendaEspecialidade modoAgenda(ModoAgendaEspecialidade modo) {
        return modo == null ? ModoAgendaEspecialidade.CAPACIDADE_TURNO : modo;
    }

    private static ModoAgendaEspecialidade modoAgenda(AgendaEspecialidade agenda) {
        if (agenda == null || agenda.getModoAgenda() == null) {
            return ModoAgendaEspecialidade.CAPACIDADE_TURNO;
        }
        return agenda.getModoAgenda();
    }

    private YearMonth parseCompetencia(String competencia) {
        String valor = normalize(competencia);
        if (valor == null) {
            throw new IllegalArgumentException("Competencia (mes/ano) e obrigatoria");
        }
        try {
            return YearMonth.parse(valor, DateTimeFormatter.ofPattern("yyyy-MM"));
        } catch (DateTimeException ex) {
            throw new IllegalArgumentException("Competencia invalida. Use o formato AAAA-MM.");
        }
    }

    private void sincronizarSlots(AgendaEspecialidade agenda) {
        LinkedHashMap<LocalDateTime, LocalDateTime> grade = gerarGradeDataHora(agenda);
        List<AgendaEspecialidadeSlot> existentes = agendaSlotRepository.findByAgendaEspecialidadeIdOrderByDataHoraInicioAsc(agenda.getId());
        Map<LocalDateTime, AgendaEspecialidadeSlot> porInicio = new LinkedHashMap<>();
        for (AgendaEspecialidadeSlot slot : existentes) {
            porInicio.put(slot.getDataHoraInicio(), slot);
        }

        List<AgendaEspecialidadeSlot> novos = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        for (Map.Entry<LocalDateTime, LocalDateTime> entry : grade.entrySet()) {
            if (!porInicio.containsKey(entry.getKey())) {
                AgendaEspecialidadeSlot slot = new AgendaEspecialidadeSlot();
                slot.setAgendaEspecialidade(agenda);
                slot.setDataHoraInicio(entry.getKey());
                slot.setDataHoraFim(entry.getValue());
                slot.setStatus(StatusAgendaSlot.LIVRE);
                slot.setCriadoEm(now);
                slot.setAtualizadoEm(now);
                novos.add(slot);
            }
        }
        if (!novos.isEmpty()) {
            agendaSlotRepository.saveAll(novos);
            existentes = agendaSlotRepository.findByAgendaEspecialidadeIdOrderByDataHoraInicioAsc(agenda.getId());
        }

        List<AgendaEspecialidadeSlot> alterados = new ArrayList<>();
        for (AgendaEspecialidadeSlot slot : existentes) {
            LocalDateTime fimEsperado = grade.get(slot.getDataHoraInicio());
            boolean possuiAgendamentoAtivo = agendaPacienteRepository.existsByAgendaSlotIdAndStatusNot(
                    slot.getId(), StatusAgendamentoPaciente.CANCELADO);
            boolean mudou = false;
            if (fimEsperado == null) {
                if (possuiAgendamentoAtivo) {
                    throw new IllegalArgumentException("Nao foi possivel ajustar grade: existe agendamento ativo fora da nova configuracao");
                }
                if (slot.getStatus() != StatusAgendaSlot.BLOQUEADO) {
                    slot.setStatus(StatusAgendaSlot.BLOQUEADO);
                    mudou = true;
                }
            } else {
                if (!fimEsperado.equals(slot.getDataHoraFim())) {
                    slot.setDataHoraFim(fimEsperado);
                    mudou = true;
                }
                StatusAgendaSlot statusEsperado = possuiAgendamentoAtivo ? StatusAgendaSlot.OCUPADO : StatusAgendaSlot.LIVRE;
                if (slot.getStatus() != statusEsperado) {
                    slot.setStatus(statusEsperado);
                    mudou = true;
                }
            }
            if (mudou) {
                slot.setAtualizadoEm(now);
                alterados.add(slot);
            }
        }
        if (!alterados.isEmpty()) {
            agendaSlotRepository.saveAll(alterados);
        }
    }

    private AgendaEspecialidadePaciente buscarAgendaPacienteDaAgenda(AgendaEspecialidade agenda, Long agendaPacienteId) {
        AgendaEspecialidadePaciente item = agendaPacienteRepository.buscarPorIdComRelacionamentos(agendaPacienteId)
                .orElseThrow(() -> new IllegalArgumentException("Vinculo de paciente nao encontrado"));
        if (!item.getAgendaEspecialidade().getId().equals(agenda.getId())) {
            throw new IllegalArgumentException("Vinculo nao pertence a agenda informada");
        }
        return item;
    }

    private AgendaEspecialidadeSlot obterSlotLivre(AgendaEspecialidade agenda, LocalTime horarioAtendimento) {
        LocalDateTime inicio = LocalDateTime.of(agenda.getDataAgenda(), horarioAtendimento);
        AgendaEspecialidadeSlot slot = agendaSlotRepository.findByAgendaEspecialidadeIdAndDataHoraInicio(agenda.getId(), inicio)
                .orElseThrow(() -> new IllegalArgumentException("Horario invalido para a grade desta agenda"));
        if (slot.getStatus() == StatusAgendaSlot.BLOQUEADO) {
            throw new IllegalArgumentException("Horario bloqueado para esta agenda");
        }
        if (agendaPacienteRepository.existsByAgendaSlotIdAndStatusNot(slot.getId(), StatusAgendamentoPaciente.CANCELADO)
                || slot.getStatus() == StatusAgendaSlot.OCUPADO) {
            throw new IllegalArgumentException("Horario ja ocupado nesta agenda");
        }
        return slot;
    }

    private AgendaEspecialidadeSlot obterPrimeiroSlotLivre(AgendaEspecialidade agenda) {
        return agendaSlotRepository.findByAgendaEspecialidadeIdOrderByDataHoraInicioAsc(agenda.getId())
                .stream()
                .filter(slot -> slot.getStatus() != StatusAgendaSlot.BLOQUEADO)
                .filter(slot -> !agendaPacienteRepository.existsByAgendaSlotIdAndStatusNot(slot.getId(), StatusAgendamentoPaciente.CANCELADO))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Nao ha mais vagas disponiveis nesta agenda"));
    }

    private void alterarStatus(Long unidadeId,
                               Long agendaId,
                               Long agendaPacienteId,
                               StatusAgendamentoPaciente novoStatus,
                               AcaoAgendamentoHistorico acao,
                               String observacao) {
        AgendaEspecialidade agenda = buscar(unidadeId, agendaId);
        AgendaEspecialidadePaciente item = buscarAgendaPacienteDaAgenda(agenda, agendaPacienteId);
        if (item.getStatus() == novoStatus) {
            return;
        }

        StatusAgendamentoPaciente statusAnterior = item.getStatus();
        AgendaEspecialidadeSlot slot = item.getAgendaSlot();
        item.setStatus(novoStatus);
        agendaPacienteRepository.save(item);

        if (novoStatus == StatusAgendamentoPaciente.CANCELADO) {
            atualizarStatusSlot(slot, item.getId());
        } else {
            ocuparSlot(slot);
        }

        registrarHistorico(
                item,
                acao,
                statusAnterior,
                novoStatus,
                agenda,
                agenda,
                slot,
                slot,
                item.getHoraAtendimento(),
                item.getHoraAtendimento(),
                observacao);
    }

    private void ocuparSlot(AgendaEspecialidadeSlot slot) {
        if (slot.getStatus() != StatusAgendaSlot.OCUPADO) {
            slot.setStatus(StatusAgendaSlot.OCUPADO);
            slot.setAtualizadoEm(LocalDateTime.now());
            agendaSlotRepository.save(slot);
        }
    }

    private void atualizarStatusSlot(AgendaEspecialidadeSlot slot) {
        atualizarStatusSlot(slot, null);
    }

    private void atualizarStatusSlot(AgendaEspecialidadeSlot slot, Long agendaPacienteIgnorarId) {
        boolean possuiAgendamentoAtivo = agendaPacienteIgnorarId == null
                ? agendaPacienteRepository.existsByAgendaSlotIdAndStatusNot(
                slot.getId(), StatusAgendamentoPaciente.CANCELADO)
                : agendaPacienteRepository.existsByAgendaSlotIdAndIdNotAndStatusNot(
                slot.getId(), agendaPacienteIgnorarId, StatusAgendamentoPaciente.CANCELADO);
        StatusAgendaSlot novoStatus;
        if (possuiAgendamentoAtivo) {
            novoStatus = StatusAgendaSlot.OCUPADO;
        } else if (slot.getStatus() == StatusAgendaSlot.BLOQUEADO) {
            novoStatus = StatusAgendaSlot.BLOQUEADO;
        } else {
            novoStatus = StatusAgendaSlot.LIVRE;
        }
        if (slot.getStatus() != novoStatus) {
            slot.setStatus(novoStatus);
            slot.setAtualizadoEm(LocalDateTime.now());
            agendaSlotRepository.save(slot);
        }
    }

    private void registrarHistorico(AgendaEspecialidadePaciente item,
                                    AcaoAgendamentoHistorico acao,
                                    StatusAgendamentoPaciente statusAnterior,
                                    StatusAgendamentoPaciente statusNovo,
                                    AgendaEspecialidade agendaOrigem,
                                    AgendaEspecialidade agendaDestino,
                                    AgendaEspecialidadeSlot slotOrigem,
                                    AgendaEspecialidadeSlot slotDestino,
                                    LocalTime horarioOrigem,
                                    LocalTime horarioDestino,
                                    String observacao) {
        AgendaEspecialidadePacienteHistorico hist = new AgendaEspecialidadePacienteHistorico();
        hist.setAgendaPaciente(item);
        hist.setAcao(acao);
        hist.setStatusAnterior(statusAnterior);
        hist.setStatusNovo(statusNovo);
        hist.setAgendaOrigem(agendaOrigem);
        hist.setAgendaDestino(agendaDestino);
        hist.setSlotOrigem(slotOrigem);
        hist.setSlotDestino(slotDestino);
        hist.setHorarioOrigem(horarioOrigem);
        hist.setHorarioDestino(horarioDestino);
        hist.setObservacao(normalizeUpper(observacao));
        hist.setCriadoEm(LocalDateTime.now());
        hist.setCriadoPor(usuarioAuditoriaService.usernameAtualOuSistema());
        hist.setCriadoPorUsuario(usuarioAuditoriaService.usuarioAtual().orElse(null));
        agendaPacienteHistoricoRepository.save(hist);
    }

    private static List<LocalTime> gerarHorariosDisponiveis(LocalTime horaInicio, LocalTime horaFim, Integer intervaloMinutos) {
        if (horaInicio == null || horaFim == null || intervaloMinutos == null || intervaloMinutos <= 0 || !horaFim.isAfter(horaInicio)) {
            return List.of();
        }
        List<LocalTime> horarios = new ArrayList<>();
        LocalTime horarioAtual = horaInicio;
        while (horarioAtual.isBefore(horaFim)) {
            horarios.add(horarioAtual);
            horarioAtual = horarioAtual.plusMinutes(intervaloMinutos);
        }
        return horarios;
    }

    private static LinkedHashMap<LocalDateTime, LocalDateTime> gerarGradeDataHora(AgendaEspecialidade agenda) {
        LinkedHashMap<LocalDateTime, LocalDateTime> grade = new LinkedHashMap<>();
        for (LocalTime horario : gerarHorariosDisponiveis(agenda.getHoraInicio(), agenda.getHoraFim(), agenda.getIntervaloMinutos())) {
            LocalDateTime inicio = LocalDateTime.of(agenda.getDataAgenda(), horario);
            grade.put(inicio, inicio.plusMinutes(agenda.getIntervaloMinutos()));
        }
        return grade;
    }

    private void validarUnidadeComAgendamento(Long unidadeId) {
        if (!unidadePermiteAgendamento(unidadeId)) {
            throw new IllegalArgumentException("Agendamento nao habilitado para esta unidade");
        }
    }

    private static String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private static String normalizeUpper(String value) {
        String normalized = normalize(value);
        return normalized == null ? null : normalized.toUpperCase();
    }
}
