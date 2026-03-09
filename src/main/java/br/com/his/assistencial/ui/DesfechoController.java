package br.com.his.assistencial.ui;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.his.access.context.UnidadeContext;
import br.com.his.access.service.OperationalPermissionService;
import br.com.his.assistencial.dto.DesfechoForm;
import br.com.his.assistencial.model.Atendimento;
import br.com.his.assistencial.model.Desfecho;
import br.com.his.assistencial.model.TipoAtendimento;
import br.com.his.assistencial.repository.AtendimentoRepository;
import br.com.his.assistencial.repository.StatusAtendimentoRepository;
import br.com.his.assistencial.repository.TipoDesfechoRepository;
import br.com.his.assistencial.service.DesfechoService;
import br.com.his.admin.service.DestinoRedeAdminService;
import br.com.his.admin.service.MotivoDesfechoAdminService;
import br.com.his.admin.service.TipoDesfechoAdminService;
import br.com.his.paciente.validation.CpfUtils;
import jakarta.validation.Valid;

@Controller
@RequestMapping({"/ui/desfechos", "/ui/altas"})
public class DesfechoController {

    private final DesfechoService desfechoService;
    private final AtendimentoRepository atendimentoRepository;
    private final TipoDesfechoRepository tipoDesfechoRepository;
    private final StatusAtendimentoRepository statusAtendimentoRepository;
    private final UnidadeContext unidadeContext;
    private final OperationalPermissionService operationalPermissionService;
    private final TipoDesfechoAdminService tipoDesfechoAdminService;
    private final MotivoDesfechoAdminService motivoDesfechoAdminService;
    private final DestinoRedeAdminService destinoRedeAdminService;

    public DesfechoController(DesfechoService desfechoService,
                          AtendimentoRepository atendimentoRepository,
                          TipoDesfechoRepository tipoDesfechoRepository,
                          StatusAtendimentoRepository statusAtendimentoRepository,
                          UnidadeContext unidadeContext,
                          OperationalPermissionService operationalPermissionService,
                          TipoDesfechoAdminService tipoDesfechoAdminService,
                          MotivoDesfechoAdminService motivoDesfechoAdminService,
                          DestinoRedeAdminService destinoRedeAdminService) {
        this.desfechoService = desfechoService;
        this.atendimentoRepository = atendimentoRepository;
        this.tipoDesfechoRepository = tipoDesfechoRepository;
        this.statusAtendimentoRepository = statusAtendimentoRepository;
        this.unidadeContext = unidadeContext;
        this.operationalPermissionService = operationalPermissionService;
        this.tipoDesfechoAdminService = tipoDesfechoAdminService;
        this.motivoDesfechoAdminService = motivoDesfechoAdminService;
        this.destinoRedeAdminService = destinoRedeAdminService;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) String nome,
                         @RequestParam(required = false) String cpf,
                         @RequestParam(required = false) TipoAtendimento tipoAtendimento,
                         @RequestParam(required = false) Long statusId,
                         @RequestParam(required = false) Long tipoDesfechoId,
                         @RequestParam(required = false) Long motivoDesfechoId,
                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
                         Model model) {
        requirePermission();
        Long unidadeId = unidadeContext.getUnidadeAtual().orElse(null);
        List<Desfecho> items = desfechoService.listar().stream()
                .filter(item -> unidadeId == null || Objects.equals(item.getAtendimento().getUnidade().getId(), unidadeId))
                .filter(item -> matchesNome(item, nome))
                .filter(item -> matchesCpf(item, cpf))
                .filter(item -> tipoAtendimento == null || item.getAtendimento().getTipoAtendimento() == tipoAtendimento)
                .filter(item -> statusId == null || (item.getAtendimento().getStatus() != null
                        && statusId.equals(item.getAtendimento().getStatus().getId())))
                .filter(item -> tipoDesfechoId == null || (item.getTipoDesfecho() != null
                        && tipoDesfechoId.equals(item.getTipoDesfecho().getId())))
                .filter(item -> motivoDesfechoId == null || (item.getMotivoDesfecho() != null
                        && motivoDesfechoId.equals(item.getMotivoDesfecho().getId())))
                .filter(item -> matchesPeriodo(item, dataInicio, dataFim))
                .toList();

        model.addAttribute("items", items);
        model.addAttribute("nome", nome);
        model.addAttribute("cpf", cpf);
        model.addAttribute("tipoAtendimentoSelecionado", tipoAtendimento);
        model.addAttribute("statusSelecionadoId", statusId);
        model.addAttribute("tipoDesfechoSelecionadoId", tipoDesfechoId);
        model.addAttribute("motivoDesfechoSelecionadoId", motivoDesfechoId);
        model.addAttribute("dataInicio", dataInicio);
        model.addAttribute("dataFim", dataFim);
        model.addAttribute("tiposAtendimento", TipoAtendimento.values());
        model.addAttribute("statusAtendimentoOptions", statusAtendimentoRepository.findAllByOrderByDescricaoAsc());
        model.addAttribute("tiposDesfechoOptions", tipoDesfechoAdminService.listarAtivos());
        model.addAttribute("motivosDesfechoOptions", motivoDesfechoAdminService.listarAtivos());
        return "pages/desfechos/list";
    }

    @GetMapping("/novo")
    public String novo(@RequestParam(required = false) Long atendimentoId,
                       Model model,
                       RedirectAttributes redirectAttributes) {
        if (atendimentoId != null) {
            Atendimento atendimento = atendimentoRepository.findById(atendimentoId)
                    .orElseThrow(() -> new IllegalArgumentException("Atendimento nao encontrado"));
            if (desfechoService.pacienteNaoIdentificado(atendimento)) {
                redirectAttributes.addFlashAttribute(
                        "errorMessage",
                        "Paciente nao identificado. Identifique o paciente antes de registrar desfecho.");
                return "redirect:/ui/atendimentos";
            }
        }
        if (!model.containsAttribute("form")) {
            DesfechoForm form = new DesfechoForm();
            if (atendimentoId != null) {
                form.setAtendimentoId(atendimentoId);
                tipoDesfechoRepository.findAll().stream()
                        .filter(item -> "ATENDIMENTO".equalsIgnoreCase(item.getDescricao()))
                        .findFirst()
                        .ifPresent(item -> form.setTipoDesfechoId(item.getId()));
            }
            model.addAttribute("form", form);
        }
        model.addAttribute("modoEdicao", false);
        model.addAttribute("atendimentoTravado", atendimentoId != null);
        populateReferences(model, atendimentoId);
        return "pages/desfechos/form";
    }

    @PostMapping
    public String criar(@Valid @ModelAttribute("form") DesfechoForm form,
                        BindingResult bindingResult,
                        @RequestParam(defaultValue = "false") boolean atendimentoTravado,
                        Model model,
                        RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("modoEdicao", false);
            model.addAttribute("atendimentoTravado", atendimentoTravado);
            populateReferences(model, form.getAtendimentoId());
            return "pages/desfechos/form";
        }
        try {
            desfechoService.criar(form);
            redirectAttributes.addFlashAttribute("successMessage", "Desfecho cadastrado com sucesso");
            return "redirect:/ui/atendimentos";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("modoEdicao", false);
            model.addAttribute("atendimentoTravado", atendimentoTravado);
            model.addAttribute("errorMessage", ex.getMessage());
            populateReferences(model, form.getAtendimentoId());
            return "pages/desfechos/form";
        }
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("form", desfechoService.toForm(desfechoService.buscar(id)));
        model.addAttribute("modoEdicao", true);
        model.addAttribute("itemId", id);
        model.addAttribute("atendimentoTravado", false);
        populateReferences(model, null);
        return "pages/desfechos/form";
    }

    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id,
                            @Valid @ModelAttribute("form") DesfechoForm form,
                            BindingResult bindingResult,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("modoEdicao", true);
            model.addAttribute("itemId", id);
            model.addAttribute("atendimentoTravado", false);
            populateReferences(model, null);
            return "pages/desfechos/form";
        }
        try {
            desfechoService.atualizar(id, form);
            redirectAttributes.addFlashAttribute("successMessage", "Desfecho atualizado com sucesso");
            return "redirect:/ui/desfechos";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("modoEdicao", true);
            model.addAttribute("itemId", id);
            model.addAttribute("atendimentoTravado", false);
            model.addAttribute("errorMessage", ex.getMessage());
            populateReferences(model, null);
            return "pages/desfechos/form";
        }
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        desfechoService.excluir(id);
        redirectAttributes.addFlashAttribute("successMessage", "Desfecho excluido com sucesso");
        return "redirect:/ui/desfechos";
    }

    private void populateReferences(Model model, Long atendimentoSelecionadoId) {
        List<Atendimento> atendimentos = atendimentoRepository.findAll(Sort.by(Sort.Direction.DESC, "dataHoraChegada"));
        model.addAttribute("atendimentos", atendimentos);
        if (atendimentoSelecionadoId != null) {
            atendimentoRepository.findById(atendimentoSelecionadoId)
                    .ifPresent(atendimento -> model.addAttribute("atendimentoSelecionado", atendimento));
        }
        model.addAttribute("tiposDesfecho", tipoDesfechoAdminService.listarAtivos());
        model.addAttribute("motivosDesfecho", motivoDesfechoAdminService.listarAtivos());
        model.addAttribute("destinosRede", destinoRedeAdminService.listarAtivos());
    }

    private void requirePermission() {
        if (!operationalPermissionService.hasAny(
                SecurityContextHolder.getContext().getAuthentication(),
                java.util.Set.of(
                        OperationalPermissionService.PERM_RECEPCAO_EXECUTAR,
                        OperationalPermissionService.PERM_BUROCRATA_EXECUTAR))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Sem permissao para desfecho");
        }
    }

    private boolean matchesNome(Desfecho item, String nome) {
        String filtro = normalizeFiltro(nome);
        if (filtro == null) {
            return true;
        }
        String nomePaciente = item.getAtendimento().getPaciente().getNomeExibicao();
        return nomePaciente != null && nomePaciente.toUpperCase().contains(filtro.toUpperCase());
    }

    private boolean matchesCpf(Desfecho item, String cpf) {
        String filtro = normalizeFiltro(cpf);
        if (filtro == null) {
            return true;
        }
        String filtroDigits = CpfUtils.digitsOnly(filtro);
        String cpfPaciente = item.getAtendimento().getPaciente().getCpf();
        String cpfDigits = CpfUtils.digitsOnly(cpfPaciente);
        return filtroDigits != null && cpfDigits != null && cpfDigits.contains(filtroDigits);
    }

    private boolean matchesPeriodo(Desfecho item, LocalDate dataInicio, LocalDate dataFim) {
        if (dataInicio == null && dataFim == null) {
            return true;
        }
        LocalDate dataReferencia = item.getDataHora() != null
                ? item.getDataHora().toLocalDate()
                : item.getAtendimento().getDataHoraChegada().toLocalDate();
        if (dataInicio != null && dataReferencia.isBefore(dataInicio)) {
            return false;
        }
        if (dataFim != null && dataReferencia.isAfter(dataFim)) {
            return false;
        }
        return true;
    }

    private static String normalizeFiltro(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
