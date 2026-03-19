package br.com.his.care.scheduling.ui;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.his.access.context.UnidadeContext;
import br.com.his.access.repository.CargoColaboradorRepository;
import br.com.his.access.service.OperationalPermissionService;
import br.com.his.care.scheduling.dto.AgendaCalendarioLivreDto;
import br.com.his.care.scheduling.dto.AgendaEspecialidadeForm;
import br.com.his.care.scheduling.dto.AgendaCalendarioEventoDto;
import br.com.his.care.scheduling.dto.AgendaCalendarioSessaoDto;
import br.com.his.care.scheduling.dto.AgendaMapaMensalItemDto;
import br.com.his.care.scheduling.dto.AgendaPacienteForm;
import br.com.his.care.scheduling.dto.AgendaReagendamentoForm;
import br.com.his.care.scheduling.dto.PeriodicidadeRecorrenciaAgendamento;
import br.com.his.care.scheduling.model.AgendaEspecialidade;
import br.com.his.care.scheduling.model.Especialidade;
import br.com.his.care.scheduling.model.ModoAgendaEspecialidade;
import br.com.his.care.scheduling.model.StatusAgendamentoPaciente;
import br.com.his.care.scheduling.model.TipoVagaAgenda;
import br.com.his.care.scheduling.service.AgendaEspecialidadeService;
import br.com.his.care.scheduling.service.EspecialidadeAdminService;
import br.com.his.patient.dto.PacienteLookupOption;
import br.com.his.patient.service.PacienteService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/ui/agendamentos")
public class AgendaEspecialidadeController {

    private static final DateTimeFormatter COMPETENCIA_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");
    private static final DateTimeFormatter HORARIO_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private final AgendaEspecialidadeService agendaService;
    private final EspecialidadeAdminService especialidadeAdminService;
    private final CargoColaboradorRepository cargoColaboradorRepository;
    private final PacienteService pacienteService;
    private final UnidadeContext unidadeContext;
    private final OperationalPermissionService operationalPermissionService;

    public AgendaEspecialidadeController(AgendaEspecialidadeService agendaService,
                                         EspecialidadeAdminService especialidadeAdminService,
                                         CargoColaboradorRepository cargoColaboradorRepository,
                                         PacienteService pacienteService,
                                         UnidadeContext unidadeContext,
                                         OperationalPermissionService operationalPermissionService) {
        this.agendaService = agendaService;
        this.especialidadeAdminService = especialidadeAdminService;
        this.cargoColaboradorRepository = cargoColaboradorRepository;
        this.pacienteService = pacienteService;
        this.unidadeContext = unidadeContext;
        this.operationalPermissionService = operationalPermissionService;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) Long cargoColaboradorId,
                         @RequestParam(required = false) Long especialidadeId,
                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
                         Model model) {
        requirePermission();
        Long unidadeId = unidadeAtual();
        LocalDate inicio = dataInicio;
        LocalDate fim = dataFim;
        if (inicio != null && fim != null && fim.isBefore(inicio)) {
            LocalDate tmp = inicio;
            inicio = fim;
            fim = tmp;
        }

        boolean habilitado = agendaService.unidadePermiteAgendamento(unidadeId);
        model.addAttribute("agendamentoHabilitado", habilitado);
        List<AgendaEspecialidade> items;
        if (habilitado) {
            items = agendaService.listar(unidadeId, cargoColaboradorId, especialidadeId, inicio, fim);
        } else {
            items = java.util.List.of();
        }
        model.addAttribute("items", items);
        model.addAttribute("vagasDisponiveisPorAgenda", agendaService.calcularVagasDisponiveisPorAgendas(items));
        model.addAttribute("cargos", cargoColaboradorRepository.findAssistenciaisAtivosOrderByDescricaoAsc());
        model.addAttribute("especialidades", especialidadeAdminService.listarAtivas());
        model.addAttribute("cargoColaboradorSelecionadoId", cargoColaboradorId);
        model.addAttribute("especialidadeSelecionadaId", especialidadeId);
        model.addAttribute("dataInicio", inicio);
        model.addAttribute("dataFim", fim);
        return "pages/care/scheduling/agendas/list";
    }

    @GetMapping("/calendario")
    public String calendario(@RequestParam(required = false) Long cargoColaboradorId,
                             @RequestParam(required = false) Long especialidadeId,
                             @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
                             @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
                             @RequestParam(required = false, defaultValue = "false") boolean mostrarLivres,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        requirePermission();
        Long unidadeId = unidadeAtual();
        if (!agendaService.unidadePermiteAgendamento(unidadeId)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Agendamento nao habilitado para esta unidade");
            return "redirect:/ui/agendamentos";
        }

        LocalDate inicio = dataInicio == null ? LocalDate.now() : dataInicio;
        LocalDate fim = dataFim == null ? inicio.plusDays(30) : dataFim;
        if (fim.isBefore(inicio)) {
            LocalDate tmp = inicio;
            inicio = fim;
            fim = tmp;
        }

        Long cargoSelecionadoId = cargoColaboradorId;
        Long especialidadeSelecionadaId = cargoSelecionadoId == null ? null : especialidadeId;
        boolean filtroCalendarioAplicado = cargoSelecionadoId != null;

        model.addAttribute("cargos", cargoColaboradorRepository.findAssistenciaisAtivosOrderByDescricaoAsc());
        model.addAttribute("especialidades", especialidadeAdminService.listarAtivas());
        model.addAttribute("cargoColaboradorSelecionadoId", cargoSelecionadoId);
        model.addAttribute("especialidadeSelecionadaId", especialidadeSelecionadaId);
        model.addAttribute("dataInicio", inicio);
        model.addAttribute("dataFim", fim);
        model.addAttribute("mostrarLivres", mostrarLivres);
        model.addAttribute("filtroCalendarioAplicado", filtroCalendarioAplicado);
        model.addAttribute("pacientesDisponiveis", pacienteService.listarAtivosNaoMergeadosParaSelecao());
        model.addAttribute("tiposVaga", TipoVagaAgenda.values());
        model.addAttribute("periodicidadesRecorrencia", PeriodicidadeRecorrenciaAgendamento.values());

        List<AgendaCalendarioEventoDto> eventosCalendario = List.of();
        List<AgendaCalendarioSessaoDto> sessoesCalendario = List.of();
        List<AgendaCalendarioLivreDto> horariosLivresCalendario = List.of();
        long totalVagas = 0;
        long vagasOcupadas = 0;
        long vagasLivres = 0;

        if (filtroCalendarioAplicado) {
            eventosCalendario = agendaService.listarEventosCalendario(unidadeId, cargoSelecionadoId, especialidadeSelecionadaId, inicio, fim);
            sessoesCalendario = agendaService.listarSessoesCalendario(unidadeId, cargoSelecionadoId, especialidadeSelecionadaId, inicio, fim);
            horariosLivresCalendario = agendaService.listarHorariosLivresParaCalendario(unidadeId, cargoSelecionadoId, especialidadeSelecionadaId, inicio, fim);
            totalVagas = sessoesCalendario.stream().mapToLong(item -> item.getVagasTotais()).sum();
            vagasOcupadas = sessoesCalendario.stream().mapToLong(item -> item.getVagasOcupadas()).sum();
            vagasLivres = sessoesCalendario.stream().mapToLong(item -> item.getVagasLivres()).sum();
        }

        model.addAttribute("eventosCalendario", eventosCalendario);
        model.addAttribute("sessoesCalendario", sessoesCalendario);
        model.addAttribute("horariosLivresCalendario", horariosLivresCalendario);
        model.addAttribute("qtdPendente", contarStatus(eventosCalendario, StatusAgendamentoPaciente.PENDENTE));
        model.addAttribute("qtdConfirmado", contarStatus(eventosCalendario, StatusAgendamentoPaciente.CONFIRMADO));
        model.addAttribute("qtdAtendido", contarStatus(eventosCalendario, StatusAgendamentoPaciente.ATENDIDO));
        model.addAttribute("qtdFaltou", contarStatus(eventosCalendario, StatusAgendamentoPaciente.FALTOU));
        model.addAttribute("qtdCancelado", contarStatus(eventosCalendario, StatusAgendamentoPaciente.CANCELADO));
        model.addAttribute("totalVagasSessoes", totalVagas);
        model.addAttribute("vagasOcupadasSessoes", vagasOcupadas);
        model.addAttribute("vagasLivresSessoes", vagasLivres);
        return "pages/care/scheduling/agendas/calendario";
    }

    @GetMapping("/novo")
    public String novo(@RequestParam(required = false) String competencia,
                       Model model,
                       RedirectAttributes redirectAttributes) {
        requirePermission();
        Long unidadeId = unidadeAtual();
        if (!agendaService.unidadePermiteAgendamento(unidadeId)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Agendamento nao habilitado para esta unidade");
            return "redirect:/ui/agendamentos";
        }
        if (!model.containsAttribute("form")) {
            AgendaEspecialidadeForm form = new AgendaEspecialidadeForm();
            form.setDataAgenda(LocalDate.now());
            form.setCompetencia(normalizarCompetencia(competencia));
            form.setModoAgenda(ModoAgendaEspecialidade.CAPACIDADE_TURNO);
            form.setIntervaloMinutos(60);
            form.setVagasTotais(10);
            model.addAttribute("form", form);
        }
        model.addAttribute("modoEdicao", false);
        model.addAttribute("agendamentoHabilitado", true);
        AgendaEspecialidadeForm form = (AgendaEspecialidadeForm) model.getAttribute("form");
        if (form.getCompetencia() == null || form.getCompetencia().isBlank()) {
            form.setCompetencia(normalizarCompetencia(competencia));
        }
        popularCombosFormulario(model, form);
        return "pages/care/scheduling/agendas/form";
    }

    @PostMapping
    public String criar(@Valid @ModelAttribute("form") AgendaEspecialidadeForm form,
                        BindingResult bindingResult,
                        Model model,
                        RedirectAttributes redirectAttributes) {
        requirePermission();
        Long unidadeId = unidadeAtual();
        if (bindingResult.hasErrors()) {
            model.addAttribute("modoEdicao", false);
            model.addAttribute("agendamentoHabilitado", agendaService.unidadePermiteAgendamento(unidadeId));
            popularCombosFormulario(model, form);
            return "pages/care/scheduling/agendas/form";
        }
        try {
            int totalCriadas = agendaService.criar(unidadeId, form);
            String mensagem = totalCriadas == 1
                    ? "Agenda cadastrada com sucesso"
                    : totalCriadas + " agendas cadastradas com sucesso";
            redirectAttributes.addFlashAttribute("successMessage", mensagem);
            return "redirect:/ui/agendamentos";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("modoEdicao", false);
            model.addAttribute("agendamentoHabilitado", agendaService.unidadePermiteAgendamento(unidadeId));
            popularCombosFormulario(model, form);
            model.addAttribute("errorMessage", ex.getMessage());
            return "pages/care/scheduling/agendas/form";
        }
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        requirePermission();
        Long unidadeId = unidadeAtual();
        if (!agendaService.unidadePermiteAgendamento(unidadeId)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Agendamento nao habilitado para esta unidade");
            return "redirect:/ui/agendamentos";
        }
        AgendaEspecialidadeForm form = agendaService.toForm(agendaService.buscar(unidadeId, id));
        model.addAttribute("form", form);
        model.addAttribute("modoEdicao", true);
        model.addAttribute("itemId", id);
        model.addAttribute("agendamentoHabilitado", true);
        popularCombosFormulario(model, form);
        return "pages/care/scheduling/agendas/form";
    }

    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id,
                            @Valid @ModelAttribute("form") AgendaEspecialidadeForm form,
                            BindingResult bindingResult,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        requirePermission();
        Long unidadeId = unidadeAtual();
        if (bindingResult.hasErrors()) {
            model.addAttribute("modoEdicao", true);
            model.addAttribute("itemId", id);
            model.addAttribute("agendamentoHabilitado", agendaService.unidadePermiteAgendamento(unidadeId));
            popularCombosFormulario(model, form);
            return "pages/care/scheduling/agendas/form";
        }
        try {
            agendaService.atualizar(unidadeId, id, form);
            redirectAttributes.addFlashAttribute("successMessage", "Agenda atualizada com sucesso");
            return "redirect:/ui/agendamentos";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("modoEdicao", true);
            model.addAttribute("itemId", id);
            model.addAttribute("agendamentoHabilitado", agendaService.unidadePermiteAgendamento(unidadeId));
            popularCombosFormulario(model, form);
            model.addAttribute("errorMessage", ex.getMessage());
            return "pages/care/scheduling/agendas/form";
        }
    }

    @GetMapping("/especialidades-por-cargo/{cargoColaboradorId}")
    @ResponseBody
    public List<PacienteLookupOption> listarEspecialidadesPorCargo(@PathVariable Long cargoColaboradorId) {
        requirePermission();
        return agendaService.listarEspecialidadesAtivasPorCargo(cargoColaboradorId)
                .stream()
                .map(this::toOption)
                .toList();
    }

    @GetMapping("/mapa-mensal")
    @ResponseBody
    public List<AgendaMapaMensalItemDto> listarMapaMensal(@RequestParam(required = false) Long cargoColaboradorId,
                                                           @RequestParam(required = false) Long especialidadeId,
                                                           @RequestParam(required = false) String competencia) {
        requirePermission();
        if (cargoColaboradorId == null || competencia == null || competencia.isBlank()) {
            return List.of();
        }
        YearMonth competenciaSelecionada = parseCompetencia(competencia);
        return agendaService.listarMapaMensal(unidadeAtual(), cargoColaboradorId, especialidadeId, competenciaSelecionada);
    }

    @GetMapping("/{id}/horarios-disponiveis")
    @ResponseBody
    public List<String> listarHorariosDisponiveis(@PathVariable Long id) {
        requirePermission();
        return agendaService.listarHorariosDisponiveis(unidadeAtual(), id)
                .stream()
                .map(this::formatarHorario)
                .toList();
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        requirePermission();
        Long unidadeId = unidadeAtual();
        try {
            agendaService.excluir(unidadeId, id);
            redirectAttributes.addFlashAttribute("successMessage", "Agenda excluida com sucesso");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/ui/agendamentos";
    }

    @GetMapping("/{id}/pacientes")
    public String pacientes(@PathVariable Long id,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        requirePermission();
        Long unidadeId = unidadeAtual();
        if (!agendaService.unidadePermiteAgendamento(unidadeId)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Agendamento nao habilitado para esta unidade");
            return "redirect:/ui/agendamentos";
        }

        if (!model.containsAttribute("pacienteForm")) {
            model.addAttribute("pacienteForm", new AgendaPacienteForm());
        }
        popularTelaPacientes(model, unidadeId, id);
        return "pages/care/scheduling/agendas/pacientes";
    }

    @PostMapping("/{id}/pacientes")
    public String vincularPaciente(@PathVariable Long id,
                                   @Valid @ModelAttribute("pacienteForm") AgendaPacienteForm form,
                                   BindingResult bindingResult,
                                   Model model,
                                   @RequestParam(required = false) String origem,
                                   @RequestParam(required = false) Long cargoColaboradorId,
                                   @RequestParam(required = false) Long especialidadeId,
                                   @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
                                   @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
                                   @RequestParam(required = false, defaultValue = "false") boolean mostrarLivres,
                                   RedirectAttributes redirectAttributes) {
        requirePermission();
        Long unidadeId = unidadeAtual();
        boolean origemCalendario = "calendario".equalsIgnoreCase(origem);
        if (bindingResult.hasErrors()) {
            if (origemCalendario) {
                redirectAttributes.addFlashAttribute("errorMessage", "Dados invalidos para vincular paciente no horario selecionado.");
                return redirecionarParaCalendario(cargoColaboradorId, especialidadeId, dataInicio, dataFim, mostrarLivres);
            }
            popularTelaPacientes(model, unidadeId, id);
            return "pages/care/scheduling/agendas/pacientes";
        }
        try {
            var resultado = agendaService.vincularPaciente(unidadeId, id, form);
            String mensagem = resultado.getTotalCriado() == 1
                    ? "Paciente vinculado com sucesso"
                    : resultado.getTotalCriado() + " sessoes vinculadas com sucesso";
            if (resultado.getTotalNaoCriado() > 0) {
                mensagem += ". Nao criadas: " + resultado.getTotalNaoCriado();
                String resumoAvisos = resultado.resumoAvisos(2);
                if (!resumoAvisos.isBlank()) {
                    mensagem += " (" + resumoAvisos + ")";
                }
            }
            redirectAttributes.addFlashAttribute("successMessage", mensagem);
            if (origemCalendario) {
                return redirecionarParaCalendario(cargoColaboradorId, especialidadeId, dataInicio, dataFim, mostrarLivres);
            }
            return "redirect:/ui/agendamentos/" + id + "/pacientes";
        } catch (IllegalArgumentException ex) {
            if (origemCalendario) {
                redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
                return redirecionarParaCalendario(cargoColaboradorId, especialidadeId, dataInicio, dataFim, mostrarLivres);
            }
            model.addAttribute("errorMessage", ex.getMessage());
            popularTelaPacientes(model, unidadeId, id);
            return "pages/care/scheduling/agendas/pacientes";
        }
    }

    @PostMapping("/{id}/pacientes/{agendaPacienteId}/excluir")
    public String removerPaciente(@PathVariable Long id,
                                  @PathVariable Long agendaPacienteId,
                                  @RequestParam(required = false) String origem,
                                  @RequestParam(required = false) Long cargoColaboradorId,
                                  @RequestParam(required = false) Long especialidadeId,
                                  @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
                                  @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
                                  @RequestParam(required = false, defaultValue = "false") boolean mostrarLivres,
                                  RedirectAttributes redirectAttributes) {
        requirePermission();
        Long unidadeId = unidadeAtual();
        try {
            agendaService.removerPaciente(unidadeId, id, agendaPacienteId);
            redirectAttributes.addFlashAttribute("successMessage", "Paciente removido da agenda");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return redirecionarPosAcaoAgenda(id, origem, cargoColaboradorId, especialidadeId, dataInicio, dataFim, mostrarLivres);
    }

    @PostMapping("/{id}/pacientes/{agendaPacienteId}/confirmar")
    public String confirmarPaciente(@PathVariable Long id,
                                    @PathVariable Long agendaPacienteId,
                                    @RequestParam(required = false) String origem,
                                    @RequestParam(required = false) Long cargoColaboradorId,
                                    @RequestParam(required = false) Long especialidadeId,
                                    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
                                    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
                                    @RequestParam(required = false, defaultValue = "false") boolean mostrarLivres,
                                    RedirectAttributes redirectAttributes) {
        requirePermission();
        Long unidadeId = unidadeAtual();
        try {
            agendaService.confirmarPaciente(unidadeId, id, agendaPacienteId);
            redirectAttributes.addFlashAttribute("successMessage", "Agendamento confirmado");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return redirecionarPosAcaoAgenda(id, origem, cargoColaboradorId, especialidadeId, dataInicio, dataFim, mostrarLivres);
    }

    @PostMapping("/{id}/pacientes/{agendaPacienteId}/falta")
    public String marcarFalta(@PathVariable Long id,
                              @PathVariable Long agendaPacienteId,
                              @RequestParam(required = false) String origem,
                              @RequestParam(required = false) Long cargoColaboradorId,
                              @RequestParam(required = false) Long especialidadeId,
                              @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
                              @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
                              @RequestParam(required = false, defaultValue = "false") boolean mostrarLivres,
                              RedirectAttributes redirectAttributes) {
        requirePermission();
        Long unidadeId = unidadeAtual();
        try {
            agendaService.marcarFalta(unidadeId, id, agendaPacienteId);
            redirectAttributes.addFlashAttribute("successMessage", "Paciente marcado como falta");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return redirecionarPosAcaoAgenda(id, origem, cargoColaboradorId, especialidadeId, dataInicio, dataFim, mostrarLivres);
    }

    @PostMapping("/{id}/pacientes/{agendaPacienteId}/atendido")
    public String marcarAtendido(@PathVariable Long id,
                                 @PathVariable Long agendaPacienteId,
                                 @RequestParam(required = false) String origem,
                                 @RequestParam(required = false) Long cargoColaboradorId,
                                 @RequestParam(required = false) Long especialidadeId,
                                 @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
                                 @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
                                 @RequestParam(required = false, defaultValue = "false") boolean mostrarLivres,
                                 RedirectAttributes redirectAttributes) {
        requirePermission();
        Long unidadeId = unidadeAtual();
        try {
            agendaService.marcarAtendido(unidadeId, id, agendaPacienteId);
            redirectAttributes.addFlashAttribute("successMessage", "Paciente marcado como atendido");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return redirecionarPosAcaoAgenda(id, origem, cargoColaboradorId, especialidadeId, dataInicio, dataFim, mostrarLivres);
    }

    @GetMapping("/{id}/pacientes/{agendaPacienteId}/reagendar")
    public String telaReagendar(@PathVariable Long id,
                                @PathVariable Long agendaPacienteId,
                                @RequestParam(required = false) Long agendaDestinoId,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        requirePermission();
        Long unidadeId = unidadeAtual();
        if (!agendaService.unidadePermiteAgendamento(unidadeId)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Agendamento nao habilitado para esta unidade");
            return "redirect:/ui/agendamentos";
        }
        var agenda = agendaService.buscar(unidadeId, id);
        var pacientes = agendaService.listarPacientesAgendados(unidadeId, id);
        var agendaPaciente = pacientes.stream()
                .filter(item -> item.getId().equals(agendaPacienteId))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Agendamento de paciente nao encontrado"));
        if (!model.containsAttribute("reagendamentoForm")) {
            AgendaReagendamentoForm form = new AgendaReagendamentoForm();
            form.setAgendaDestinoId(id);
            var horariosDisponiveis = agendaService.listarHorariosDisponiveis(unidadeId, id);
            if (!horariosDisponiveis.isEmpty()) {
                form.setHorarioDestino(horariosDisponiveis.get(0));
            }
            model.addAttribute("reagendamentoForm", form);
        }
        AgendaReagendamentoForm reagendamentoForm = (AgendaReagendamentoForm) model.getAttribute("reagendamentoForm");
        if (agendaDestinoId != null && !agendaDestinoId.equals(reagendamentoForm.getAgendaDestinoId())) {
            reagendamentoForm.setAgendaDestinoId(agendaDestinoId);
            reagendamentoForm.setHorarioDestino(null);
        }
        LocalDate inicioBusca = agenda.getDataAgenda().minusDays(7);
        LocalDate fimBusca = agenda.getDataAgenda().plusDays(30);
        Long especialidadeId = agenda.getEspecialidade() == null ? null : agenda.getEspecialidade().getId();
        var agendasDestino = agendaService.listarAgendasDoContexto(
                unidadeId,
                agenda.getCargoColaborador().getId(),
                especialidadeId,
                inicioBusca,
                fimBusca);

        Long agendaDestinoSelecionada = reagendamentoForm.getAgendaDestinoId();
        if (agendaDestinoSelecionada == null) {
            agendaDestinoSelecionada = id;
        }
        var horariosDestino = agendaService.listarHorariosDisponiveis(unidadeId, agendaDestinoSelecionada);
        if (reagendamentoForm.getHorarioDestino() == null || !horariosDestino.contains(reagendamentoForm.getHorarioDestino())) {
            reagendamentoForm.setHorarioDestino(horariosDestino.isEmpty() ? null : horariosDestino.get(0));
        }

        model.addAttribute("agenda", agenda);
        model.addAttribute("agendaPaciente", agendaPaciente);
        model.addAttribute("agendasDestino", agendasDestino);
        model.addAttribute("horariosDestino", horariosDestino);
        return "pages/care/scheduling/agendas/reagendar";
    }

    @PostMapping("/{id}/pacientes/{agendaPacienteId}/reagendar")
    public String reagendar(@PathVariable Long id,
                            @PathVariable Long agendaPacienteId,
                            @Valid @ModelAttribute("reagendamentoForm") AgendaReagendamentoForm form,
                            BindingResult bindingResult,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        requirePermission();
        Long unidadeId = unidadeAtual();
        if (bindingResult.hasErrors()) {
            return telaReagendar(id, agendaPacienteId, null, model, redirectAttributes);
        }
        try {
            agendaService.reagendarPaciente(unidadeId, id, agendaPacienteId, form);
            redirectAttributes.addFlashAttribute("successMessage", "Paciente reagendado com sucesso");
            Long agendaDestino = form.getAgendaDestinoId() == null ? id : form.getAgendaDestinoId();
            return "redirect:/ui/agendamentos/" + agendaDestino + "/pacientes";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return telaReagendar(id, agendaPacienteId, null, model, redirectAttributes);
        }
    }

    private void popularTelaPacientes(Model model, Long unidadeId, Long agendaId) {
        var agenda = agendaService.buscar(unidadeId, agendaId);
        var pacientesAgendados = agendaService.listarPacientesAgendados(unidadeId, agendaId);
        var pacientesOrdenados = pacientesAgendados.stream()
                .sorted(java.util.Comparator
                        .comparing((br.com.his.care.scheduling.model.AgendaEspecialidadePaciente item) -> item.getCriadoEm())
                        .thenComparing(item -> item.getPaciente().getNomeExibicao()))
                .toList();
        long vagasOcupadas = pacientesAgendados.stream()
                .filter(item -> item.getStatus() != StatusAgendamentoPaciente.CANCELADO)
                .count();
        long vagasDisponiveis = Math.max(agenda.getVagasTotais() - vagasOcupadas, 0);

        model.addAttribute("agenda", agenda);
        model.addAttribute("pacientesAgendados", pacientesOrdenados);
        model.addAttribute("pacientesDisponiveis", pacienteService.listarAtivosNaoMergeadosParaSelecao());
        model.addAttribute("tiposVaga", TipoVagaAgenda.values());
        model.addAttribute("statusAgendamentoValores", StatusAgendamentoPaciente.values());
        model.addAttribute("periodicidadesRecorrencia", PeriodicidadeRecorrenciaAgendamento.values());
        model.addAttribute("vagasOcupadas", vagasOcupadas);
        model.addAttribute("vagasDisponiveis", vagasDisponiveis);
        model.addAttribute("horariosDisponiveis", agendaService.listarHorariosDisponiveis(unidadeId, agendaId));
    }

    private Long unidadeAtual() {
        return unidadeContext.getUnidadeAtual()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unidade nao selecionada"));
    }

    private List<Especialidade> listarEspecialidadesParaFormulario(Long cargoColaboradorId) {
        if (cargoColaboradorId == null) {
            return List.of();
        }
        return agendaService.listarEspecialidadesAtivasPorCargo(cargoColaboradorId);
    }

    private String redirecionarParaCalendario(Long cargoColaboradorId,
                                              Long especialidadeId,
                                              LocalDate dataInicio,
                                              LocalDate dataFim,
                                              boolean mostrarLivres) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromPath("/ui/agendamentos/calendario");
        if (cargoColaboradorId != null) {
            builder.queryParam("cargoColaboradorId", cargoColaboradorId);
        }
        if (especialidadeId != null) {
            builder.queryParam("especialidadeId", especialidadeId);
        }
        if (dataInicio != null) {
            builder.queryParam("dataInicio", dataInicio);
        }
        if (dataFim != null) {
            builder.queryParam("dataFim", dataFim);
        }
        if (mostrarLivres) {
            builder.queryParam("mostrarLivres", "true");
        }
        return "redirect:" + builder.toUriString();
    }

    private String redirecionarPosAcaoAgenda(Long agendaId,
                                             String origem,
                                             Long cargoColaboradorId,
                                             Long especialidadeId,
                                             LocalDate dataInicio,
                                             LocalDate dataFim,
                                             boolean mostrarLivres) {
        if ("calendario".equalsIgnoreCase(origem)) {
            return redirecionarParaCalendario(cargoColaboradorId, especialidadeId, dataInicio, dataFim, mostrarLivres);
        }
        return "redirect:/ui/agendamentos/" + agendaId + "/pacientes";
    }

    private void popularCombosFormulario(Model model, AgendaEspecialidadeForm form) {
        model.addAttribute("cargos", cargoColaboradorRepository.findAssistenciaisAtivosOrderByDescricaoAsc());
        model.addAttribute("especialidades", listarEspecialidadesParaFormulario(
                form == null ? null : form.getCargoColaboradorId()));
        model.addAttribute("modosAgenda", ModoAgendaEspecialidade.values());
        model.addAttribute("intervalosSessao", List.of(15, 20, 30, 45, 60, 90, 120));
        if (form != null && !Boolean.TRUE.equals(model.getAttribute("modoEdicao"))) {
            YearMonth competencia = parseCompetencia(form.getCompetencia());
            model.addAttribute("competenciaAtual", competencia.format(COMPETENCIA_FORMATTER));
            model.addAttribute("diasCompetencia", diasDaCompetencia(competencia));
        }
    }

    private String normalizarCompetencia(String competencia) {
        if (competencia != null && !competencia.isBlank()) {
            return parseCompetencia(competencia).format(COMPETENCIA_FORMATTER);
        }
        return YearMonth.now().format(COMPETENCIA_FORMATTER);
    }

    private YearMonth parseCompetencia(String competencia) {
        try {
            return YearMonth.parse(normalizarCompetenciaInput(competencia), COMPETENCIA_FORMATTER);
        } catch (RuntimeException ex) {
            return YearMonth.now();
        }
    }

    private String normalizarCompetenciaInput(String competencia) {
        if (competencia == null || competencia.isBlank()) {
            return YearMonth.now().format(COMPETENCIA_FORMATTER);
        }
        return competencia.trim();
    }

    private List<DiaCompetenciaOption> diasDaCompetencia(YearMonth competencia) {
        List<DiaCompetenciaOption> dias = new java.util.ArrayList<>();
        for (int dia = 1; dia <= competencia.lengthOfMonth(); dia++) {
            LocalDate data = competencia.atDay(dia);
            dias.add(new DiaCompetenciaOption(
                    dia,
                    data.getDayOfWeek().name(),
                    data.getDayOfWeek().getValue()));
        }
        return dias;
    }

    private PacienteLookupOption toOption(Especialidade especialidade) {
        return new PacienteLookupOption(especialidade.getId(), especialidade.getDescricao());
    }

    private void requirePermission() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!operationalPermissionService.has(authentication, OperationalPermissionService.PERM_RECEPCAO_EXECUTAR)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Sem permissao para acessar agendamento");
        }
    }

    private long contarStatus(List<AgendaCalendarioEventoDto> eventos,
                              StatusAgendamentoPaciente status) {
        return eventos.stream()
                .filter(evento -> evento.getStatus() == status)
                .count();
    }

    private String formatarHorario(LocalTime horario) {
        return horario == null ? "" : horario.format(HORARIO_FORMATTER);
    }

    public record DiaCompetenciaOption(int dia, String diaSemanaNome, int diaSemanaValor) {
    }
}
