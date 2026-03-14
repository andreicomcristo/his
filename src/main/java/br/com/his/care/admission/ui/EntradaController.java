package br.com.his.care.admission.ui;

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

import br.com.his.access.service.OperationalPermissionService;
import br.com.his.care.admission.dto.EntradaPendenteForm;
import br.com.his.care.admission.dto.EntradaForm;
import br.com.his.care.attendance.model.Atendimento;
import br.com.his.care.attendance.service.AssistencialFlowService;
import br.com.his.reference.location.repository.MunicipioRepository;
import br.com.his.reference.location.repository.UnidadeFederativaRepository;
import br.com.his.patient.dto.PacienteForm;
import br.com.his.patient.model.Paciente;
import br.com.his.patient.service.PacienteLookupService;
import br.com.his.patient.service.PacienteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;

@Controller
@RequestMapping("/ui/entradas")
public class EntradaController {

    private final AssistencialFlowService assistencialFlowService;
    private final OperationalPermissionService operationalPermissionService;
    private final AreaRepository areaRepository;
    private final FormaChegadaRepository formaChegadaRepository;
    private final GrauParentescoRepository grauParentescoRepository;
    private final MotivoEntradaRepository motivoEntradaRepository;
    private final SituacaoOcupacionalRepository situacaoOcupacionalRepository;
    private final UnidadeFederativaRepository unidadeFederativaRepository;
    private final PacienteLookupService pacienteLookupService;
    private final PacienteService pacienteService;
    private final MunicipioRepository MunicipioRepository;
    private final SmartValidator validator;

    public EntradaController(AssistencialFlowService assistencialFlowService,
                             OperationalPermissionService operationalPermissionService,
                             AreaRepository areaRepository,
                             FormaChegadaRepository formaChegadaRepository,
                             GrauParentescoRepository grauParentescoRepository,
                             MotivoEntradaRepository motivoEntradaRepository,
                             SituacaoOcupacionalRepository situacaoOcupacionalRepository,
                             UnidadeFederativaRepository unidadeFederativaRepository,
                             PacienteLookupService pacienteLookupService,
                             PacienteService pacienteService,
                             MunicipioRepository MunicipioRepository,
                             SmartValidator validator) {
        this.assistencialFlowService = assistencialFlowService;
        this.operationalPermissionService = operationalPermissionService;
        this.areaRepository = areaRepository;
        this.formaChegadaRepository = formaChegadaRepository;
        this.grauParentescoRepository = grauParentescoRepository;
        this.motivoEntradaRepository = motivoEntradaRepository;
        this.situacaoOcupacionalRepository = situacaoOcupacionalRepository;
        this.unidadeFederativaRepository = unidadeFederativaRepository;
        this.pacienteLookupService = pacienteLookupService;
        this.pacienteService = pacienteService;
        this.MunicipioRepository = MunicipioRepository;
        this.validator = validator;
    }

    @GetMapping("/atendimento/{atendimentoId}")
    public String telaPorAtendimento(@PathVariable Long atendimentoId, Model model) {
        requirePermission();
        if (!model.containsAttribute("form")) {
            EntradaForm form = assistencialFlowService.buscarEntradaPorAtendimento(atendimentoId)
                    .map(entrada -> {
                        EntradaForm f = new EntradaForm();
                        f.setAreaId(entrada.getArea() == null ? null : entrada.getArea().getId());
                        f.setTipoProcedenciaId(entrada.getTipoProcedencia() == null
                                ? (entrada.getProcedencia() == null || entrada.getProcedencia().getTipoProcedencia() == null
                                        ? null : entrada.getProcedencia().getTipoProcedencia().getId())
                                : entrada.getTipoProcedencia().getId());
                        f.setProcedenciaId(entrada.getProcedencia() == null ? null : entrada.getProcedencia().getId());
                        f.setProcedenciaBairroId(entrada.getProcedenciaBairro() == null ? null : entrada.getProcedenciaBairro().getId());
                        f.setProcedenciaMunicipioUfId(entrada.getProcedenciaMunicipio() == null || entrada.getProcedenciaMunicipio().getUnidadeFederativa() == null
                                ? null : entrada.getProcedenciaMunicipio().getUnidadeFederativa().getId());
                        f.setProcedenciaMunicipioId(entrada.getProcedenciaMunicipio() == null ? null : entrada.getProcedenciaMunicipio().getId());
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
            var atendimento = assistencialFlowService.buscarAtendimento(atendimentoId);
            if (form.getProfissaoId() == null
                    && atendimento.getPaciente() != null
                    && atendimento.getPaciente().getProfissao() != null) {
                form.setProfissaoId(atendimento.getPaciente().getProfissao().getId());
            }
            model.addAttribute("form", form);
        }
        model.addAttribute("atendimentoId", atendimentoId);
        model.addAttribute("formAction", "/ui/entradas/atendimento/" + atendimentoId);
        var atendimento = assistencialFlowService.buscarAtendimento(atendimentoId);
        model.addAttribute("areasEntrada", areaRepository.findAreasAtivasRecebemEntradaByUnidadeId(atendimento.getUnidade().getId()));
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
        if (bindingResult.hasErrors()) {
            var atendimento = assistencialFlowService.buscarAtendimento(atendimentoId);
            model.addAttribute("atendimentoId", atendimentoId);
            model.addAttribute("formAction", "/ui/entradas/atendimento/" + atendimentoId);
            model.addAttribute("areasEntrada", areaRepository.findAreasAtivasRecebemEntradaByUnidadeId(atendimento.getUnidade().getId()));
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
        bindingResult.pushNestedPath("entradaForm");
        validator.validate(pendenteForm.getEntradaForm(), bindingResult);
        bindingResult.popNestedPath();
        if (bindingResult.hasErrors()) {
            populatePendenteModel(model, atendimento, pendenteForm, 2);
            return "pages/care/admission/pendente-wizard";
        }
        try {
            assistencialFlowService.registrarEntradaPorAtendimento(atendimentoId, pendenteForm.getEntradaForm());
            redirectAttributes.addFlashAttribute("successMessage", "Entrada registrada");
            return "redirect:/ui/atendimentos/pendentes-entrada";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            populatePendenteModel(model, atendimento, pendenteForm, 2);
            return "pages/care/admission/pendente-wizard";
        }
    }

    private void populatePendenteModel(Model model, Atendimento atendimento, EntradaPendenteForm pendenteForm, int currentStep) {
        model.addAttribute("atendimento", atendimento);
        model.addAttribute("classificacao", assistencialFlowService.mapaUltimaClassificacao(java.util.List.of(atendimento.getId())).get(atendimento.getId()));
        model.addAttribute("currentStep", currentStep);
        model.addAttribute("areasEntrada", areaRepository.findAreasAtivasRecebemEntradaByUnidadeId(atendimento.getUnidade().getId()));
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
                : MunicipioRepository.findByUnidadeFederativaIdOrderByNome(form.getUnidadeFederativaId()));
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
        form.setAreaId(entrada.getArea() == null ? null : entrada.getArea().getId());
        form.setTipoProcedenciaId(entrada.getTipoProcedencia() == null
                ? (entrada.getProcedencia() == null || entrada.getProcedencia().getTipoProcedencia() == null
                        ? null : entrada.getProcedencia().getTipoProcedencia().getId())
                : entrada.getTipoProcedencia().getId());
        form.setProcedenciaId(entrada.getProcedencia() == null ? null : entrada.getProcedencia().getId());
        form.setProcedenciaBairroId(entrada.getProcedenciaBairro() == null ? null : entrada.getProcedenciaBairro().getId());
        form.setProcedenciaMunicipioUfId(entrada.getProcedenciaMunicipio() == null || entrada.getProcedenciaMunicipio().getUnidadeFederativa() == null
                ? null : entrada.getProcedenciaMunicipio().getUnidadeFederativa().getId());
        form.setProcedenciaMunicipioId(entrada.getProcedenciaMunicipio() == null ? null : entrada.getProcedenciaMunicipio().getId());
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
}


