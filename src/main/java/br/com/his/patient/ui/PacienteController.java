package br.com.his.patient.ui;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.his.care.attendance.service.AssistencialFlowService;
import br.com.his.reference.location.model.Municipio;
import br.com.his.reference.location.repository.MunicipioRepository;
import br.com.his.reference.location.repository.UnidadeFederativaRepository;
import br.com.his.patient.dto.PacienteForm;
import br.com.his.patient.dto.PacienteCepResponse;
import br.com.his.patient.dto.PacienteCpfSUSResponse;
import br.com.his.patient.dto.PacienteIdentificarCpfForm;
import br.com.his.patient.dto.PacienteLookupOption;
import br.com.his.patient.model.Paciente;
import br.com.his.patient.service.CadsusLookupService;
import br.com.his.patient.service.CepLookupService;
import br.com.his.patient.service.PacienteLookupService;
import br.com.his.patient.service.PacienteService;
import br.com.his.patient.validation.CpfUtils;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/ui/pacientes")
public class PacienteController {

    private final PacienteService pacienteService;
    private final AssistencialFlowService assistencialFlowService;
    private final PacienteLookupService pacienteLookupService;
    private final CepLookupService cepLookupService;
    private final CadsusLookupService cadsusLookupService;
    private final UnidadeFederativaRepository unidadeFederativaRepository;
    private final MunicipioRepository MunicipioRepository;

    public PacienteController(PacienteService pacienteService,
                              AssistencialFlowService assistencialFlowService,
                              PacienteLookupService pacienteLookupService,
                              CepLookupService cepLookupService,
                              CadsusLookupService cadsusLookupService,
                              UnidadeFederativaRepository unidadeFederativaRepository,
                              MunicipioRepository MunicipioRepository) {
        this.pacienteService = pacienteService;
        this.assistencialFlowService = assistencialFlowService;
        this.pacienteLookupService = pacienteLookupService;
        this.cepLookupService = cepLookupService;
        this.cadsusLookupService = cadsusLookupService;
        this.unidadeFederativaRepository = unidadeFederativaRepository;
        this.MunicipioRepository = MunicipioRepository;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) String nome,
                         @RequestParam(required = false) String cpf,
                         @RequestParam(required = false) String cns,
                         Model model) {
        List<Paciente> pacientes = pacienteService.buscarPorCpfCnsNome(nome, cpf, cns);
        model.addAttribute("pacientes", pacientes);
        model.addAttribute("nome", nome);
        model.addAttribute("cpf", cpf);
        model.addAttribute("cns", cns);
        return "pages/patient/list";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        if (!model.containsAttribute("pacienteForm")) {
            PacienteForm form = new PacienteForm();
            form.setTemporario(false);
            form.setSexo("NI");
            model.addAttribute("pacienteForm", form);
        }
        populateLookups(model);
        model.addAttribute("modoEdicao", false);
        return "pages/patient/form";
    }

    @GetMapping("/municipios-por-uf/{unidadeFederativaId}")
    @ResponseBody
    public List<PacienteLookupOption> MunicipiosPorUf(@PathVariable Long unidadeFederativaId) {
        return MunicipioRepository.findByUnidadeFederativaIdOrderByDescricao(unidadeFederativaId)
                .stream()
                .map(this::toMunicipioOption)
                .toList();
    }

    @GetMapping("/cep/{cep}")
    @ResponseBody
    public ResponseEntity<?> buscarCep(@PathVariable String cep) {
        try {
            PacienteCepResponse response = cepLookupService.buscarPorCep(cep);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(java.util.Map.of("message", ex.getMessage()));
        }
    }

    @GetMapping("/cpf/{cpf}/cadsus")
    @ResponseBody
    public ResponseEntity<?> buscarPorCpfNoCadsus(@PathVariable String cpf) {
        try {
            PacienteCpfSUSResponse response = cadsusLookupService.buscarPorCpf(cpf);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(java.util.Map.of("message", ex.getMessage()));
        }
    }

    @PostMapping
    public String criar(@Valid @ModelAttribute("pacienteForm") PacienteForm form,
                        BindingResult bindingResult,
                        Model model,
                        RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            populateLookups(model);
            model.addAttribute("modoEdicao", false);
            return "pages/patient/form";
        }

        try {
            Paciente saved = form.isTemporario()
                    ? pacienteService.criarPacienteTemporario(form)
                    : pacienteService.criarPacienteDefinitivo(form);
            redirectAttributes.addFlashAttribute("successMessage", "Paciente criado com sucesso: ID " + saved.getId());
            return "redirect:/ui/pacientes";
        } catch (IllegalArgumentException ex) {
            bindingResult.reject("paciente", ex.getMessage());
            populateLookups(model);
            model.addAttribute("modoEdicao", false);
            return "pages/patient/form";
        }
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id,
                         @RequestParam(required = false) String cpfSugerido,
                         Model model) {
        Paciente paciente = pacienteService.buscarPorId(id);
        PacienteForm form = toForm(paciente);
        if (cpfSugerido != null && !cpfSugerido.isBlank()) {
            form.setCpf(CpfUtils.digitsOnly(cpfSugerido));
            form.setTemporario(false);
        }
        populateLookups(model);
        model.addAttribute("pacienteForm", form);
        model.addAttribute("pacienteId", paciente.getId());
        model.addAttribute("modoEdicao", true);
        return "pages/patient/form";
    }

    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id,
                            @Valid @ModelAttribute("pacienteForm") PacienteForm form,
                            BindingResult bindingResult,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            populateLookups(model);
            model.addAttribute("pacienteId", id);
            model.addAttribute("modoEdicao", true);
            return "pages/patient/form";
        }

        try {
            Paciente updated = pacienteService.atualizarPaciente(id, form);
            if (!updated.getId().equals(id)) {
                redirectAttributes.addFlashAttribute("successMessage",
                        "Paciente temporario identificado e mergeado no paciente definitivo ID " + updated.getId());
            } else {
                redirectAttributes.addFlashAttribute("successMessage", "Paciente atualizado com sucesso");
            }
            return "redirect:/ui/pacientes";
        } catch (IllegalArgumentException ex) {
            bindingResult.reject("paciente", ex.getMessage());
            populateLookups(model);
            model.addAttribute("pacienteId", id);
            model.addAttribute("modoEdicao", true);
            return "pages/patient/form";
        }
    }

    @PostMapping("/{id}/cancelar")
    public String cancelar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            pacienteService.cancelarPaciente(id);
            redirectAttributes.addFlashAttribute("successMessage", "Paciente cancelado com sucesso");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/ui/pacientes";
    }

    @PostMapping("/{id}/restaurar")
    public String restaurar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            pacienteService.restaurarPaciente(id);
            redirectAttributes.addFlashAttribute("successMessage", "Paciente restaurado com sucesso");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/ui/pacientes";
    }

    @GetMapping("/temporario")
    public String criarTemporario(@RequestParam(required = false) String sexo,
                                  @RequestParam(required = false) Integer idadeAparente,
                                  RedirectAttributes redirectAttributes) {
        try {
            Paciente paciente = pacienteService.criarPacienteTemporario(sexo, idadeAparente);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Paciente temporario criado com ID " + paciente.getId());
            return "redirect:/ui/pacientes/" + paciente.getId() + "/editar";
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/ui/pacientes";
        }
    }

    @GetMapping("/{id}/identificar")
    public String identificarPorCpfTela(@PathVariable Long id,
                                        @RequestParam(required = false) Long atendimentoId,
                                        @RequestParam(required = false) String retorno,
                                        Model model) {
        Paciente paciente = pacienteService.buscarPorId(id);
        if (!paciente.isTemporario() || !paciente.isAtivo() || paciente.getMergedInto() != null) {
            model.addAttribute("errorMessage", "Apenas paciente temporario ativo pode ser identificado por CPF.");
            return "redirect:/ui/pacientes";
        }

        if (!model.containsAttribute("identificarForm")) {
            PacienteIdentificarCpfForm form = new PacienteIdentificarCpfForm();
            form.setCpf(paciente.getCpf());
            model.addAttribute("identificarForm", form);
        }
        model.addAttribute("pacienteTemporario", paciente);
        model.addAttribute("atendimentoId", atendimentoId);
        model.addAttribute("retorno", resolveRetorno(retorno));
        if (atendimentoId != null) {
            model.addAttribute("atendimento", assistencialFlowService.buscarAtendimento(atendimentoId));
        }
        return "pages/patient/identificar-cpf";
    }

    @PostMapping("/{id}/identificar")
    public String identificarPorCpf(@PathVariable Long id,
                                    @RequestParam(required = false) Long atendimentoId,
                                    @RequestParam(required = false) String retorno,
                                    @Valid @ModelAttribute("identificarForm") PacienteIdentificarCpfForm form,
                                    BindingResult bindingResult,
                                    Model model,
                                    RedirectAttributes redirectAttributes) {
        Paciente pacienteTemporario = pacienteService.buscarPorId(id);
        String redirectRetorno = resolveRetorno(retorno);
        if (!pacienteTemporario.isTemporario() || !pacienteTemporario.isAtivo() || pacienteTemporario.getMergedInto() != null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Apenas paciente temporario ativo pode ser identificado por CPF.");
            return "redirect:" + redirectRetorno;
        }

        String cpf = CpfUtils.digitsOnly(form.getCpf());
        if (!CpfUtils.isValid(cpf)) {
            bindingResult.rejectValue("cpf", "cpf.invalido", "CPF invalido");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("pacienteTemporario", pacienteTemporario);
            model.addAttribute("atendimentoId", atendimentoId);
            model.addAttribute("retorno", redirectRetorno);
            if (atendimentoId != null) {
                model.addAttribute("atendimento", assistencialFlowService.buscarAtendimento(atendimentoId));
            }
            return "pages/patient/identificar-cpf";
        }

        var candidato = pacienteService.buscarDefinitivoAtivoPorCpf(cpf, pacienteTemporario.getId());
        if (candidato.isPresent()) {
            model.addAttribute("pacienteTemporario", pacienteTemporario);
            model.addAttribute("pacienteDefinitivo", candidato.get());
            model.addAttribute("cpfInformado", cpf);
            model.addAttribute("atendimentoId", atendimentoId);
            model.addAttribute("retorno", redirectRetorno);
            if (atendimentoId != null) {
                model.addAttribute("atendimento", assistencialFlowService.buscarAtendimento(atendimentoId));
            }
            return "pages/patient/identificar-cpf";
        }

        redirectAttributes.addFlashAttribute("successMessage",
                "CPF nao encontrado. Complete o cadastro para identificar o paciente temporario.");
        return "redirect:/ui/pacientes/" + id + "/editar?cpfSugerido=" + cpf;
    }

    @PostMapping("/{id}/identificar/merge")
    public String mergeIdentificacao(@PathVariable Long id,
                                     @RequestParam(required = false) String retorno,
                                     @RequestParam Long pacienteDefinitivoId,
                                     @RequestParam(required = false) String motivo,
                                     RedirectAttributes redirectAttributes) {
        String redirectRetorno = resolveRetorno(retorno);
        try {
            String motivoMerge = (motivo == null || motivo.isBlank())
                    ? "Identificacao por CPF na triagem"
                    : motivo;
            pacienteService.mergePaciente(id, pacienteDefinitivoId, motivoMerge);
            redirectAttributes.addFlashAttribute("successMessage", "Merge executado com sucesso");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:" + redirectRetorno;
    }

    private String resolveRetorno(String retorno) {
        if (retorno != null && retorno.startsWith("/ui/")) {
            return retorno;
        }
        return "/ui/pacientes";
    }

    private static PacienteForm toForm(Paciente paciente) {
        PacienteForm form = new PacienteForm();
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
        return form;
    }

    private void populateLookups(Model model) {
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
        Long unidadeFederativaId = null;
        Object formObject = model.getAttribute("pacienteForm");
        if (formObject instanceof PacienteForm form) {
            unidadeFederativaId = form.getUnidadeFederativaId();
        }
        model.addAttribute("municipios", unidadeFederativaId == null
                ? List.of()
                : MunicipioRepository.findByUnidadeFederativaIdOrderByDescricao(unidadeFederativaId));
    }

    private PacienteLookupOption toMunicipioOption(Municipio Municipio) {
        return new PacienteLookupOption(Municipio.getId(), Municipio.getDescricao());
    }
}


