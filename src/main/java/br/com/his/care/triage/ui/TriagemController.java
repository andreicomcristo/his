package br.com.his.care.triage.ui;

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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.his.access.context.UnidadeContext;
import br.com.his.access.service.OperationalPermissionService;
import br.com.his.care.attendance.dto.AtendimentoWizardForm;
import br.com.his.care.triage.dto.TriagemForm;
import br.com.his.care.attendance.model.Atendimento;
import br.com.his.care.triage.model.AvcSinalAlerta;
import br.com.his.care.triage.model.ClassificacaoRisco;
import br.com.his.care.triage.model.GlasgowAberturaOcular;
import br.com.his.care.triage.model.GlasgowRespostaMotora;
import br.com.his.care.triage.model.GlasgowRespostaPupilar;
import br.com.his.care.triage.model.GlasgowRespostaVerbal;
import br.com.his.care.attendance.model.PrimeiroPassoFluxo;
import br.com.his.care.triage.model.ReguaDor;
import br.com.his.care.attendance.model.TipoAtendimento;
import br.com.his.care.triage.repository.AlergiaSeveridadeRepository;
import br.com.his.care.triage.repository.AlergiaSubstanciaRepository;
import br.com.his.care.triage.repository.AvcSinalAlertaRepository;
import br.com.his.care.triage.repository.ComorbidadeRepository;
import br.com.his.care.triage.repository.ClassificacaoCorRepository;
import br.com.his.care.triage.repository.GlasgowAberturaOcularRepository;
import br.com.his.care.triage.repository.GlasgowRespostaMotoraRepository;
import br.com.his.care.triage.repository.GlasgowRespostaPupilarRepository;
import br.com.his.care.triage.repository.GlasgowRespostaVerbalRepository;
import br.com.his.care.triage.repository.ReguaDorRepository;
import br.com.his.care.attendance.repository.StatusAtendimentoRepository;
import br.com.his.care.attendance.repository.UnidadeConfigFluxoRepository;
import br.com.his.care.attendance.service.AssistencialFlowService;
import br.com.his.patient.dto.PacienteForm;
import br.com.his.patient.model.Paciente;
import br.com.his.patient.service.PacienteLookupService;
import br.com.his.patient.service.PacienteService;
import br.com.his.patient.validation.CpfUtils;

@Controller
@RequestMapping("/ui/triagem")
public class TriagemController {

    private static final String SESSION_TRIAGEM_CHEGADAS = "triagemChegadas";
    private static final String SESSION_TRIAGEM_WIZARD = "triagemWizard";

    private final AssistencialFlowService assistencialFlowService;
    private final OperationalPermissionService operationalPermissionService;
    private final UnidadeContext unidadeContext;
    private final PacienteService pacienteService;
    private final PacienteLookupService pacienteLookupService;
    private final UnidadeConfigFluxoRepository unidadeConfigFluxoRepository;
    private final StatusAtendimentoRepository statusAtendimentoRepository;
    private final AlergiaSubstanciaRepository alergiaSubstanciaRepository;
    private final AlergiaSeveridadeRepository alergiaSeveridadeRepository;
    private final ComorbidadeRepository comorbidadeRepository;
    private final ClassificacaoCorRepository classificacaoCorRepository;
    private final AvcSinalAlertaRepository avcSinalAlertaRepository;
    private final ReguaDorRepository reguaDorRepository;
    private final GlasgowAberturaOcularRepository glasgowAberturaOcularRepository;
    private final GlasgowRespostaVerbalRepository glasgowRespostaVerbalRepository;
    private final GlasgowRespostaMotoraRepository glasgowRespostaMotoraRepository;
    private final GlasgowRespostaPupilarRepository glasgowRespostaPupilarRepository;

    public TriagemController(AssistencialFlowService assistencialFlowService,
                             OperationalPermissionService operationalPermissionService,
                             UnidadeContext unidadeContext,
                             PacienteService pacienteService,
                             PacienteLookupService pacienteLookupService,
                             UnidadeConfigFluxoRepository unidadeConfigFluxoRepository,
                             StatusAtendimentoRepository statusAtendimentoRepository,
                             AlergiaSubstanciaRepository alergiaSubstanciaRepository,
                             AlergiaSeveridadeRepository alergiaSeveridadeRepository,
                             ComorbidadeRepository comorbidadeRepository,
                             ClassificacaoCorRepository classificacaoCorRepository,
                             AvcSinalAlertaRepository avcSinalAlertaRepository,
                             ReguaDorRepository reguaDorRepository,
                             GlasgowAberturaOcularRepository glasgowAberturaOcularRepository,
                             GlasgowRespostaVerbalRepository glasgowRespostaVerbalRepository,
                             GlasgowRespostaMotoraRepository glasgowRespostaMotoraRepository,
                             GlasgowRespostaPupilarRepository glasgowRespostaPupilarRepository) {
        this.assistencialFlowService = assistencialFlowService;
        this.operationalPermissionService = operationalPermissionService;
        this.unidadeContext = unidadeContext;
        this.pacienteService = pacienteService;
        this.pacienteLookupService = pacienteLookupService;
        this.unidadeConfigFluxoRepository = unidadeConfigFluxoRepository;
        this.statusAtendimentoRepository = statusAtendimentoRepository;
        this.alergiaSubstanciaRepository = alergiaSubstanciaRepository;
        this.alergiaSeveridadeRepository = alergiaSeveridadeRepository;
        this.comorbidadeRepository = comorbidadeRepository;
        this.classificacaoCorRepository = classificacaoCorRepository;
        this.avcSinalAlertaRepository = avcSinalAlertaRepository;
        this.reguaDorRepository = reguaDorRepository;
        this.glasgowAberturaOcularRepository = glasgowAberturaOcularRepository;
        this.glasgowRespostaVerbalRepository = glasgowRespostaVerbalRepository;
        this.glasgowRespostaMotoraRepository = glasgowRespostaMotoraRepository;
        this.glasgowRespostaPupilarRepository = glasgowRespostaPupilarRepository;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) String nome,
                         @RequestParam(required = false) String cpf,
                         @RequestParam(required = false) Long atendimentoId,
                         @RequestParam(required = false) TipoAtendimento tipoAtendimento,
                         @RequestParam(required = false) Long statusId,
                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
                         Model model) {
        requirePermission();
        Long unidadeId = unidadeAtual();
        List<Atendimento> atendimentos = assistencialFlowService.listarFilaClassificacao(unidadeId).stream()
                .filter(atendimento -> matchesNome(atendimento, nome))
                .filter(atendimento -> matchesCpf(atendimento, cpf))
                .filter(atendimento -> matchesAtendimentoId(atendimento, atendimentoId))
                .filter(atendimento -> tipoAtendimento == null || atendimento.getTipoAtendimento() == tipoAtendimento)
                .filter(atendimento -> statusId == null
                        || (atendimento.getStatus() != null && statusId.equals(atendimento.getStatus().getId())))
                .filter(atendimento -> matchesPeriodo(atendimento, dataInicio, dataFim))
                .toList();
        Map<Long, ClassificacaoRisco> ultimaClassificacaoMap = assistencialFlowService.mapaUltimaClassificacao(
                atendimentos.stream().map(Atendimento::getId).toList());
        model.addAttribute("atendimentos", atendimentos);
        model.addAttribute("ultimaClassificacaoMap", ultimaClassificacaoMap);
        model.addAttribute("canCriarPorClassificacao", unidadeComecaNaTriagem());
        model.addAttribute("nome", nome);
        model.addAttribute("cpf", cpf);
        model.addAttribute("atendimentoId", atendimentoId);
        model.addAttribute("tipoAtendimentoSelecionado", tipoAtendimento);
        model.addAttribute("statusSelecionadoId", statusId);
        model.addAttribute("dataInicio", dataInicio);
        model.addAttribute("dataFim", dataFim);
        model.addAttribute("tiposAtendimento", TipoAtendimento.values());
        model.addAttribute("statusAtendimentoOptions", statusAtendimentoRepository.findAllByOrderByDescricaoAsc());
        return "pages/care/triage/list";
    }

    @GetMapping("/novo")
    public String novo(@RequestParam(required = false) Long pacienteId,
                       @RequestParam(required = false) Integer step,
                       @RequestParam(defaultValue = "false") boolean restart,
                       Model model,
                       HttpSession session) {
        requireWizardPermission();
        boolean iniciarNovoFluxo = restart || step == null;
        AtendimentoWizardForm wizard = iniciarNovoFluxo
                ? resetWizard(session, pacienteId)
                : getOrCreateWizard(session, pacienteId);
        if (step != null) {
            wizard.setCurrentStep(Math.max(1, Math.min(step, wizard.getCurrentStep())));
        }
        populateWizardModel(model, session, wizard, Map.of());
        return "pages/care/triage/wizard";
    }

    @PostMapping("/novo/tipo-paciente")
    public String definirTipoPaciente(@RequestParam String tipoPaciente,
                                      Model model,
                                      HttpSession session) {
        requireWizardPermission();
        AtendimentoWizardForm wizard = getOrCreateWizard(session, null);
        if ("IDENTIFICADO".equalsIgnoreCase(tipoPaciente)) {
            wizard.setPacienteIdentificado(Boolean.TRUE);
            wizard.setCurrentStep(2);
        } else if ("NAO_IDENTIFICADO".equalsIgnoreCase(tipoPaciente)) {
            wizard.setPacienteIdentificado(Boolean.FALSE);
            wizard.setCurrentStep(2);
        } else {
            populateWizardModel(model, session, wizard, Map.of("tipoPaciente", "Selecione o tipo de paciente."));
            return "pages/care/triage/wizard";
        }
        populateWizardModel(model, session, wizard, Map.of());
        return "pages/care/triage/wizard";
    }

    @PostMapping("/novo/paciente/buscar-cpf")
    public String buscarPacientePorCpf(@RequestParam String cpfBusca,
                                       Model model,
                                       HttpSession session) {
        requireWizardPermission();
        AtendimentoWizardForm wizard = getOrCreateWizard(session, null);
        wizard.setCurrentStep(2);
        wizard.setCpfBusca(cpfBusca);

        Map<String, String> errors = new LinkedHashMap<>();
        if (!Boolean.TRUE.equals(wizard.getPacienteIdentificado())) {
            errors.put("tipoPaciente", "Este passo so se aplica para paciente identificado.");
            populateWizardModel(model, session, wizard, errors);
            return "pages/care/triage/wizard";
        }

        String cpfNormalizado = CpfUtils.digitsOnly(cpfBusca);
        if (cpfNormalizado == null) {
            errors.put("cpfBusca", "Informe o CPF para buscar o paciente.");
            populateWizardModel(model, session, wizard, errors);
            return "pages/care/triage/wizard";
        }
        if (!CpfUtils.isValid(cpfNormalizado)) {
            errors.put("cpfBusca", "CPF invalido.");
            populateWizardModel(model, session, wizard, errors);
            return "pages/care/triage/wizard";
        }

        wizard.setCpfBusca(cpfNormalizado);
        wizard.setNovoPacienteCpf(cpfNormalizado);
        wizard.setNovoPacienteSexo(defaultSexo(wizard.getNovoPacienteSexo()));

        pacienteService.buscarDefinitivoAtivoPorCpf(cpfNormalizado, null).ifPresentOrElse(paciente -> {
            wizard.setPacienteSelecionadoId(paciente.getId());
            wizard.setCadastrarNovoPaciente(false);
        }, () -> {
            wizard.setPacienteSelecionadoId(null);
            wizard.setCadastrarNovoPaciente(true);
        });

        populateWizardModel(model, session, wizard, Map.of());
        return "pages/care/triage/wizard";
    }

    @PostMapping("/novo/paciente/continuar")
    public String continuarPaciente(AtendimentoWizardForm submitted,
                                    Model model,
                                    HttpSession session) {
        requireWizardPermission();
        AtendimentoWizardForm wizard = getOrCreateWizard(session, null);
        syncPacienteStep(wizard, submitted);
        wizard.setCurrentStep(2);

        Map<String, String> errors = validatePacienteStep(wizard);
        if (!errors.isEmpty()) {
            populateWizardModel(model, session, wizard, errors);
            return "pages/care/triage/wizard";
        }

        wizard.setCurrentStep(3);
        populateWizardModel(model, session, wizard, Map.of());
        return "pages/care/triage/wizard";
    }

    @PostMapping("/novo/atendimento/continuar")
    public String continuarAtendimento(AtendimentoWizardForm submitted,
                                       Model model,
                                       HttpSession session) {
        requireWizardPermission();
        AtendimentoWizardForm wizard = getOrCreateWizard(session, null);
        wizard.setTipoAtendimento(submitted.getTipoAtendimento());
        wizard.setCurrentStep(3);

        Map<String, String> errors = validateAtendimentoStep(wizard);
        if (!errors.isEmpty()) {
            populateWizardModel(model, session, wizard, errors);
            return "pages/care/triage/wizard";
        }

        wizard.setCurrentStep(4);
        populateWizardModel(model, session, wizard, Map.of());
        return "pages/care/triage/wizard";
    }

    @PostMapping("/novo/finalizar")
    public String finalizarWizard(AtendimentoWizardForm submitted,
                                  Model model,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        requireWizardPermission();
        AtendimentoWizardForm wizard = getOrCreateWizard(session, null);
        syncTriagemStep(wizard, submitted);
        wizard.setCurrentStep(4);

        Map<String, String> errors = validateTriagemStep(wizard);
        if (!errors.isEmpty()) {
            populateWizardModel(model, session, wizard, errors);
            return "pages/care/triage/wizard";
        }

        try {
            Long pacienteId = resolvePacienteId(wizard);
            LocalDateTime chegada = consumeChegadaToken(session, wizard.getChegadaToken());
            Atendimento atendimento = assistencialFlowService.criarAtendimento(
                    pacienteId,
                    unidadeAtual(),
                    wizard.getTipoAtendimento(),
                    chegada);
            assistencialFlowService.iniciarTriagem(atendimento.getId());
            assistencialFlowService.finalizarTriagem(atendimento.getId(), wizard.getTriagemForm());
            session.removeAttribute(SESSION_TRIAGEM_WIZARD);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Atendimento " + atendimento.getId() + " criado e classificacao registrada com sucesso");
            return "redirect:/ui/triagem";
        } catch (IllegalArgumentException ex) {
            populateWizardModel(model, session, wizard, Map.of("global", ex.getMessage()));
            return "pages/care/triage/wizard";
        }
    }

    @GetMapping("/{atendimentoId}")
    public String tela(@PathVariable Long atendimentoId, Model model) {
        requirePermission();
        var triagemAbertaOpt = assistencialFlowService.buscarTriagemAberta(atendimentoId);
        boolean triagemAberta = triagemAbertaOpt.isPresent();
        boolean possuiClassificacaoFinalizada = assistencialFlowService.buscarUltimaClassificacaoFinalizada(atendimentoId).isPresent();
        boolean modoReclassificacao = !triagemAberta && possuiClassificacaoFinalizada;

        if (!triagemAberta && !modoReclassificacao) {
            try {
                assistencialFlowService.iniciarTriagem(atendimentoId);
                triagemAbertaOpt = assistencialFlowService.buscarTriagemAberta(atendimentoId);
                triagemAberta = triagemAbertaOpt.isPresent();
            } catch (IllegalArgumentException ex) {
                model.addAttribute("errorMessage", ex.getMessage());
            }
        }

        Atendimento atendimento = assistencialFlowService.buscarAtendimento(atendimentoId);
        model.addAttribute("atendimento", atendimento);
        model.addAttribute("triagemAberta", triagemAberta);
        model.addAttribute("modoReclassificacao", modoReclassificacao);
        model.addAttribute("inicioTriagemCapturada", triagemAbertaOpt.map(ClassificacaoRisco::getDataInicio).orElse(null));
        model.addAttribute("classificacaoResumo",
                assistencialFlowService.mapaUltimaClassificacao(java.util.List.of(atendimentoId)).get(atendimentoId));
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new TriagemForm());
        }
        model.addAttribute("classificacaoCores", classificacaoCorRepository.findByAtivoTrueOrderByOrdemExibicaoAscDescricaoAsc());
        populateTriagemLookups(model);
        return "pages/care/triage/form";
    }

    @PostMapping("/{atendimentoId}/iniciar")
    public String iniciar(@PathVariable Long atendimentoId, RedirectAttributes redirectAttributes) {
        requirePermission();
        try {
            assistencialFlowService.iniciarTriagem(atendimentoId);
            redirectAttributes.addFlashAttribute("successMessage", "Triagem iniciada");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/ui/triagem/" + atendimentoId;
    }

    @PostMapping("/{atendimentoId}/finalizar")
    public String finalizar(@PathVariable Long atendimentoId,
                            @Valid @ModelAttribute("form") TriagemForm form,
                            BindingResult bindingResult,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        requirePermission();
        boolean triagemAberta = assistencialFlowService.buscarTriagemAberta(atendimentoId).isPresent();
        boolean possuiClassificacaoFinalizada = assistencialFlowService.buscarUltimaClassificacaoFinalizada(atendimentoId).isPresent();
        if (!triagemAberta && possuiClassificacaoFinalizada) {
            redirectAttributes.addFlashAttribute("errorMessage", "Classificacao inicial ja finalizada. Use reclassificacao.");
            return "redirect:/ui/triagem/" + atendimentoId;
        }
        applyCustomAlergiaValidation(form, bindingResult, "");
        if (bindingResult.hasErrors()) {
            model.addAttribute("atendimento", assistencialFlowService.buscarAtendimento(atendimentoId));
            model.addAttribute("triagemAberta", true);
            model.addAttribute("modoReclassificacao", false);
            model.addAttribute("classificacaoCores", classificacaoCorRepository.findByAtivoTrueOrderByOrdemExibicaoAscDescricaoAsc());
            populateTriagemLookups(model);
            return "pages/care/triage/form";
        }
        try {
            assistencialFlowService.finalizarTriagem(atendimentoId, form);
            redirectAttributes.addFlashAttribute("successMessage", "Triagem finalizada");
            return "redirect:/ui/triagem";
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/ui/triagem/" + atendimentoId;
        }
    }

    @PostMapping("/{atendimentoId}/reclassificar")
    public String reclassificar(@PathVariable Long atendimentoId,
                                @ModelAttribute("form") TriagemForm form,
                                BindingResult bindingResult,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        requirePermission();
        boolean triagemAberta = assistencialFlowService.buscarTriagemAberta(atendimentoId).isPresent();
        if (triagemAberta) {
            redirectAttributes.addFlashAttribute("errorMessage", "Finalize a classificacao inicial antes de reclassificar.");
            return "redirect:/ui/triagem/" + atendimentoId;
        }
        if (assistencialFlowService.buscarUltimaClassificacaoFinalizada(atendimentoId).isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Atendimento sem classificacao inicial finalizada.");
            return "redirect:/ui/triagem/" + atendimentoId;
        }
        applyReclassificacaoValidation(form, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("atendimento", assistencialFlowService.buscarAtendimento(atendimentoId));
            model.addAttribute("triagemAberta", false);
            model.addAttribute("modoReclassificacao", true);
            model.addAttribute("classificacaoCores", classificacaoCorRepository.findByAtivoTrueOrderByOrdemExibicaoAscDescricaoAsc());
            populateTriagemLookups(model);
            return "pages/care/triage/form";
        }
        try {
            assistencialFlowService.registrarReclassificacao(atendimentoId, form);
            redirectAttributes.addFlashAttribute("successMessage", "Reclassificacao registrada");
            return "redirect:/ui/triagem";
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/ui/triagem/" + atendimentoId;
        }
    }

    private void requirePermission() {
        if (!operationalPermissionService.has(
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication(),
                OperationalPermissionService.PERM_TRIAGEM_EXECUTAR)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Sem permissao para triagem");
        }
    }

    private void requireWizardPermission() {
        requirePermission();
        if (!operationalPermissionService.canCriarAtendimento(
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Sem permissao para criar atendimento");
        }
        if (!unidadeComecaNaTriagem()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "A unidade atual nao inicia o fluxo pela classificacao");
        }
    }

    private Long unidadeAtual() {
        return unidadeContext.getUnidadeAtual()
                .orElseThrow(() -> new IllegalArgumentException("Unidade atual nao definida"));
    }

    private boolean unidadeComecaNaTriagem() {
        return unidadeConfigFluxoRepository.findById(unidadeAtual())
                .map(config -> config.getPrimeiroPasso() == PrimeiroPassoFluxo.TRIAGEM)
                .orElse(false);
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
        model.addAttribute("classificacaoCores", classificacaoCorRepository.findByAtivoTrueOrderByOrdemExibicaoAscDescricaoAsc());
        model.addAttribute("horaChegadaCapturada", lookupChegadaToken(session, wizard.getChegadaToken()));
        model.addAttribute("wizardErrors", errors);
        model.addAttribute("sexos", pacienteLookupService.listarSexos());
        model.addAttribute("patientSummary", buildPatientSummary(wizard));
        populateTriagemLookups(model);
    }

    private List<TipoAtendimento> tiposPermitidosOrdenados(
            org.springframework.security.core.Authentication authentication) {
        Set<TipoAtendimento> permitidos = operationalPermissionService.tiposPermitidosCriarAtendimento(authentication);
        List<TipoAtendimento> ordenados = new ArrayList<>(permitidos);
        ordenados.sort(java.util.Comparator.comparing(Enum::name));
        return ordenados;
    }

    private AtendimentoWizardForm getOrCreateWizard(HttpSession session, Long pacienteId) {
        Object attr = session.getAttribute(SESSION_TRIAGEM_WIZARD);
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
        session.setAttribute(SESSION_TRIAGEM_WIZARD, wizard);
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

            String nome = normalize(wizard.getNovoPacienteNome());
            String cpf = CpfUtils.digitsOnly(wizard.getNovoPacienteCpf());
            String sexo = defaultSexo(wizard.getNovoPacienteSexo());
            if (nome == null) {
                errors.put("novoPacienteNome", "Nome do paciente e obrigatorio.");
            }
            if (cpf == null) {
                errors.put("novoPacienteCpf", "CPF do paciente e obrigatorio.");
            } else if (!CpfUtils.isValid(cpf)) {
                errors.put("novoPacienteCpf", "CPF invalido.");
            } else if (pacienteService.buscarDefinitivoAtivoPorCpf(cpf, null).isPresent()) {
                errors.put("novoPacienteCpf", "Ja existe paciente ativo com este CPF. Use a busca por CPF.");
            }
            if (!sexo.matches("M|F|NI")) {
                errors.put("novoPacienteSexo", "Sexo invalido.");
            }
            wizard.setNovoPacienteNome(nome);
            wizard.setNovoPacienteCpf(cpf);
            wizard.setNovoPacienteSexo(sexo);
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

    private Map<String, String> validateTriagemStep(AtendimentoWizardForm wizard) {
        Map<String, String> errors = new LinkedHashMap<>();
        TriagemForm form = wizard.getTriagemForm();
        if (form.getClassificacaoCorId() == null) {
            errors.put("triagemForm.classificacaoCorId", "Classificacao de risco e obrigatoria.");
        }
        if (form.getReguaDorId() == null) {
            errors.put("triagemForm.reguaDorId", "Escala de dor e obrigatoria.");
        }
        if (normalize(form.getQueixaPrincipal()) == null) {
            errors.put("triagemForm.queixaPrincipal", "Queixa principal e obrigatoria.");
        }
        collectAlergiaValidationErrors(form, errors, "triagemForm.");
        collectGlasgowValidationErrors(form, errors, "triagemForm.");
        return errors;
    }

    private Long resolvePacienteId(AtendimentoWizardForm wizard) {
        if (Boolean.TRUE.equals(wizard.getPacienteIdentificado())) {
            if (wizard.getPacienteSelecionadoId() != null) {
                return wizard.getPacienteSelecionadoId();
            }
            PacienteForm form = new PacienteForm();
            form.setTemporario(false);
            form.setNome(wizard.getNovoPacienteNome());
            form.setCpf(wizard.getNovoPacienteCpf());
            form.setCns(normalize(wizard.getNovoPacienteCns()));
            form.setDataNascimento(wizard.getNovoPacienteDataNascimento());
            form.setSexo(defaultSexo(wizard.getNovoPacienteSexo()));
            form.setTelefone(normalize(wizard.getNovoPacienteTelefone()));
            form.setNomeMae(normalize(wizard.getNovoPacienteNomeMae()));
            form.setNomePai(normalize(wizard.getNovoPacienteNomePai()));
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
            summary.put("dataNascimento", paciente.getDataNascimento() == null ? "-" : paciente.getDataNascimento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            summary.put("telefone", valueOrDash(paciente.getTelefone()));
            summary.put("tipo", "PACIENTE IDENTIFICADO");
            return summary;
        }
        if (Boolean.TRUE.equals(wizard.getPacienteIdentificado())) {
            summary.put("nome", valueOrDash(wizard.getNovoPacienteNome()));
            summary.put("nomeMae", valueOrDash(wizard.getNovoPacienteNomeMae()));
            summary.put("nomePai", valueOrDash(wizard.getNovoPacienteNomePai()));
            summary.put("cpf", valueOrDash(wizard.getNovoPacienteCpf()));
            summary.put("cns", valueOrDash(wizard.getNovoPacienteCns()));
            summary.put("dataNascimento", wizard.getNovoPacienteDataNascimento() == null ? "-" : wizard.getNovoPacienteDataNascimento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            summary.put("telefone", valueOrDash(wizard.getNovoPacienteTelefone()));
            summary.put("tipo", "NOVO PACIENTE IDENTIFICADO");
            return summary;
        }

        summary.put("nome", "Paciente nao identificado");
        summary.put("sexo", valueOrDash(wizard.getSexoTemporario()));
        summary.put("idadeAparente", wizard.getIdadeAparenteTemporario() == null ? "-" : String.valueOf(wizard.getIdadeAparenteTemporario()));
        summary.put("tipo", "PACIENTE TEMPORARIO");
        return summary;
    }

    private void syncPacienteStep(AtendimentoWizardForm wizard, AtendimentoWizardForm submitted) {
        wizard.setNovoPacienteNome(submitted.getNovoPacienteNome());
        wizard.setNovoPacienteCpf(submitted.getNovoPacienteCpf());
        wizard.setNovoPacienteCns(submitted.getNovoPacienteCns());
        wizard.setNovoPacienteDataNascimento(submitted.getNovoPacienteDataNascimento());
        wizard.setNovoPacienteSexo(submitted.getNovoPacienteSexo());
        wizard.setNovoPacienteTelefone(submitted.getNovoPacienteTelefone());
        wizard.setNovoPacienteNomeMae(submitted.getNovoPacienteNomeMae());
        wizard.setNovoPacienteNomePai(submitted.getNovoPacienteNomePai());
        wizard.setSexoTemporario(submitted.getSexoTemporario());
        wizard.setIdadeAparenteTemporario(submitted.getIdadeAparenteTemporario());
    }

    private void syncTriagemStep(AtendimentoWizardForm wizard, AtendimentoWizardForm submitted) {
        TriagemForm origem = submitted.getTriagemForm();
        TriagemForm destino = wizard.getTriagemForm();
        destino.setClassificacaoCorId(origem.getClassificacaoCorId());
        destino.setPressaoArterial(origem.getPressaoArterial());
        destino.setTemperatura(origem.getTemperatura());
        destino.setFrequenciaCardiaca(origem.getFrequenciaCardiaca());
        destino.setSaturacaoO2(origem.getSaturacaoO2());
        destino.setFrequenciaRespiratoria(origem.getFrequenciaRespiratoria());
        destino.setSaturacaoO2ComTerapiaO2(origem.getSaturacaoO2ComTerapiaO2());
        destino.setSaturacaoO2Aa(origem.getSaturacaoO2Aa());
        destino.setGlicemiaCapilar(origem.getGlicemiaCapilar());
        destino.setPesoKg(origem.getPesoKg());
        destino.setAlturaCm(origem.getAlturaCm());
        destino.setHgt(origem.getHgt());
        destino.setPerfusaoCapilarPerifericaSeg(origem.getPerfusaoCapilarPerifericaSeg());
        destino.setPreenchimentoCapilarCentralSeg(origem.getPreenchimentoCapilarCentralSeg());
        destino.setReguaDorId(origem.getReguaDorId());
        destino.setGlasgowAberturaOcularId(origem.getGlasgowAberturaOcularId());
        destino.setGlasgowRespostaVerbalId(origem.getGlasgowRespostaVerbalId());
        destino.setGlasgowRespostaMotoraId(origem.getGlasgowRespostaMotoraId());
        destino.setGlasgowRespostaPupilarId(origem.getGlasgowRespostaPupilarId());
        destino.setGlasgowTotal(origem.getGlasgowTotal());
        destino.setQueixaPrincipal(origem.getQueixaPrincipal());
        destino.setMedicacoesUsoContinuo(origem.getMedicacoesUsoContinuo());
        destino.setDiscriminador(origem.getDiscriminador());
        destino.setObservacao(origem.getObservacao());
        destino.setAlergias(cloneAlergias(origem.getAlergias()));
        destino.setComorbidadeIds(origem.getComorbidadeIds() == null ? new ArrayList<>() : new ArrayList<>(origem.getComorbidadeIds()));
        destino.setAvcSinalAlertaIds(origem.getAvcSinalAlertaIds() == null ? new ArrayList<>() : new ArrayList<>(origem.getAvcSinalAlertaIds()));
    }

    private void populateTriagemLookups(Model model) {
        var alergiaSubstancias = alergiaSubstanciaRepository.findByAtivoTrueOrderByDescricaoAsc();
        var alergiaSeveridades = alergiaSeveridadeRepository.findByAtivoTrueOrderByDescricaoAsc();
        var comorbidades = comorbidadeRepository.findByAtivoTrueOrderByDescricaoAsc();
        var avcSinaisAlerta = avcSinalAlertaRepository.findByAtivoTrueOrderByIdAsc();
        var reguasDor = reguaDorRepository.findByAtivoTrueOrderByValorAsc();
        var glasgowAberturaOcular = glasgowAberturaOcularRepository.findByAtivoTrueOrderByPontosDesc();
        var glasgowRespostaVerbal = glasgowRespostaVerbalRepository.findByAtivoTrueOrderByPontosDesc();
        var glasgowRespostaMotora = glasgowRespostaMotoraRepository.findByAtivoTrueOrderByPontosDesc();
        var glasgowRespostaPupilar = glasgowRespostaPupilarRepository.findByAtivoTrueOrderByPontosAsc();
        model.addAttribute("alergiaSubstancias", alergiaSubstancias);
        model.addAttribute("alergiaSeveridades", alergiaSeveridades);
        model.addAttribute("comorbidades", comorbidades);
        model.addAttribute("avcSinaisAlerta", avcSinaisAlerta);
        model.addAttribute("reguasDor", reguasDor);
        model.addAttribute("glasgowAberturaOcular", glasgowAberturaOcular);
        model.addAttribute("glasgowRespostaVerbal", glasgowRespostaVerbal);
        model.addAttribute("glasgowRespostaMotora", glasgowRespostaMotora);
        model.addAttribute("glasgowRespostaPupilar", glasgowRespostaPupilar);
        model.addAttribute("alergiaSubstanciaMap", alergiaSubstancias.stream()
                .collect(Collectors.toMap(item -> item.getId(), item -> item.getDescricao())));
        model.addAttribute("alergiaSeveridadeMap", alergiaSeveridades.stream()
                .collect(Collectors.toMap(item -> item.getId(), item -> item.getDescricao())));
        model.addAttribute("comorbidadeMap", comorbidades.stream()
                .collect(Collectors.toMap(item -> item.getId(), item -> item.getDescricao())));
        model.addAttribute("avcSinalAlertaMap", avcSinaisAlerta.stream()
                .collect(Collectors.toMap(AvcSinalAlerta::getId, AvcSinalAlerta::getDescricao)));
        model.addAttribute("reguaDorMap", reguasDor.stream()
                .collect(Collectors.toMap(ReguaDor::getId, ReguaDor::getValor)));
        model.addAttribute("glasgowAberturaOcularMap", glasgowAberturaOcular.stream()
                .collect(Collectors.toMap(GlasgowAberturaOcular::getId, GlasgowAberturaOcular::getPontos)));
        model.addAttribute("glasgowRespostaVerbalMap", glasgowRespostaVerbal.stream()
                .collect(Collectors.toMap(GlasgowRespostaVerbal::getId, GlasgowRespostaVerbal::getPontos)));
        model.addAttribute("glasgowRespostaMotoraMap", glasgowRespostaMotora.stream()
                .collect(Collectors.toMap(GlasgowRespostaMotora::getId, GlasgowRespostaMotora::getPontos)));
        model.addAttribute("glasgowRespostaPupilarMap", glasgowRespostaPupilar.stream()
                .collect(Collectors.toMap(GlasgowRespostaPupilar::getId, GlasgowRespostaPupilar::getPontos)));
    }

    private void applyCustomAlergiaValidation(TriagemForm form, BindingResult bindingResult, String fieldPrefix) {
        Map<String, String> errors = new LinkedHashMap<>();
        if (form.getClassificacaoCorId() == null) {
            errors.put(fieldPrefix + "classificacaoCorId", "Classificacao de risco e obrigatoria.");
        }
        if (normalize(form.getQueixaPrincipal()) == null) {
            errors.put(fieldPrefix + "queixaPrincipal", "Queixa principal e obrigatoria.");
        }
        int beforeAlergia = errors.size();
        collectAlergiaValidationErrors(form, errors, fieldPrefix);
        boolean hasAlergiaError = errors.size() > beforeAlergia;
        collectGlasgowValidationErrors(form, errors, fieldPrefix);
        if (form.getReguaDorId() == null) {
            errors.put(fieldPrefix + "reguaDorId", "Escala de dor e obrigatoria.");
        }
        if (hasAlergiaError) {
            String mensagemAlergia = errors.entrySet().stream()
                    .filter(entry -> entry.getKey().contains("alergias["))
                    .map(Map.Entry::getValue)
                    .findFirst()
                    .orElse("Corrija os dados de alergia.");
            errors.putIfAbsent(fieldPrefix + "alergias", mensagemAlergia);
        }
        errors.forEach((field, message) -> bindingResult.rejectValue(field, "invalid", message));
    }

    private void applyReclassificacaoValidation(TriagemForm form, BindingResult bindingResult) {
        Map<String, String> errors = new LinkedHashMap<>();
        if (form.getClassificacaoCorId() == null) {
            errors.put("classificacaoCorId", "Classificacao de risco e obrigatoria.");
        }
        if (form.getReguaDorId() == null) {
            errors.put("reguaDorId", "Escala de dor e obrigatoria.");
        }
        collectGlasgowValidationErrors(form, errors, "");
        errors.forEach((field, message) -> bindingResult.rejectValue(field, "invalid", message));
    }

    private void collectGlasgowValidationErrors(TriagemForm form, Map<String, String> errors, String fieldPrefix) {
        Long ocularId = form.getGlasgowAberturaOcularId();
        Long verbalId = form.getGlasgowRespostaVerbalId();
        Long motoraId = form.getGlasgowRespostaMotoraId();
        Long pupilarId = form.getGlasgowRespostaPupilarId();

        boolean any = ocularId != null || verbalId != null || motoraId != null || pupilarId != null;
        if (!any) {
            return;
        }
        if (ocularId == null) {
            errors.put(fieldPrefix + "glasgowAberturaOcularId", "Abertura ocular e obrigatoria ao preencher Glasgow.");
        }
        if (verbalId == null) {
            errors.put(fieldPrefix + "glasgowRespostaVerbalId", "Resposta verbal e obrigatoria ao preencher Glasgow.");
        }
        if (motoraId == null) {
            errors.put(fieldPrefix + "glasgowRespostaMotoraId", "Resposta motora e obrigatoria ao preencher Glasgow.");
        }
    }

    private void collectAlergiaValidationErrors(TriagemForm form, Map<String, String> errors, String fieldPrefix) {
        List<TriagemForm.AlergiaItem> alergias = form.getAlergias() == null ? List.of() : form.getAlergias();
        for (int i = 0; i < alergias.size(); i++) {
            TriagemForm.AlergiaItem alergia = alergias.get(i);
            if (alergia == null) {
                continue;
            }
            if (alergia.getSubstanciaId() == null) {
                if (alergia.getSeveridadeId() != null || normalize(alergia.getDescricao()) != null) {
                    errors.put(fieldPrefix + "alergias[" + i + "].substanciaId",
                            "Selecione a substancia da alergia antes de informar severidade ou detalhe.");
                }
                continue;
            }

            String descricaoSubstancia = alergiaSubstanciaRepository.findById(alergia.getSubstanciaId())
                    .map(item -> normalize(item.getDescricao()))
                    .orElse(null);
            if (descricaoSubstancia == null) {
                errors.put(fieldPrefix + "alergias[" + i + "].substanciaId", "Substancia de alergia invalida.");
                continue;
            }

            boolean naoRelataAlergia = "NAO RELATA ALERGIA".equalsIgnoreCase(descricaoSubstancia);
            if (!naoRelataAlergia && normalize(alergia.getDescricao()) == null) {
                errors.put(fieldPrefix + "alergias[" + i + "].descricao",
                        "Detalhe da alergia e obrigatorio para a substancia selecionada.");
            }
        }
    }

    private List<TriagemForm.AlergiaItem> cloneAlergias(List<TriagemForm.AlergiaItem> origem) {
        if (origem == null || origem.isEmpty()) {
            return new ArrayList<>();
        }
        List<TriagemForm.AlergiaItem> destino = new ArrayList<>();
        for (TriagemForm.AlergiaItem item : origem) {
            if (item == null) {
                continue;
            }
            TriagemForm.AlergiaItem clone = new TriagemForm.AlergiaItem();
            clone.setSubstanciaId(item.getSubstanciaId());
            clone.setSeveridadeId(item.getSeveridadeId());
            clone.setDescricao(item.getDescricao());
            destino.add(clone);
        }
        return destino;
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

    private boolean matchesNome(Atendimento atendimento, String nome) {
        if (nome == null || nome.isBlank()) {
            return true;
        }
        String nomePaciente = atendimento.getPaciente() == null ? "" : atendimento.getPaciente().getNome();
        return nomePaciente != null && nomePaciente.toLowerCase().contains(nome.trim().toLowerCase());
    }

    private boolean matchesCpf(Atendimento atendimento, String cpf) {
        if (cpf == null || cpf.isBlank()) {
            return true;
        }
        String filtro = CpfUtils.digitsOnly(cpf);
        String cpfPaciente = atendimento.getPaciente() == null ? null : atendimento.getPaciente().getCpf();
        String cpfDigits = cpfPaciente == null ? null : CpfUtils.digitsOnly(cpfPaciente);
        return filtro != null && filtro.equals(cpfDigits);
    }

    private boolean matchesAtendimentoId(Atendimento atendimento, Long atendimentoId) {
        if (atendimentoId == null) {
            return true;
        }
        return atendimentoId.equals(atendimento.getId());
    }

    private boolean matchesPeriodo(Atendimento atendimento, LocalDate dataInicio, LocalDate dataFim) {
        if (dataInicio == null && dataFim == null) {
            return true;
        }
        LocalDate dataAtendimento = atendimento.getDataHoraChegada() == null
                ? null
                : atendimento.getDataHoraChegada().toLocalDate();
        if (dataAtendimento == null) {
            return false;
        }
        if (dataInicio != null && dataAtendimento.isBefore(dataInicio)) {
            return false;
        }
        if (dataFim != null && dataAtendimento.isAfter(dataFim)) {
            return false;
        }
        return true;
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
        Object attr = session.getAttribute(SESSION_TRIAGEM_CHEGADAS);
        if (attr instanceof Map<?, ?> map) {
            return (Map<String, LocalDateTime>) map;
        }
        Map<String, LocalDateTime> novoMapa = new HashMap<>();
        session.setAttribute(SESSION_TRIAGEM_CHEGADAS, novoMapa);
        return novoMapa;
    }
}
