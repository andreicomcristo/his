package br.com.his.assistencial.ui;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpSession;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.his.access.context.UnidadeContext;
import br.com.his.access.repository.UnidadeRepository;
import br.com.his.access.service.OperationalPermissionService;
import br.com.his.admin.service.AreaAdminService;
import br.com.his.admin.service.FormaChegadaAdminService;
import br.com.his.admin.service.GrauParentescoAdminService;
import br.com.his.admin.service.MotivoEntradaAdminService;
import br.com.his.admin.service.SituacaoOcupacionalAdminService;
import br.com.his.assistencial.dto.AtendimentoWizardForm;
import br.com.his.assistencial.model.Desfecho;
import br.com.his.assistencial.model.Atendimento;
import br.com.his.assistencial.model.Entrada;
import br.com.his.assistencial.model.Internacao;
import br.com.his.assistencial.model.Observacao;
import br.com.his.assistencial.model.StatusAtendimento;
import br.com.his.assistencial.model.TipoAtendimento;
import br.com.his.assistencial.repository.DesfechoRepository;
import br.com.his.assistencial.repository.EntradaRepository;
import br.com.his.assistencial.repository.InternacaoRepository;
import br.com.his.assistencial.repository.ObservacaoRepository;
import br.com.his.assistencial.repository.StatusAtendimentoRepository;
import br.com.his.assistencial.service.AssistencialFlowService;
import br.com.his.configuracao.repository.CidadeRepository;
import br.com.his.configuracao.repository.UnidadeFederativaRepository;
import br.com.his.paciente.dto.PacienteForm;
import br.com.his.paciente.model.Paciente;
import br.com.his.paciente.service.PacienteLookupService;
import br.com.his.paciente.service.PacienteService;
import br.com.his.paciente.validation.CpfUtils;
import org.springframework.http.HttpStatus;

@Controller
@RequestMapping("/ui/atendimentos")
public class AtendimentoController {

    private static final String SESSION_ATENDIMENTO_CHEGADAS = "atendimentoChegadas";
    private static final String SESSION_ATENDIMENTO_WIZARD = "atendimentoWizard";

    private final AssistencialFlowService assistencialFlowService;
    private final DesfechoRepository desfechoRepository;
    private final EntradaRepository entradaRepository;
    private final ObservacaoRepository observacaoRepository;
    private final InternacaoRepository internacaoRepository;
    private final StatusAtendimentoRepository statusAtendimentoRepository;
    private final UnidadeContext unidadeContext;
    private final UnidadeRepository unidadeRepository;
    private final OperationalPermissionService operationalPermissionService;
    private final PacienteService pacienteService;
    private final PacienteLookupService pacienteLookupService;
    private final UnidadeFederativaRepository unidadeFederativaRepository;
    private final CidadeRepository cidadeRepository;
    private final AreaAdminService areaAdminService;
    private final FormaChegadaAdminService formaChegadaAdminService;
    private final GrauParentescoAdminService grauParentescoAdminService;
    private final MotivoEntradaAdminService motivoEntradaAdminService;
    private final SituacaoOcupacionalAdminService situacaoOcupacionalAdminService;

    public AtendimentoController(AssistencialFlowService assistencialFlowService,
                                 DesfechoRepository desfechoRepository,
                                 EntradaRepository entradaRepository,
                                 ObservacaoRepository observacaoRepository,
                                 InternacaoRepository internacaoRepository,
                                 StatusAtendimentoRepository statusAtendimentoRepository,
                                 UnidadeContext unidadeContext,
                                 UnidadeRepository unidadeRepository,
                                 OperationalPermissionService operationalPermissionService,
                                 PacienteService pacienteService,
                                 PacienteLookupService pacienteLookupService,
                                 UnidadeFederativaRepository unidadeFederativaRepository,
                                 CidadeRepository cidadeRepository,
                                 AreaAdminService areaAdminService,
                                 FormaChegadaAdminService formaChegadaAdminService,
                                 GrauParentescoAdminService grauParentescoAdminService,
                                 MotivoEntradaAdminService motivoEntradaAdminService,
                                 SituacaoOcupacionalAdminService situacaoOcupacionalAdminService) {
        this.assistencialFlowService = assistencialFlowService;
        this.desfechoRepository = desfechoRepository;
        this.entradaRepository = entradaRepository;
        this.observacaoRepository = observacaoRepository;
        this.internacaoRepository = internacaoRepository;
        this.statusAtendimentoRepository = statusAtendimentoRepository;
        this.unidadeContext = unidadeContext;
        this.unidadeRepository = unidadeRepository;
        this.operationalPermissionService = operationalPermissionService;
        this.pacienteService = pacienteService;
        this.pacienteLookupService = pacienteLookupService;
        this.unidadeFederativaRepository = unidadeFederativaRepository;
        this.cidadeRepository = cidadeRepository;
        this.areaAdminService = areaAdminService;
        this.formaChegadaAdminService = formaChegadaAdminService;
        this.grauParentescoAdminService = grauParentescoAdminService;
        this.motivoEntradaAdminService = motivoEntradaAdminService;
        this.situacaoOcupacionalAdminService = situacaoOcupacionalAdminService;
    }

    @GetMapping
    public String fila(@RequestParam(required = false) String nome,
                       @RequestParam(required = false) String cpf,
                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
                       @RequestParam(required = false) TipoAtendimento tipoAtendimento,
                       @RequestParam(required = false) Long statusId,
                       @RequestParam(required = false) Long areaEntradaId,
                       Model model) {
        requirePermission(OperationalPermissionService.PERM_ATENDIMENTO_ACESSAR);
        Long unidadeId = unidadeAtual();
        List<Atendimento> atendimentosBase = carregarAtendimentosBase(unidadeId, nome, cpf, dataInicio, dataFim, tipoAtendimento, statusId);
        Map<Long, Entrada> entradasMap = mapEntradas(atendimentosBase.stream().map(Atendimento::getId).toList());
        List<Atendimento> atendimentos = atendimentosBase.stream()
                .filter(a -> matchesAreaEntrada(entradasMap.get(a.getId()), areaEntradaId))
                .toList();
        populateListaModel(model, unidadeId, atendimentos, entradasMap, nome, cpf, dataInicio, dataFim,
                tipoAtendimento, statusId, areaEntradaId);
        model.addAttribute("pageTitle", "Atendimentos");
        model.addAttribute("listActionPath", "/ui/atendimentos");
        model.addAttribute("showNovoAtendimentoButton", true);
        model.addAttribute("showAreaEntradaFilter", true);
        model.addAttribute("pendingEntryMode", false);
        model.addAttribute("areaFilterLabel", "Porta de entrada");
        model.addAttribute("emptyMessage", "Nenhum atendimento encontrado");
        return "pages/atendimentos/list";
    }

    @GetMapping("/pendentes-entrada")
    public String pendentesEntrada(@RequestParam(required = false) String nome,
                                   @RequestParam(required = false) String cpf,
                                   @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
                                   @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
                                   @RequestParam(required = false) TipoAtendimento tipoAtendimento,
                                   @RequestParam(required = false) Long statusId,
                                   Model model) {
        requirePermission(OperationalPermissionService.PERM_ATENDIMENTO_ACESSAR);
        Long unidadeId = unidadeAtual();
        List<Atendimento> atendimentosBase = carregarAtendimentosBase(unidadeId, nome, cpf, dataInicio, dataFim, tipoAtendimento, statusId);
        Map<Long, Entrada> entradasMap = mapEntradas(atendimentosBase.stream().map(Atendimento::getId).toList());
        Map<Long, br.com.his.assistencial.model.ClassificacaoRisco> classificacoesMap =
                assistencialFlowService.mapaUltimaClassificacao(atendimentosBase.stream().map(Atendimento::getId).toList());
        List<Atendimento> atendimentos = atendimentosBase.stream()
                .filter(a -> !entradasMap.containsKey(a.getId()))
                .filter(a -> classificacoesMap.get(a.getId()) != null)
                .toList();
        populateListaModel(model, unidadeId, atendimentos, entradasMap, nome, cpf, dataInicio, dataFim,
                tipoAtendimento, statusId, null);
        model.addAttribute("pageTitle", "Atendimentos pendentes de entrada");
        model.addAttribute("listActionPath", "/ui/atendimentos/pendentes-entrada");
        model.addAttribute("showNovoAtendimentoButton", false);
        model.addAttribute("showAreaEntradaFilter", false);
        model.addAttribute("pendingEntryMode", true);
        model.addAttribute("emptyMessage", "Nenhum atendimento classificado aguardando entrada");
        return "pages/atendimentos/list";
    }

    @GetMapping("/pendentes-classificacao")
    public String pendentesClassificacao(@RequestParam(required = false) String nome,
                                         @RequestParam(required = false) String cpf,
                                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
                                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
                                         @RequestParam(required = false) TipoAtendimento tipoAtendimento,
                                         @RequestParam(required = false) Long statusId,
                                         @RequestParam(required = false) Long areaEntradaId,
                                         Model model) {
        requirePermission(OperationalPermissionService.PERM_ATENDIMENTO_ACESSAR);
        Long unidadeId = unidadeAtual();
        List<Atendimento> atendimentosBase = carregarAtendimentosBase(unidadeId, nome, cpf, dataInicio, dataFim, tipoAtendimento, statusId);
        Map<Long, Entrada> entradasMap = mapEntradas(atendimentosBase.stream().map(Atendimento::getId).toList());
        Map<Long, br.com.his.assistencial.model.ClassificacaoRisco> classificacoesMap =
                assistencialFlowService.mapaUltimaClassificacao(atendimentosBase.stream().map(Atendimento::getId).toList());
        List<Atendimento> atendimentos = atendimentosBase.stream()
                .filter(a -> entradasMap.containsKey(a.getId()))
                .filter(a -> classificacoesMap.get(a.getId()) == null)
                .filter(a -> matchesAreaEntrada(entradasMap.get(a.getId()), areaEntradaId))
                .toList();
        populateListaModel(model, unidadeId, atendimentos, entradasMap, nome, cpf, dataInicio, dataFim,
                tipoAtendimento, statusId, areaEntradaId);
        model.addAttribute("pageTitle", "Atendimentos pendentes de classificacao");
        model.addAttribute("listActionPath", "/ui/atendimentos/pendentes-classificacao");
        model.addAttribute("showNovoAtendimentoButton", false);
        model.addAttribute("showAreaEntradaFilter", true);
        model.addAttribute("pendingEntryMode", false);
        model.addAttribute("areaFilterLabel", "Porta de entrada");
        model.addAttribute("emptyMessage", "Nenhum atendimento aguardando classificacao");
        return "pages/atendimentos/list";
    }

    @GetMapping("/nao-identificados")
    public String naoIdentificados(@RequestParam(required = false) String nome,
                                   @RequestParam(required = false) String cpf,
                                   @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
                                   @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
                                   @RequestParam(required = false) TipoAtendimento tipoAtendimento,
                                   @RequestParam(required = false) Long statusId,
                                   @RequestParam(required = false) Long areaEntradaId,
                                   Model model) {
        requirePermission(OperationalPermissionService.PERM_PACIENTE_IDENTIFICAR);
        Long unidadeId = unidadeAtual();
        LocalDate inicio = dataInicio;
        LocalDate fim = dataFim;
        if (inicio != null && fim != null && fim.isBefore(inicio)) {
            LocalDate tmp = inicio;
            inicio = fim;
            fim = tmp;
        }
        final LocalDate filtroInicio = inicio;
        final LocalDate filtroFim = fim;

        List<Atendimento> filtrados = assistencialFlowService.listarNaoIdentificadosEmAberto(unidadeId).stream()
                .filter(a -> matchesPacienteNome(a, nome))
                .filter(a -> matchesPacienteCpf(a, cpf))
                .filter(a -> tipoAtendimento == null || a.getTipoAtendimento() == tipoAtendimento)
                .filter(a -> matchesDataChegada(a, filtroInicio, filtroFim))
                .toList();

        filtrados = filtrarPorStatusVisual(filtrados, statusId);
        Map<Long, Entrada> entradasMap = mapEntradas(filtrados.stream().map(Atendimento::getId).toList());
        List<Atendimento> atendimentos = filtrados.stream()
                .filter(a -> matchesAreaEntrada(entradasMap.get(a.getId()), areaEntradaId))
                .toList();

        model.addAttribute("atendimentos", atendimentos);
        model.addAttribute("nome", nome);
        model.addAttribute("cpf", cpf);
        model.addAttribute("dataInicio", inicio);
        model.addAttribute("dataFim", fim);
        model.addAttribute("tipoAtendimentoSelecionado", tipoAtendimento);
        model.addAttribute("statusSelecionadoId", statusId);
        model.addAttribute("areaEntradaSelecionadaId", areaEntradaId);
        model.addAttribute("tiposAtendimento", TipoAtendimento.values());
        model.addAttribute("statusAtendimentoOptions", statusAtendimentoRepository.findAllByOrderByDescricaoAsc());
        model.addAttribute("areasEntradaOptions", areaAdminService.listarAreasRecebemEntrada(unidadeId));
        model.addAttribute("listActionPath", "/ui/atendimentos/nao-identificados");
        return "pages/atendimentos/nao-identificados";
    }

    @GetMapping("/novo")
    public String novo(@RequestParam(required = false) Long pacienteId,
                       @RequestParam(required = false) Integer step,
                       @RequestParam(defaultValue = "false") boolean restart,
                       Model model,
                       HttpSession session) {
        requireCreateAtendimentoPermission();
        boolean iniciarNovoFluxo = restart || step == null;
        AtendimentoWizardForm wizard = iniciarNovoFluxo
                ? resetWizard(session, pacienteId)
                : getOrCreateWizard(session, pacienteId);
        if (step != null) {
            wizard.setCurrentStep(Math.max(1, Math.min(step, wizard.getCurrentStep())));
        }
        populateWizardModel(model, session, wizard, Map.of());
        return "pages/atendimentos/wizard";
    }

    @PostMapping("/novo/tipo-paciente")
    public String definirTipoPaciente(@RequestParam String tipoPaciente,
                                      Model model,
                                      HttpSession session) {
        requireCreateAtendimentoPermission();
        AtendimentoWizardForm wizard = getOrCreateWizard(session, null);
        if ("IDENTIFICADO".equalsIgnoreCase(tipoPaciente)) {
            wizard.setPacienteIdentificado(Boolean.TRUE);
            wizard.setCurrentStep(2);
        } else if ("NAO_IDENTIFICADO".equalsIgnoreCase(tipoPaciente)) {
            wizard.setPacienteIdentificado(Boolean.FALSE);
            wizard.setCurrentStep(2);
        } else {
            populateWizardModel(model, session, wizard, Map.of("tipoPaciente", "Selecione o tipo de paciente."));
            return "pages/atendimentos/wizard";
        }
        populateWizardModel(model, session, wizard, Map.of());
        return "pages/atendimentos/wizard";
    }

    @PostMapping("/novo/paciente/buscar-cpf")
    public String buscarPacientePorCpf(@RequestParam String cpfBusca,
                                       Model model,
                                       HttpSession session) {
        requireCreateAtendimentoPermission();
        AtendimentoWizardForm wizard = getOrCreateWizard(session, null);
        wizard.setCurrentStep(2);
        wizard.setCpfBusca(cpfBusca);

        Map<String, String> errors = new LinkedHashMap<>();
        if (!Boolean.TRUE.equals(wizard.getPacienteIdentificado())) {
            errors.put("tipoPaciente", "Este passo so se aplica para paciente identificado.");
            populateWizardModel(model, session, wizard, errors);
            return "pages/atendimentos/wizard";
        }

        String cpfNormalizado = CpfUtils.digitsOnly(cpfBusca);
        if (cpfNormalizado == null) {
            errors.put("cpfBusca", "Informe o CPF para buscar o paciente.");
            populateWizardModel(model, session, wizard, errors);
            return "pages/atendimentos/wizard";
        }
        if (!CpfUtils.isValid(cpfNormalizado)) {
            errors.put("cpfBusca", "CPF invalido.");
            populateWizardModel(model, session, wizard, errors);
            return "pages/atendimentos/wizard";
        }

        wizard.setCpfBusca(cpfNormalizado);
        wizard.getNovoPacienteForm().setCpf(cpfNormalizado);
        wizard.getNovoPacienteForm().setTemporario(false);
        wizard.getNovoPacienteForm().setSexo(defaultSexo(wizard.getNovoPacienteForm().getSexo()));

        pacienteService.buscarDefinitivoAtivoPorCpf(cpfNormalizado, null).ifPresentOrElse(paciente -> {
            wizard.setPacienteSelecionadoId(paciente.getId());
            wizard.setCadastrarNovoPaciente(false);
        }, () -> {
            wizard.setPacienteSelecionadoId(null);
            wizard.setCadastrarNovoPaciente(true);
        });

        populateWizardModel(model, session, wizard, Map.of());
        return "pages/atendimentos/wizard";
    }

    @PostMapping("/novo/paciente/continuar")
    public String continuarPaciente(AtendimentoWizardForm submitted,
                                    Model model,
                                    HttpSession session) {
        requireCreateAtendimentoPermission();
        AtendimentoWizardForm wizard = getOrCreateWizard(session, null);
        syncPacienteStep(wizard, submitted);
        wizard.setCurrentStep(2);

        Map<String, String> errors = validatePacienteStep(wizard);
        if (!errors.isEmpty()) {
            populateWizardModel(model, session, wizard, errors);
            return "pages/atendimentos/wizard";
        }

        wizard.setCurrentStep(3);
        populateWizardModel(model, session, wizard, Map.of());
        return "pages/atendimentos/wizard";
    }

    @PostMapping("/novo/atendimento/continuar")
    public String continuarAtendimento(AtendimentoWizardForm submitted,
                                       Model model,
                                       HttpSession session) {
        requireCreateAtendimentoPermission();
        AtendimentoWizardForm wizard = getOrCreateWizard(session, null);
        syncAtendimentoStep(wizard, submitted);
        wizard.setCurrentStep(3);

        Map<String, String> errors = validateAtendimentoStep(wizard);
        if (!errors.isEmpty()) {
            populateWizardModel(model, session, wizard, errors);
            return "pages/atendimentos/wizard";
        }

        wizard.setCurrentStep(4);
        populateWizardModel(model, session, wizard, Map.of());
        return "pages/atendimentos/wizard";
    }

    @PostMapping("/novo/finalizar")
    public String finalizarWizard(AtendimentoWizardForm submitted,
                                  Model model,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        requireCreateAtendimentoPermission();
        AtendimentoWizardForm wizard = getOrCreateWizard(session, null);
        syncEntradaStep(wizard, submitted);
        wizard.setCurrentStep(4);

        Map<String, String> errors = validateEntradaStep(wizard);
        if (!errors.isEmpty()) {
            populateWizardModel(model, session, wizard, errors);
            return "pages/atendimentos/wizard";
        }

        try {
            Long pacienteId = resolvePacienteId(wizard);
            LocalDateTime chegada = consumeChegadaToken(session, wizard.getChegadaToken());
            Atendimento atendimento = assistencialFlowService.criarAtendimento(
                    pacienteId,
                    unidadeAtual(),
                    wizard.getTipoAtendimento(),
                    chegada);
            assistencialFlowService.registrarEntradaPorAtendimento(atendimento.getId(), wizard.getEntradaForm());
            session.removeAttribute(SESSION_ATENDIMENTO_WIZARD);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Atendimento " + atendimento.getId() + " criado e entrada registrada com sucesso");
            return "redirect:/ui/atendimentos";
        } catch (IllegalArgumentException ex) {
            populateWizardModel(model, session, wizard, Map.of("global", ex.getMessage()));
            return "pages/atendimentos/wizard";
        }
    }

    @PostMapping("/{id}/finalizar")
    public String finalizar(@PathVariable Long id,
                            RedirectAttributes redirectAttributes) {
        requirePermission(OperationalPermissionService.PERM_RECEPCAO_EXECUTAR);
        try {
            assistencialFlowService.finalizarAtendimento(id, false);
            redirectAttributes.addFlashAttribute("successMessage", "Atendimento finalizado");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/ui/atendimentos";
    }

    @PostMapping("/{id}/evadir")
    public String evadir(@PathVariable Long id,
                         RedirectAttributes redirectAttributes) {
        requirePermission(OperationalPermissionService.PERM_RECEPCAO_EXECUTAR);
        try {
            assistencialFlowService.finalizarAtendimento(id, true);
            redirectAttributes.addFlashAttribute("successMessage", "Atendimento marcado como evadido");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/ui/atendimentos";
    }

    @GetMapping("/{id}/timeline")
    public String timeline(@PathVariable Long id, Model model) {
        requirePermission(OperationalPermissionService.PERM_ATENDIMENTO_ACESSAR);
        model.addAttribute("atendimento", assistencialFlowService.buscarAtendimento(id));
        model.addAttribute("timeline", assistencialFlowService.timeline(id));
        return "pages/atendimentos/timeline";
    }

    private void requirePermission(String permission) {
        if (!operationalPermissionService.has(
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication(),
                permission)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Sem permissao para esta operacao");
        }
    }

    private Long unidadeAtual() {
        return unidadeContext.getUnidadeAtual()
                .orElseThrow(() -> new IllegalArgumentException("Unidade atual nao definida"));
    }

    private void requireCreateAtendimentoPermission() {
        if (!operationalPermissionService.canCriarAtendimento(
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Sem permissao para criar atendimento");
        }
    }

    private List<TipoAtendimento> tiposPermitidosOrdenados(
            org.springframework.security.core.Authentication authentication) {
        Set<TipoAtendimento> permitidos = operationalPermissionService.tiposPermitidosCriarAtendimento(authentication);
        List<TipoAtendimento> ordenados = new ArrayList<>(permitidos);
        ordenados.sort(java.util.Comparator.comparing(Enum::name));
        return ordenados;
    }

    private void populateWizardModel(Model model,
                                     HttpSession session,
                                     AtendimentoWizardForm wizard,
                                     Map<String, String> errors) {
        var authentication = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        List<TipoAtendimento> tiposPermitidos = tiposPermitidosOrdenados(authentication);
        if (tiposPermitidos.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Sem permissao para criar atendimento nesta unidade");
        }
        if (wizard.getTipoAtendimento() == null || !tiposPermitidos.contains(wizard.getTipoAtendimento())) {
            wizard.setTipoAtendimento(tiposPermitidos.get(0));
        }
        model.addAttribute("wizard", wizard);
        model.addAttribute("tiposAtendimento", tiposPermitidos);
        model.addAttribute("horaChegadaCapturada", lookupChegadaToken(session, wizard.getChegadaToken()));
        model.addAttribute("wizardErrors", errors);
        model.addAttribute("patientSummary", buildPatientSummary(wizard));
        model.addAttribute("areasEntrada", areaAdminService.listarAreasRecebemEntrada(unidadeAtual()));
        model.addAttribute("tiposProcedenciaEntrada", pacienteLookupService.listarTiposProcedenciaEntrada());
        model.addAttribute("procedenciasEntrada", pacienteLookupService.listarProcedenciasEntrada(unidadeAtual()));
        Long cidadeId = unidadeRepository.findById(unidadeAtual())
                .map(unidade -> unidade.getCidade())
                .map(br.com.his.configuracao.model.Cidade::getId)
                .orElse(null);
                        model.addAttribute("bairrosEntrada", pacienteLookupService.listarBairrosPorCidade(cidadeId));
        model.addAttribute("ufsEntrada", unidadeFederativaRepository.findAllByOrderByNomeAsc());
        model.addAttribute("cidadesEntrada", pacienteLookupService.listarCidadesProcedenciaEntrada());
        model.addAttribute("formasChegada", formaChegadaAdminService.listarAtivas());
        model.addAttribute("grausParentesco", grauParentescoAdminService.listarAtivos());
        model.addAttribute("motivosEntrada", motivoEntradaAdminService.listarAtivos());
        model.addAttribute("situacoesOcupacionais", situacaoOcupacionalAdminService.listarAtivas());
        model.addAttribute("profissoes", pacienteLookupService.listarProfissoes());
        populateEntradaDefaults(wizard);
        populatePacienteLookups(model, wizard.getNovoPacienteForm());
    }

    private Map<Long, Entrada> mapEntradas(List<Long> atendimentoIds) {
        Map<Long, Entrada> result = new HashMap<>();
        for (Long atendimentoId : atendimentoIds) {
            entradaRepository.findByAtendimentoId(atendimentoId)
                    .ifPresent(entrada -> result.put(atendimentoId, entrada));
        }
        return result;
    }

    private Map<Long, Desfecho> mapDesfechos(List<Long> atendimentoIds) {
        if (atendimentoIds.isEmpty()) {
            return Map.of();
        }
        return desfechoRepository.findByAtendimentoIdIn(atendimentoIds).stream()
                .collect(Collectors.toMap(desfecho -> desfecho.getAtendimento().getId(), desfecho -> desfecho));
    }

    private Map<Long, Observacao> mapObservacoesAtivas(List<Long> atendimentoIds) {
        if (atendimentoIds.isEmpty()) {
            return Map.of();
        }
        Map<Long, Observacao> result = new HashMap<>();
        for (Long atendimentoId : atendimentoIds) {
            observacaoRepository.findByAtendimentoIdAndDataHoraCancelamentoIsNullAndDataHoraFimIsNull(atendimentoId)
                    .ifPresent(observacao -> result.put(atendimentoId, observacao));
        }
        return result;
    }

    private Map<Long, Internacao> mapInternacoesAtivas(List<Long> atendimentoIds) {
        if (atendimentoIds.isEmpty()) {
            return Map.of();
        }
        Map<Long, Internacao> result = new HashMap<>();
        for (Long atendimentoId : atendimentoIds) {
            internacaoRepository.findByAtendimentoIdAndDataHoraCancelamentoIsNullAndDataHoraFimInternacaoIsNull(atendimentoId)
                    .ifPresent(internacao -> result.put(atendimentoId, internacao));
        }
        return result;
    }

    private List<Atendimento> carregarAtendimentosBase(Long unidadeId,
                                                       String nome,
                                                       String cpf,
                                                       LocalDate dataInicio,
                                                       LocalDate dataFim,
                                                       TipoAtendimento tipoAtendimento,
                                                       Long statusId) {
        List<Atendimento> filtrados = assistencialFlowService.listarPorPeriodo(unidadeId, dataInicio, dataFim).stream()
                .filter(a -> matchesPacienteNome(a, nome))
                .filter(a -> matchesPacienteCpf(a, cpf))
                .filter(a -> tipoAtendimento == null || a.getTipoAtendimento() == tipoAtendimento)
                .toList();
        return filtrarPorStatusVisual(filtrados, statusId);
    }

    private List<Atendimento> filtrarPorStatusVisual(List<Atendimento> atendimentos, Long statusId) {
        if (statusId == null) {
            return atendimentos;
        }
        StatusAtendimento statusSelecionado = statusAtendimentoRepository.findById(statusId).orElse(null);
        if (statusSelecionado == null) {
            return List.of();
        }
        String codigo = statusSelecionado.getCodigo() == null
                ? ""
                : statusSelecionado.getCodigo().trim().toUpperCase();
        List<Long> atendimentoIds = atendimentos.stream().map(Atendimento::getId).toList();
        if (atendimentoIds.isEmpty()) {
            return atendimentos;
        }

        if ("OBSERVACAO".equals(codigo)) {
            Set<Long> idsObservacao = new HashSet<>(observacaoRepository.findAtendimentoIdsComObservacaoAtiva(atendimentoIds));
            return atendimentos.stream()
                    .filter(a -> idsObservacao.contains(a.getId()))
                    .toList();
        }
        if ("INTERNACAO".equals(codigo)) {
            Set<Long> idsInternacao = new HashSet<>(internacaoRepository.findAtendimentoIdsComInternacao(atendimentoIds));
            return atendimentos.stream()
                    .filter(a -> idsInternacao.contains(a.getId()))
                    .toList();
        }

        return atendimentos.stream()
                .filter(a -> matchesStatus(a, statusId))
                .toList();
    }

    private void populateListaModel(Model model,
                                    Long unidadeId,
                                    List<Atendimento> atendimentos,
                                    Map<Long, Entrada> entradasMap,
                                    String nome,
                                    String cpf,
                                    LocalDate dataInicio,
                                    LocalDate dataFim,
                                    TipoAtendimento tipoAtendimento,
                                    Long statusId,
                                    Long areaEntradaId) {
        List<Long> atendimentoIds = atendimentos.stream().map(Atendimento::getId).toList();
        model.addAttribute("atendimentos", atendimentos);
        model.addAttribute("entradasMap", entradasMap);
        model.addAttribute("classificacoesMap", assistencialFlowService.mapaUltimaClassificacao(atendimentoIds));
        model.addAttribute("desfechosMap", mapDesfechos(atendimentoIds));
        model.addAttribute("observacoesAtivasMap", mapObservacoesAtivas(atendimentoIds));
        model.addAttribute("internacoesAtivasMap", mapInternacoesAtivas(atendimentoIds));
        model.addAttribute("nome", nome);
        model.addAttribute("cpf", cpf);
        model.addAttribute("dataInicio", dataInicio);
        model.addAttribute("dataFim", dataFim);
        model.addAttribute("tipoAtendimentoSelecionado", tipoAtendimento);
        model.addAttribute("statusSelecionadoId", statusId);
        model.addAttribute("areaEntradaSelecionadaId", areaEntradaId);
        model.addAttribute("tiposAtendimento", TipoAtendimento.values());
        model.addAttribute("statusAtendimentoOptions", statusAtendimentoRepository.findAllByOrderByDescricaoAsc());
        model.addAttribute("statusObservacaoVisual", statusAtendimentoRepository.findByCodigoIgnoreCase("OBSERVACAO").orElse(null));
        model.addAttribute("statusInternacaoVisual", statusAtendimentoRepository.findByCodigoIgnoreCase("INTERNACAO").orElse(null));
        model.addAttribute("areasEntradaOptions", areaAdminService.listarAreasRecebemEntrada(unidadeId));
    }

    private boolean matchesPacienteNome(Atendimento atendimento, String nome) {
        String filtro = normalizeFiltro(nome);
        if (filtro == null) {
            return true;
        }
        String nomePaciente = atendimento.getPaciente().getMergedInto() != null
                ? atendimento.getPaciente().getMergedInto().getNome()
                : atendimento.getPaciente().getNome();
        return nomePaciente != null && nomePaciente.toUpperCase().contains(filtro.toUpperCase());
    }

    private boolean matchesPacienteCpf(Atendimento atendimento, String cpf) {
        String filtro = normalizeFiltro(cpf);
        if (filtro == null) {
            return true;
        }
        String cpfPaciente = atendimento.getPaciente().getCpf();
        return cpfPaciente != null && cpfPaciente.contains(filtro);
    }

    private boolean matchesStatus(Atendimento atendimento, Long statusId) {
        if (statusId == null) {
            return true;
        }
        StatusAtendimento status = atendimento.getStatus();
        return status != null && statusId.equals(status.getId());
    }

    private boolean matchesDataChegada(Atendimento atendimento, LocalDate dataInicio, LocalDate dataFim) {
        if (atendimento.getDataHoraChegada() == null) {
            return false;
        }
        if (dataInicio != null && atendimento.getDataHoraChegada().isBefore(dataInicio.atStartOfDay())) {
            return false;
        }
        if (dataFim != null && atendimento.getDataHoraChegada().isAfter(dataFim.plusDays(1).atStartOfDay().minusNanos(1))) {
            return false;
        }
        return true;
    }

    private boolean matchesAreaEntrada(Entrada entrada, Long areaEntradaId) {
        if (areaEntradaId == null) {
            return true;
        }
        return entrada != null
                && entrada.getArea() != null
                && areaEntradaId.equals(entrada.getArea().getId());
    }

    private static String normalizeFiltro(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private AtendimentoWizardForm getOrCreateWizard(HttpSession session, Long pacienteId) {
        Object attr = session.getAttribute(SESSION_ATENDIMENTO_WIZARD);
        if (attr instanceof AtendimentoWizardForm wizard) {
            if (pacienteId != null && wizard.getPacienteSelecionadoId() == null) {
                applyPacientePreset(wizard, pacienteId);
            }
            return wizard;
        }
        return resetWizard(session, pacienteId);
    }

    private AtendimentoWizardForm resetWizard(HttpSession session, Long pacienteId) {
        AtendimentoWizardForm wizard = new AtendimentoWizardForm();
        wizard.setChegadaToken(registerChegadaToken(session));
        if (pacienteId != null) {
            applyPacientePreset(wizard, pacienteId);
        }
        session.setAttribute(SESSION_ATENDIMENTO_WIZARD, wizard);
        return wizard;
    }

    private void applyPacientePreset(AtendimentoWizardForm wizard, Long pacienteId) {
        Paciente paciente = pacienteService.buscarPorId(pacienteId);
        if (!paciente.isAtivo() || paciente.getMergedInto() != null || paciente.isTemporario()) {
            throw new IllegalArgumentException("Paciente informado nao pode iniciar este fluxo");
        }
        wizard.setPacienteIdentificado(Boolean.TRUE);
        wizard.setPacienteSelecionadoId(paciente.getId());
        wizard.setCpfBusca(paciente.getCpf());
        wizard.setCadastrarNovoPaciente(false);
        wizard.setCurrentStep(3);
    }

    private Map<String, String> validatePacienteStep(AtendimentoWizardForm wizard) {
        Map<String, String> errors = new LinkedHashMap<>();
        if (wizard.getPacienteIdentificado() == null) {
            errors.put("tipoPaciente", "Selecione se o paciente e identificado ou nao identificado.");
            return errors;
        }

        if (Boolean.TRUE.equals(wizard.getPacienteIdentificado())) {
            if (wizard.getPacienteSelecionadoId() != null) {
                Paciente paciente = pacienteService.buscarPorId(wizard.getPacienteSelecionadoId());
                if (!paciente.isAtivo() || paciente.getMergedInto() != null || paciente.isTemporario()) {
                    errors.put("cpfBusca", "Paciente selecionado nao pode ser usado neste fluxo.");
                }
                return errors;
            }

            PacienteForm form = wizard.getNovoPacienteForm();
            form.setTemporario(false);
            String nome = normalize(form.getNome());
            String cpf = CpfUtils.digitsOnly(form.getCpf());
            String sexo = defaultSexo(form.getSexo());
            if (nome == null) {
                errors.put("novoPacienteForm.nome", "Nome do paciente e obrigatorio.");
            }
            if (cpf == null) {
                errors.put("novoPacienteForm.cpf", "CPF do paciente e obrigatorio.");
            } else if (!CpfUtils.isValid(cpf)) {
                errors.put("novoPacienteForm.cpf", "CPF invalido.");
            } else if (pacienteService.buscarDefinitivoAtivoPorCpf(cpf, null).isPresent()) {
                errors.put("novoPacienteForm.cpf", "Ja existe paciente ativo com este CPF. Use a busca por CPF.");
            }
            if (!sexo.matches("M|F|NI")) {
                errors.put("novoPacienteForm.sexo", "Sexo invalido.");
            }
            form.setNome(nome);
            form.setCpf(cpf);
            form.setSexo(sexo);
            return errors;
        }

        String sexoTemporario = defaultSexo(wizard.getSexoTemporario());
        if (!sexoTemporario.matches("M|F|NI")) {
            errors.put("sexoTemporario", "Sexo temporario invalido.");
        }
        wizard.setSexoTemporario(sexoTemporario);
        return errors;
    }

    private Map<String, String> validateAtendimentoStep(AtendimentoWizardForm wizard) {
        Map<String, String> errors = new LinkedHashMap<>();
        var authentication = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        List<TipoAtendimento> tiposPermitidos = tiposPermitidosOrdenados(authentication);
        if (wizard.getTipoAtendimento() == null) {
            errors.put("tipoAtendimento", "Tipo de atendimento e obrigatorio.");
        } else if (!tiposPermitidos.contains(wizard.getTipoAtendimento())) {
            errors.put("tipoAtendimento", "Tipo de atendimento nao permitido para seu perfil na unidade atual.");
        }
        return errors;
    }

    private Map<String, String> validateEntradaStep(AtendimentoWizardForm wizard) {
        Map<String, String> errors = new LinkedHashMap<>();
        if (wizard.getEntradaForm().getTipoProcedenciaId() == null) {
            errors.put("entradaForm.tipoProcedenciaId", "Tipo de procedencia e obrigatorio.");
        } else if (Long.valueOf(2L).equals(wizard.getEntradaForm().getTipoProcedenciaId())) {
            if (wizard.getEntradaForm().getProcedenciaBairroId() == null) {
                errors.put("entradaForm.procedenciaBairroId", "Bairro e obrigatorio.");
            }
        } else if (Long.valueOf(3L).equals(wizard.getEntradaForm().getTipoProcedenciaId())) {
            if (wizard.getEntradaForm().getProcedenciaCidadeId() == null) {
                errors.put("entradaForm.procedenciaCidadeId", "Cidade e obrigatoria.");
            }
        } else if (wizard.getEntradaForm().getProcedenciaId() == null) {
            errors.put("entradaForm.procedenciaId", "Procedencia e obrigatoria.");
        }
        if (wizard.getEntradaForm().getAreaId() == null) {
            errors.put("entradaForm.areaId", "Area da entrada e obrigatoria.");
        }
        if (wizard.getEntradaForm().getFormaChegadaId() == null) {
            errors.put("entradaForm.formaChegadaId", "Forma de chegada e obrigatoria.");
        }
        return errors;
    }

    private Long resolvePacienteId(AtendimentoWizardForm wizard) {
        if (Boolean.TRUE.equals(wizard.getPacienteIdentificado())) {
            if (wizard.getPacienteSelecionadoId() != null) {
                return wizard.getPacienteSelecionadoId();
            }
            PacienteForm form = wizard.getNovoPacienteForm();
            form.setTemporario(false);
            form.setSexo(defaultSexo(form.getSexo()));
            return pacienteService.criarPacienteDefinitivo(form).getId();
        }
        return pacienteService.criarPacienteTemporario(
                wizard.getSexoTemporario(),
                wizard.getIdadeAparenteTemporario()).getId();
    }

    private Map<String, String> buildPatientSummary(AtendimentoWizardForm wizard) {
        Map<String, String> summary = new LinkedHashMap<>();
        if (wizard.getPacienteIdentificado() == null) {
            return summary;
        }
        if (Boolean.TRUE.equals(wizard.getPacienteIdentificado()) && wizard.getPacienteSelecionadoId() != null) {
            Paciente paciente = pacienteService.buscarPorId(wizard.getPacienteSelecionadoId());
            summary.put("nome", valueOrDash(paciente.getNome()));
            summary.put("nomeMae", valueOrDash(paciente.getNomeMae()));
            summary.put("nomePai", valueOrDash(paciente.getNomePai()));
            summary.put("cpf", valueOrDash(paciente.getCpf()));
            summary.put("cns", valueOrDash(paciente.getCns()));
            summary.put("dataNascimento", paciente.getDataNascimento() == null ? "-" : paciente.getDataNascimento().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            summary.put("telefone", valueOrDash(paciente.getTelefone()));
            summary.put("tipo", "PACIENTE IDENTIFICADO");
            return summary;
        }
        if (Boolean.TRUE.equals(wizard.getPacienteIdentificado())) {
            PacienteForm form = wizard.getNovoPacienteForm();
            summary.put("nome", valueOrDash(form.getNome()));
            summary.put("nomeMae", valueOrDash(form.getNomeMae()));
            summary.put("nomePai", valueOrDash(form.getNomePai()));
            summary.put("cpf", valueOrDash(form.getCpf()));
            summary.put("cns", valueOrDash(form.getCns()));
            summary.put("dataNascimento", form.getDataNascimento() == null ? "-" : form.getDataNascimento().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            summary.put("telefone", valueOrDash(form.getTelefone()));
            summary.put("tipo", "NOVO PACIENTE IDENTIFICADO");
            return summary;
        }

        summary.put("nome", "Paciente nao identificado");
        summary.put("sexo", valueOrDash(wizard.getSexoTemporario()));
        summary.put("idadeAparente", wizard.getIdadeAparenteTemporario() == null ? "-" : String.valueOf(wizard.getIdadeAparenteTemporario()));
        summary.put("tipo", "PACIENTE TEMPORARIO");
        return summary;
    }

    private String defaultSexo(String sexo) {
        String value = normalize(sexo);
        return value == null ? "NI" : value;
    }

    private String valueOrDash(String value) {
        String normalized = normalize(value);
        return normalized == null ? "-" : normalized;
    }

    private String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private void syncPacienteStep(AtendimentoWizardForm wizard, AtendimentoWizardForm submitted) {
        PacienteForm source = submitted.getNovoPacienteForm();
        PacienteForm target = wizard.getNovoPacienteForm();
        target.setTemporario(false);
        target.setNome(source.getNome());
        target.setNomeSocial(source.getNomeSocial());
        target.setCpf(source.getCpf());
        target.setCns(source.getCns());
        target.setRg(source.getRg());
        target.setDataNascimento(source.getDataNascimento());
        target.setSexo(source.getSexo());
        target.setTelefone(source.getTelefone());
        target.setNomeMae(source.getNomeMae());
        target.setNomePai(source.getNomePai());
        target.setRacaCorId(source.getRacaCorId());
        target.setEtniaIndigenaId(source.getEtniaIndigenaId());
        target.setNacionalidadeId(source.getNacionalidadeId());
        target.setNaturalidadeId(source.getNaturalidadeId());
        target.setEstadoCivilId(source.getEstadoCivilId());
        target.setEscolaridadeId(source.getEscolaridadeId());
        target.setTipoSanguineoId(source.getTipoSanguineoId());
        target.setOrientacaoSexualId(source.getOrientacaoSexualId());
        target.setIdentidadeGeneroId(source.getIdentidadeGeneroId());
        target.setDeficienciaId(source.getDeficienciaId());
        target.setProfissaoId(source.getProfissaoId());
        target.setProcedenciaId(source.getProcedenciaId());
        target.setEmail(source.getEmail());
        target.setObservacoes(source.getObservacoes());
        target.setCep(source.getCep());
        target.setLogradouro(source.getLogradouro());
        target.setNumero(source.getNumero());
        target.setComplemento(source.getComplemento());
        target.setBairro(source.getBairro());
        target.setUnidadeFederativaId(source.getUnidadeFederativaId());
        target.setCidadeId(source.getCidadeId());
        wizard.setSexoTemporario(submitted.getSexoTemporario());
        wizard.setIdadeAparenteTemporario(submitted.getIdadeAparenteTemporario());
    }

    private void syncAtendimentoStep(AtendimentoWizardForm wizard, AtendimentoWizardForm submitted) {
        wizard.setTipoAtendimento(submitted.getTipoAtendimento());
    }

    private void syncEntradaStep(AtendimentoWizardForm wizard, AtendimentoWizardForm submitted) {
        wizard.getEntradaForm().setAreaId(submitted.getEntradaForm().getAreaId());
        wizard.getEntradaForm().setTipoProcedenciaId(submitted.getEntradaForm().getTipoProcedenciaId());
        wizard.getEntradaForm().setProcedenciaId(submitted.getEntradaForm().getProcedenciaId());
        wizard.getEntradaForm().setProcedenciaBairroId(submitted.getEntradaForm().getProcedenciaBairroId());
        wizard.getEntradaForm().setProcedenciaCidadeUfId(submitted.getEntradaForm().getProcedenciaCidadeUfId());
        wizard.getEntradaForm().setProcedenciaCidadeId(submitted.getEntradaForm().getProcedenciaCidadeId());
        wizard.getEntradaForm().setFormaChegadaId(submitted.getEntradaForm().getFormaChegadaId());
        wizard.getEntradaForm().setMotivoEntradaId(submitted.getEntradaForm().getMotivoEntradaId());
        wizard.getEntradaForm().setTelefoneComunicante(submitted.getEntradaForm().getTelefoneComunicante());
        wizard.getEntradaForm().setComunicante(submitted.getEntradaForm().getComunicante());
        wizard.getEntradaForm().setGrauParentescoId(submitted.getEntradaForm().getGrauParentescoId());
        wizard.getEntradaForm().setInformacaoAdChegada(submitted.getEntradaForm().getInformacaoAdChegada());
        wizard.getEntradaForm().setProcedenciaObservacao(submitted.getEntradaForm().getProcedenciaObservacao());
        wizard.getEntradaForm().setSituacaoOcupacionalId(submitted.getEntradaForm().getSituacaoOcupacionalId());
        wizard.getEntradaForm().setProfissaoId(submitted.getEntradaForm().getProfissaoId());
        wizard.getEntradaForm().setProfissaoObservacao(submitted.getEntradaForm().getProfissaoObservacao());
        wizard.getEntradaForm().setTempoServico(submitted.getEntradaForm().getTempoServico());
        wizard.getEntradaForm().setObservacoes(submitted.getEntradaForm().getObservacoes());
        wizard.getEntradaForm().setConvenio(submitted.getEntradaForm().getConvenio());
        wizard.getEntradaForm().setGuia(submitted.getEntradaForm().getGuia());
    }

    private void populateEntradaDefaults(AtendimentoWizardForm wizard) {
        if (wizard.getEntradaForm().getProfissaoId() != null) {
            return;
        }
        if (wizard.getPacienteSelecionadoId() != null) {
            Paciente paciente = pacienteService.buscarPorId(wizard.getPacienteSelecionadoId());
            if (paciente.getProfissao() != null) {
                wizard.getEntradaForm().setProfissaoId(paciente.getProfissao().getId());
            }
            return;
        }
        if (wizard.isCadastrarNovoPaciente() && wizard.getNovoPacienteForm().getProfissaoId() != null) {
            wizard.getEntradaForm().setProfissaoId(wizard.getNovoPacienteForm().getProfissaoId());
        }
    }

    private void populatePacienteLookups(Model model, PacienteForm form) {
        model.addAttribute("sexos", pacienteLookupService.listarSexos());
        model.addAttribute("racasCor", pacienteLookupService.listarRacasCor());
        model.addAttribute("etniasIndigenas", pacienteLookupService.listarEtniasIndigenas());
        model.addAttribute("nacionalidades", pacienteLookupService.listarNacionalidades());
        model.addAttribute("naturalidades", pacienteLookupService.listarNaturalidades());
        model.addAttribute("estadosCivis", pacienteLookupService.listarEstadosCivis());
        model.addAttribute("escolaridades", pacienteLookupService.listarEscolaridades());
        model.addAttribute("tiposSanguineos", pacienteLookupService.listarTiposSanguineos());
        model.addAttribute("orientacoesSexuais", pacienteLookupService.listarOrientacoesSexuais());
        model.addAttribute("identidadesGenero", pacienteLookupService.listarIdentidadesGenero());
        model.addAttribute("deficiencias", pacienteLookupService.listarDeficiencias());
        model.addAttribute("profissoes", pacienteLookupService.listarProfissoes());
        model.addAttribute("procedencias", pacienteLookupService.listarProcedencias());
        model.addAttribute("ufs", unidadeFederativaRepository.findAllByOrderByNomeAsc());
        model.addAttribute("cidades", form.getUnidadeFederativaId() == null
                ? List.of()
                : cidadeRepository.findByUnidadeFederativaIdOrderByNome(form.getUnidadeFederativaId()));
    }

    private String registerChegadaToken(HttpSession session) {
        Map<String, LocalDateTime> chegadas = getChegadas(session);
        String token = UUID.randomUUID().toString();
        chegadas.put(token, LocalDateTime.now());
        return token;
    }

    private LocalDateTime consumeChegadaToken(HttpSession session, String token) {
        Map<String, LocalDateTime> chegadas = getChegadas(session);
        if (token == null || token.isBlank()) {
            return LocalDateTime.now();
        }
        LocalDateTime chegada = chegadas.remove(token);
        return chegada == null ? LocalDateTime.now() : chegada;
    }

    private LocalDateTime lookupChegadaToken(HttpSession session, String token) {
        if (token == null || token.isBlank()) {
            return null;
        }
        return getChegadas(session).get(token);
    }

    @SuppressWarnings("unchecked")
    private Map<String, LocalDateTime> getChegadas(HttpSession session) {
        Object attr = session.getAttribute(SESSION_ATENDIMENTO_CHEGADAS);
        if (attr instanceof Map<?, ?> map) {
            return (Map<String, LocalDateTime>) map;
        }
        Map<String, LocalDateTime> novoMapa = new HashMap<>();
        session.setAttribute(SESSION_ATENDIMENTO_CHEGADAS, novoMapa);
        return novoMapa;
    }
}
