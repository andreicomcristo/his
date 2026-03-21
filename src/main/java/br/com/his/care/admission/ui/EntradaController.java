package br.com.his.care.admission.ui;

import java.util.List;

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

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.SmartValidator;
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
import br.com.his.care.admission.dto.EntradaPendenteForm;
import br.com.his.care.admission.dto.EntradaForm;
import br.com.his.care.admission.support.ProcedenciaEntradaRules;
import br.com.his.care.attendance.model.Atendimento;
import br.com.his.care.attendance.service.AssistencialFlowService;
import br.com.his.reference.location.repository.MunicipioRepository;
import br.com.his.reference.location.repository.UnidadeFederativaRepository;
import br.com.his.patient.dto.PacienteForm;
import br.com.his.patient.model.Paciente;
import br.com.his.patient.repository.TipoProcedenciaRepository;
import br.com.his.patient.service.PacienteLookupService;
import br.com.his.patient.service.PacienteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;

@Controller
@RequestMapping("/ui/entradas")
public class EntradaController {

    private final AssistencialFlowService assistencialFlowService;
    private final OperationalPermissionService operationalPermissionService;
    private final UnidadeContext unidadeContext;
    private final AreaRepository areaRepository;
    private final FormaChegadaRepository formaChegadaRepository;
    private final GrauParentescoRepository grauParentescoRepository;
    private final MotivoEntradaRepository motivoEntradaRepository;
    private final SituacaoOcupacionalRepository situacaoOcupacionalRepository;
    private final UnidadeFederativaRepository unidadeFederativaRepository;
    private final PacienteLookupService pacienteLookupService;
    private final PacienteService pacienteService;
    private final MunicipioRepository MunicipioRepository;
    private final TipoProcedenciaRepository tipoProcedenciaRepository;
    private final SmartValidator validator;

    public EntradaController(AssistencialFlowService assistencialFlowService,
                             OperationalPermissionService operationalPermissionService,
                             UnidadeContext unidadeContext,
                             AreaRepository areaRepository,
                             FormaChegadaRepository formaChegadaRepository,
                             GrauParentescoRepository grauParentescoRepository,
                             MotivoEntradaRepository motivoEntradaRepository,
                             SituacaoOcupacionalRepository situacaoOcupacionalRepository,
                             UnidadeFederativaRepository unidadeFederativaRepository,
                             PacienteLookupService pacienteLookupService,
                             PacienteService pacienteService,
                             MunicipioRepository MunicipioRepository,
                             TipoProcedenciaRepository tipoProcedenciaRepository,
                             SmartValidator validator) {
        this.assistencialFlowService = assistencialFlowService;
        this.operationalPermissionService = operationalPermissionService;
        this.unidadeContext = unidadeContext;
        this.areaRepository = areaRepository;
        this.formaChegadaRepository = formaChegadaRepository;
        this.grauParentescoRepository = grauParentescoRepository;
        this.motivoEntradaRepository = motivoEntradaRepository;
        this.situacaoOcupacionalRepository = situacaoOcupacionalRepository;
        this.unidadeFederativaRepository = unidadeFederativaRepository;
        this.pacienteLookupService = pacienteLookupService;
        this.pacienteService = pacienteService;
        this.MunicipioRepository = MunicipioRepository;
        this.tipoProcedenciaRepository = tipoProcedenciaRepository;
        this.validator = validator;
    }

    @GetMapping("/atendimento/{atendimentoId}")
    public String telaPorAtendimento(@PathVariable Long atendimentoId, Model model) {
        requirePermission();
        Atendimento atendimento = assistencialFlowService.buscarAtendimento(atendimentoId);
        List<Area> areasEntrada = areaRepository.findAreasAtivasRecebemEntradaByUnidadeId(atendimento.getUnidade().getId());
        Long areaExecucaoRecepcaoAtualId = resolveAreaExecucaoRecepcaoAtual(atendimento.getUnidade().getId(), areasEntrada);
        if (!model.containsAttribute("form")) {
            EntradaForm form = assistencialFlowService.buscarEntradaPorAtendimento(atendimentoId)
                    .map(entrada -> {
                        EntradaForm f = new EntradaForm();
                        f.setAreaPortaEntradaId(entrada.getAreaPortaEntrada() == null ? null : entrada.getAreaPortaEntrada().getId());
                        f.setAreaExecucaoId(entrada.getAreaExecucao() == null ? null : entrada.getAreaExecucao().getId());
                        f.setTipoProcedenciaId(entrada.getProcedencia() == null || entrada.getProcedencia().getTipoProcedencia() == null
                                ? null : entrada.getProcedencia().getTipoProcedencia().getId());
                        f.setProcedenciaId(entrada.getProcedencia() == null ? null : entrada.getProcedencia().getId());
                        f.setProcedenciaBairroId(entrada.getProcedencia() == null || entrada.getProcedencia().getBairro() == null
                                ? null : entrada.getProcedencia().getBairro().getId());
                        f.setProcedenciaMunicipioUfId(entrada.getProcedencia() == null || entrada.getProcedencia().getMunicipio() == null
                                || entrada.getProcedencia().getMunicipio().getUnidadeFederativa() == null
                                ? null : entrada.getProcedencia().getMunicipio().getUnidadeFederativa().getId());
                        f.setProcedenciaMunicipioId(entrada.getProcedencia() == null || entrada.getProcedencia().getMunicipio() == null
                                ? null : entrada.getProcedencia().getMunicipio().getId());
                        f.setFormaChegadaId(entrada.getFormaChegada() == null ? null : entrada.getFormaChegada().getId());
                        f.setMotivoEntradaId(entrada.getMotivoEntrada() == null ? null : entrada.getMotivoEntrada().getId());
                        f.setTelefoneComunicante(entrada.getTelefoneComunicante());
                        f.setComunicante(entrada.getComunicante());
                        f.setGrauParentescoId(entrada.getGrauParentesco() == null ? null : entrada.getGrauParentesco().getId());
                        f.setInformacaoAdChegada(entrada.getInformacaoAdChegada());
                        f.setProcedenciaObservacao(entrada.getProcedenciaObservacao());
                        f.setSituacaoOcupacionalId(entrada.getSituacaoOcupacional() == null ? null : entrada.getSituacaoOcupacional().getId());
                        f.setProfissaoId(entrada.getProfissao() == null ? null : entrada.getProfissao().getId());
                        f.setProfissaoObservacao(entrada.getProfissaoObservacao());
                        f.setTempoServico(entrada.getTempoServico());
                        f.setObservacoes(entrada.getObservacoes());
                        f.setConvenio(entrada.getConvenio());
                        f.setGuia(entrada.getGuia());
                        return f;
                    })
                    .orElse(new EntradaForm());
            if (form.getProfissaoId() == null
                    && atendimento.getPaciente() != null
                    && atendimento.getPaciente().getProfissao() != null) {
                form.setProfissaoId(atendimento.getPaciente().getProfissao().getId());
            }
            model.addAttribute("form", form);
        }
        applyAreaExecucaoRecepcaoDefault((EntradaForm) model.getAttribute("form"), areaExecucaoRecepcaoAtualId, areasEntrada);
        model.addAttribute("atendimentoId", atendimentoId);
        model.addAttribute("formAction", "/ui/entradas/atendimento/" + atendimentoId);
        model.addAttribute("areasEntrada", areasEntrada);
        model.addAttribute("areasExecucaoRecepcao", areasEntrada);
        model.addAttribute("areaExecucaoRecepcaoAtualId", areaExecucaoRecepcaoAtualId);
        model.addAttribute("tiposProcedenciaEntrada", pacienteLookupService.listarTiposProcedenciaEntrada());
        model.addAttribute("procedenciasEntrada", pacienteLookupService.listarProcedenciasEntrada(atendimento.getUnidade().getId()));
        model.addAttribute("bairrosEntrada", pacienteLookupService.listarBairrosPorMunicipio(
                atendimento.getUnidade().getMunicipio() == null ? null : atendimento.getUnidade().getMunicipio().getId()));
        model.addAttribute("ufsEntrada", unidadeFederativaRepository.findAllByOrderByDescricaoAsc());
        model.addAttribute("municipiosEntrada", pacienteLookupService.listarMunicipiosProcedenciaEntrada());
        model.addAttribute("formasChegada", formaChegadaRepository.findByAtivoTrueOrderByDescricaoAsc());
        model.addAttribute("grausParentesco", grauParentescoRepository.findByAtivoTrueOrderByDescricaoAsc());
        model.addAttribute("motivosEntrada", motivoEntradaRepository.findByAtivoTrueOrderByDescricaoAsc());
        model.addAttribute("situacoesOcupacionais", situacaoOcupacionalRepository.findByAtivoTrueOrderByDescricaoAsc());
        model.addAttribute("profissoes", pacienteLookupService.listarProfissoes());
        return "pages/care/admission/form";
    }

    @GetMapping("/pendente/{atendimentoId}")
    public String telaPendente(@PathVariable Long atendimentoId,
                               @RequestParam(defaultValue = "1") int step,
                               Model model) {
        requirePermission();
        Atendimento atendimento = assistencialFlowService.buscarAtendimento(atendimentoId);
        if (!model.containsAttribute("pendenteForm")) {
            EntradaPendenteForm pendenteForm = new EntradaPendenteForm();
            copyPacienteToForm(atendimento.getPaciente(), pendenteForm.getPacienteForm());
            assistencialFlowService.buscarEntradaPorAtendimento(atendimentoId)
                    .ifPresent(entrada -> copyEntradaToForm(entrada, pendenteForm.getEntradaForm()));
            if (pendenteForm.getEntradaForm().getProfissaoId() == null
                    && atendimento.getPaciente() != null
                    && atendimento.getPaciente().getProfissao() != null) {
                pendenteForm.getEntradaForm().setProfissaoId(atendimento.getPaciente().getProfissao().getId());
            }
            model.addAttribute("pendenteForm", pendenteForm);
        }
        populatePendenteModel(model, atendimento, (EntradaPendenteForm) model.getAttribute("pendenteForm"), normalizeStep(step));
        return "pages/care/admission/pendente-wizard";
    }

    @PostMapping("/atendimento/{atendimentoId}")
    public String salvarPorAtendimento(@PathVariable Long atendimentoId,
                                       @Valid @ModelAttribute("form") EntradaForm form,
                                       BindingResult bindingResult,
                                       Model model,
                                       RedirectAttributes redirectAttributes) {
        requirePermission();
        Atendimento atendimento = assistencialFlowService.buscarAtendimento(atendimentoId);
        List<Area> areasEntrada = areaRepository.findAreasAtivasRecebemEntradaByUnidadeId(atendimento.getUnidade().getId());
        Long areaExecucaoRecepcaoAtualId = resolveAreaExecucaoRecepcaoAtual(atendimento.getUnidade().getId(), areasEntrada);
        applyAreaExecucaoRecepcaoDefault(form, areaExecucaoRecepcaoAtualId, areasEntrada);
        if (form.getAreaPortaEntradaId() != null && !isAreaEntradaValida(form.getAreaPortaEntradaId(), areasEntrada)) {
            bindingResult.rejectValue("areaPortaEntradaId", "entrada.areaPortaEntrada.invalid", "Porta de entrada invalida para a unidade atual.");
        }
        if (form.getAreaExecucaoId() != null && !isAreaEntradaValida(form.getAreaExecucaoId(), areasEntrada)) {
            bindingResult.rejectValue("areaExecucaoId", "entrada.areaExecucao.invalid", "Area de execucao invalida para a unidade atual.");
        }
        validateProcedencia(form, bindingResult, "");
        if (bindingResult.hasErrors()) {
            model.addAttribute("atendimentoId", atendimentoId);
            model.addAttribute("formAction", "/ui/entradas/atendimento/" + atendimentoId);
            model.addAttribute("areasEntrada", areasEntrada);
            model.addAttribute("areasExecucaoRecepcao", areasEntrada);
            model.addAttribute("areaExecucaoRecepcaoAtualId", areaExecucaoRecepcaoAtualId);
            model.addAttribute("tiposProcedenciaEntrada", pacienteLookupService.listarTiposProcedenciaEntrada());
            model.addAttribute("procedenciasEntrada", pacienteLookupService.listarProcedenciasEntrada(atendimento.getUnidade().getId()));
            model.addAttribute("bairrosEntrada", pacienteLookupService.listarBairrosPorMunicipio(
                    atendimento.getUnidade().getMunicipio() == null ? null : atendimento.getUnidade().getMunicipio().getId()));
            model.addAttribute("ufsEntrada", unidadeFederativaRepository.findAllByOrderByDescricaoAsc());
            model.addAttribute("municipiosEntrada", pacienteLookupService.listarMunicipiosProcedenciaEntrada());
            model.addAttribute("formasChegada", formaChegadaRepository.findByAtivoTrueOrderByDescricaoAsc());
            model.addAttribute("grausParentesco", grauParentescoRepository.findByAtivoTrueOrderByDescricaoAsc());
            model.addAttribute("motivosEntrada", motivoEntradaRepository.findByAtivoTrueOrderByDescricaoAsc());
            model.addAttribute("situacoesOcupacionais", situacaoOcupacionalRepository.findByAtivoTrueOrderByDescricaoAsc());
            model.addAttribute("profissoes", pacienteLookupService.listarProfissoes());
            return "pages/care/admission/form";
        }
        try {
            assistencialFlowService.registrarEntradaPorAtendimento(atendimentoId, form);
            rememberAreaExecucaoRecepcao(form.getAreaExecucaoId());
            redirectAttributes.addFlashAttribute("successMessage", "Entrada registrada");
            return "redirect:/ui/atendimentos";
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/ui/entradas/atendimento/" + atendimentoId;
        }
    }

    @PostMapping("/pendente/{atendimentoId}/paciente")
    public String salvarPendentePaciente(@PathVariable Long atendimentoId,
                                 @ModelAttribute("pendenteForm") EntradaPendenteForm pendenteForm,
                                 BindingResult bindingResult,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        requirePermission();
        Atendimento atendimento = assistencialFlowService.buscarAtendimento(atendimentoId);
        bindingResult.pushNestedPath("pacienteForm");
        validator.validate(pendenteForm.getPacienteForm(), bindingResult);
        bindingResult.popNestedPath();
        if (bindingResult.hasErrors()) {
            populatePendenteModel(model, atendimento, pendenteForm, 1);
            return "pages/care/admission/pendente-wizard";
        }
        try {
            pacienteService.atualizarPaciente(atendimento.getPaciente().getId(), pendenteForm.getPacienteForm());
            redirectAttributes.addFlashAttribute("successMessage", "Paciente atualizado");
            return "redirect:/ui/entradas/pendente/" + atendimentoId + "?step=2";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            populatePendenteModel(model, atendimento, pendenteForm, 1);
            return "pages/care/admission/pendente-wizard";
        }
    }

    @PostMapping("/pendente/{atendimentoId}/entrada")
    public String salvarPendenteEntrada(@PathVariable Long atendimentoId,
                                        @ModelAttribute("pendenteForm") EntradaPendenteForm pendenteForm,
                                        BindingResult bindingResult,
                                        Model model,
                                        RedirectAttributes redirectAttributes) {
        requirePermission();
        Atendimento atendimento = assistencialFlowService.buscarAtendimento(atendimentoId);
        List<Area> areasEntrada = areaRepository.findAreasAtivasRecebemEntradaByUnidadeId(atendimento.getUnidade().getId());
        Long areaExecucaoRecepcaoAtualId = resolveAreaExecucaoRecepcaoAtual(atendimento.getUnidade().getId(), areasEntrada);
        applyAreaExecucaoRecepcaoDefault(pendenteForm.getEntradaForm(), areaExecucaoRecepcaoAtualId, areasEntrada);
        bindingResult.pushNestedPath("entradaForm");
        validator.validate(pendenteForm.getEntradaForm(), bindingResult);
        bindingResult.popNestedPath();
        if (pendenteForm.getEntradaForm().getAreaPortaEntradaId() != null
                && !isAreaEntradaValida(pendenteForm.getEntradaForm().getAreaPortaEntradaId(), areasEntrada)) {
            bindingResult.rejectValue("entradaForm.areaPortaEntradaId",
                    "entrada.areaPortaEntrada.invalid",
                    "Porta de entrada invalida para a unidade atual.");
        }
        if (pendenteForm.getEntradaForm().getAreaExecucaoId() != null
                && !isAreaEntradaValida(pendenteForm.getEntradaForm().getAreaExecucaoId(), areasEntrada)) {
            bindingResult.rejectValue("entradaForm.areaExecucaoId",
                    "entrada.areaExecucao.invalid",
                    "Area de execucao invalida para a unidade atual.");
        }
        validateProcedencia(pendenteForm.getEntradaForm(), bindingResult, "entradaForm.");
        if (bindingResult.hasErrors()) {
            populatePendenteModel(model, atendimento, pendenteForm, 2);
            return "pages/care/admission/pendente-wizard";
        }
        try {
            assistencialFlowService.registrarEntradaPorAtendimento(atendimentoId, pendenteForm.getEntradaForm());
            rememberAreaExecucaoRecepcao(pendenteForm.getEntradaForm().getAreaExecucaoId());
            redirectAttributes.addFlashAttribute("successMessage", "Entrada registrada");
            return "redirect:/ui/atendimentos/pendentes-entrada";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            populatePendenteModel(model, atendimento, pendenteForm, 2);
            return "pages/care/admission/pendente-wizard";
        }
    }

    private void populatePendenteModel(Model model, Atendimento atendimento, EntradaPendenteForm pendenteForm, int currentStep) {
        List<Area> areasEntrada = areaRepository.findAreasAtivasRecebemEntradaByUnidadeId(atendimento.getUnidade().getId());
        Long areaExecucaoRecepcaoAtualId = resolveAreaExecucaoRecepcaoAtual(atendimento.getUnidade().getId(), areasEntrada);
        applyAreaExecucaoRecepcaoDefault(pendenteForm.getEntradaForm(), areaExecucaoRecepcaoAtualId, areasEntrada);
        model.addAttribute("atendimento", atendimento);
        model.addAttribute("classificacao", assistencialFlowService.mapaUltimaClassificacao(java.util.List.of(atendimento.getId())).get(atendimento.getId()));
        model.addAttribute("currentStep", currentStep);
        model.addAttribute("areasEntrada", areasEntrada);
        model.addAttribute("areasExecucaoRecepcao", areasEntrada);
        model.addAttribute("areaExecucaoRecepcaoAtualId", areaExecucaoRecepcaoAtualId);
        model.addAttribute("tiposProcedenciaEntrada", pacienteLookupService.listarTiposProcedenciaEntrada());
        model.addAttribute("procedenciasEntrada", pacienteLookupService.listarProcedenciasEntrada(atendimento.getUnidade().getId()));
        model.addAttribute("bairrosEntrada", pacienteLookupService.listarBairrosPorMunicipio(
                atendimento.getUnidade().getMunicipio() == null ? null : atendimento.getUnidade().getMunicipio().getId()));
        model.addAttribute("ufsEntrada", unidadeFederativaRepository.findAllByOrderByDescricaoAsc());
        model.addAttribute("municipiosEntrada", pacienteLookupService.listarMunicipiosProcedenciaEntrada());
        model.addAttribute("formasChegada", formaChegadaRepository.findByAtivoTrueOrderByDescricaoAsc());
        model.addAttribute("grausParentesco", grauParentescoRepository.findByAtivoTrueOrderByDescricaoAsc());
        model.addAttribute("motivosEntrada", motivoEntradaRepository.findByAtivoTrueOrderByDescricaoAsc());
        model.addAttribute("situacoesOcupacionais", situacaoOcupacionalRepository.findByAtivoTrueOrderByDescricaoAsc());
        model.addAttribute("profissoes", pacienteLookupService.listarProfissoes());
        populatePacienteLookups(model, pendenteForm.getPacienteForm());
    }

    private int normalizeStep(int step) {
        return step == 2 ? 2 : 1;
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
        model.addAttribute("ufs", unidadeFederativaRepository.findAllByOrderByDescricaoAsc());
        model.addAttribute("municipios", form.getUnidadeFederativaId() == null
                ? java.util.List.of()
                : MunicipioRepository.findByUnidadeFederativaIdOrderByDescricao(form.getUnidadeFederativaId()));
    }

    private void copyPacienteToForm(Paciente paciente, PacienteForm form) {
        form.setNome(paciente.getNome());
        form.setNomeSocial(paciente.getNomeSocial());
        form.setCpf(paciente.getCpf());
        form.setCns(paciente.getCns());
        form.setRg(paciente.getRg());
        form.setDataNascimento(paciente.getDataNascimento());
        form.setSexo(paciente.getSexo());
        form.setTelefone(paciente.getTelefone());
        form.setNomeMae(paciente.getNomeMae());
        form.setNomePai(paciente.getNomePai());
        form.setRacaCorId(paciente.getRacaCor() == null ? null : paciente.getRacaCor().getId());
        form.setEtniaIndigenaId(paciente.getEtniaIndigena() == null ? null : paciente.getEtniaIndigena().getId());
        form.setNacionalidadeId(paciente.getNacionalidade() == null ? null : paciente.getNacionalidade().getId());
        form.setNaturalidadeId(paciente.getNaturalidade() == null ? null : paciente.getNaturalidade().getId());
        form.setEstadoCivilId(paciente.getEstadoCivil() == null ? null : paciente.getEstadoCivil().getId());
        form.setEscolaridadeId(paciente.getEscolaridade() == null ? null : paciente.getEscolaridade().getId());
        form.setTipoSanguineoId(paciente.getTipoSanguineo() == null ? null : paciente.getTipoSanguineo().getId());
        form.setOrientacaoSexualId(paciente.getOrientacaoSexual() == null ? null : paciente.getOrientacaoSexual().getId());
        form.setIdentidadeGeneroId(paciente.getIdentidadeGenero() == null ? null : paciente.getIdentidadeGenero().getId());
        form.setDeficienciaId(paciente.getDeficiencia() == null ? null : paciente.getDeficiencia().getId());
        form.setProfissaoId(paciente.getProfissao() == null ? null : paciente.getProfissao().getId());
        form.setProcedenciaId(paciente.getProcedencia() == null ? null : paciente.getProcedencia().getId());
        form.setEmail(paciente.getEmail());
        form.setObservacoes(paciente.getObservacoes());
        form.setCep(paciente.getCep());
        form.setLogradouro(paciente.getLogradouro());
        form.setNumero(paciente.getNumero());
        form.setComplemento(paciente.getComplemento());
        form.setBairro(paciente.getBairro());
        if (paciente.getMunicipio() != null) {
            form.setMunicipioId(paciente.getMunicipio().getId());
            form.setUnidadeFederativaId(paciente.getMunicipio().getUnidadeFederativa().getId());
        }
        form.setTemporario(paciente.isTemporario());
        form.setIdadeAparente(paciente.getIdadeAparente());
    }

    private void copyEntradaToForm(br.com.his.care.admission.model.Entrada entrada, EntradaForm form) {
        form.setAreaPortaEntradaId(entrada.getAreaPortaEntrada() == null ? null : entrada.getAreaPortaEntrada().getId());
        form.setAreaExecucaoId(entrada.getAreaExecucao() == null ? null : entrada.getAreaExecucao().getId());
        form.setTipoProcedenciaId(entrada.getProcedencia() == null || entrada.getProcedencia().getTipoProcedencia() == null
                ? null : entrada.getProcedencia().getTipoProcedencia().getId());
        form.setProcedenciaId(entrada.getProcedencia() == null ? null : entrada.getProcedencia().getId());
        form.setProcedenciaBairroId(entrada.getProcedencia() == null || entrada.getProcedencia().getBairro() == null
                ? null : entrada.getProcedencia().getBairro().getId());
        form.setProcedenciaMunicipioUfId(entrada.getProcedencia() == null || entrada.getProcedencia().getMunicipio() == null
                || entrada.getProcedencia().getMunicipio().getUnidadeFederativa() == null
                ? null : entrada.getProcedencia().getMunicipio().getUnidadeFederativa().getId());
        form.setProcedenciaMunicipioId(entrada.getProcedencia() == null || entrada.getProcedencia().getMunicipio() == null
                ? null : entrada.getProcedencia().getMunicipio().getId());
        form.setFormaChegadaId(entrada.getFormaChegada() == null ? null : entrada.getFormaChegada().getId());
        form.setMotivoEntradaId(entrada.getMotivoEntrada() == null ? null : entrada.getMotivoEntrada().getId());
        form.setTelefoneComunicante(entrada.getTelefoneComunicante());
        form.setComunicante(entrada.getComunicante());
        form.setGrauParentescoId(entrada.getGrauParentesco() == null ? null : entrada.getGrauParentesco().getId());
        form.setInformacaoAdChegada(entrada.getInformacaoAdChegada());
        form.setProcedenciaObservacao(entrada.getProcedenciaObservacao());
        form.setSituacaoOcupacionalId(entrada.getSituacaoOcupacional() == null ? null : entrada.getSituacaoOcupacional().getId());
        form.setProfissaoId(entrada.getProfissao() == null ? null : entrada.getProfissao().getId());
        form.setProfissaoObservacao(entrada.getProfissaoObservacao());
        form.setTempoServico(entrada.getTempoServico());
        form.setObservacoes(entrada.getObservacoes());
        form.setConvenio(entrada.getConvenio());
        form.setGuia(entrada.getGuia());
    }

    private void requirePermission() {
        if (!operationalPermissionService.has(
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication(),
                OperationalPermissionService.PERM_ENTRADA_REGISTRAR)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Sem permissao para registrar entrada");
        }
    }

    private void validateProcedencia(EntradaForm form, BindingResult bindingResult, String fieldPrefix) {
        if (form.getTipoProcedenciaId() == null) {
            return;
        }

        var tipoProcedenciaOpt = tipoProcedenciaRepository.findById(form.getTipoProcedenciaId());
        if (tipoProcedenciaOpt.isEmpty()) {
            bindingResult.rejectValue(fieldPrefix + "tipoProcedenciaId",
                    "entrada.tipoProcedencia.invalid",
                    "Tipo de procedencia invalido.");
            return;
        }

        ProcedenciaEntradaRules.TipoCampo tipoCampoProcedencia = ProcedenciaEntradaRules.resolve(tipoProcedenciaOpt.get());
        ProcedenciaEntradaRules.clearIrrelevantFields(form, tipoCampoProcedencia);

        if (tipoCampoProcedencia == ProcedenciaEntradaRules.TipoCampo.BAIRRO
                && form.getProcedenciaBairroId() == null) {
            bindingResult.rejectValue(fieldPrefix + "procedenciaBairroId",
                    "entrada.procedenciaBairro.required",
                    "Bairro e obrigatorio.");
            return;
        }
        if (tipoCampoProcedencia == ProcedenciaEntradaRules.TipoCampo.MUNICIPIO
                && form.getProcedenciaMunicipioId() == null) {
            bindingResult.rejectValue(fieldPrefix + "procedenciaMunicipioId",
                    "entrada.procedenciaMunicipio.required",
                    "Municipio e obrigatoria.");
            return;
        }
        if (tipoCampoProcedencia == ProcedenciaEntradaRules.TipoCampo.OUTROS
                && (form.getProcedenciaObservacao() == null || form.getProcedenciaObservacao().isBlank())) {
            bindingResult.rejectValue(fieldPrefix + "procedenciaObservacao",
                    "entrada.procedenciaObservacao.required",
                    "Descricao da procedencia e obrigatoria.");
            return;
        }
        if (tipoCampoProcedencia == ProcedenciaEntradaRules.TipoCampo.CATALOGO
                && form.getProcedenciaId() == null) {
            bindingResult.rejectValue(fieldPrefix + "procedenciaId",
                    "entrada.procedencia.required",
                    "Procedencia e obrigatoria.");
        }
    }

    private Long resolveAreaExecucaoRecepcaoAtual(Long unidadeId, List<Area> areasExecucaoRecepcao) {
        Long areaAtual = unidadeContext.getAreaExecucaoRecepcaoAtual().orElse(null);
        if (areaAtual != null && isAreaEntradaValida(areaAtual, areasExecucaoRecepcao)) {
            return areaAtual;
        }
        if (areasExecucaoRecepcao.size() == 1) {
            Long unico = areasExecucaoRecepcao.get(0).getId();
            unidadeContext.setAreaExecucaoRecepcaoAtual(unico);
            return unico;
        }
        if (areaAtual != null) {
            unidadeContext.clearAreaExecucaoRecepcaoAtual();
        }
        return null;
    }

    private void applyAreaExecucaoRecepcaoDefault(EntradaForm form,
                                                  Long areaExecucaoRecepcaoAtualId,
                                                  List<Area> areasExecucaoRecepcao) {
        if (form == null) {
            return;
        }

        if (form.getAreaExecucaoId() == null || !isAreaEntradaValida(form.getAreaExecucaoId(), areasExecucaoRecepcao)) {
            if (areaExecucaoRecepcaoAtualId != null) {
                form.setAreaExecucaoId(areaExecucaoRecepcaoAtualId);
            } else if (areasExecucaoRecepcao.size() == 1) {
                form.setAreaExecucaoId(areasExecucaoRecepcao.get(0).getId());
            }
        }

        if (form.getAreaPortaEntradaId() == null || !isAreaEntradaValida(form.getAreaPortaEntradaId(), areasExecucaoRecepcao)) {
            if (form.getAreaExecucaoId() != null && isAreaEntradaValida(form.getAreaExecucaoId(), areasExecucaoRecepcao)) {
                form.setAreaPortaEntradaId(form.getAreaExecucaoId());
            } else if (areasExecucaoRecepcao.size() == 1) {
                form.setAreaPortaEntradaId(areasExecucaoRecepcao.get(0).getId());
            }
        }
    }

    private void rememberAreaExecucaoRecepcao(Long areaId) {
        if (areaId != null) {
            unidadeContext.setAreaExecucaoRecepcaoAtual(areaId);
        }
    }

    private boolean isAreaEntradaValida(Long areaId, List<Area> areasExecucaoRecepcao) {
        if (areaId == null) {
            return false;
        }
        return areasExecucaoRecepcao.stream().anyMatch(area -> area.getId().equals(areaId));
    }
}
