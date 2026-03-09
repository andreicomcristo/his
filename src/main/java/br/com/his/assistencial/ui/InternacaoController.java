package br.com.his.assistencial.ui;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.format.annotation.DateTimeFormat;
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
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.his.access.context.UnidadeContext;
import br.com.his.access.service.OperationalPermissionService;
import br.com.his.assistencial.dto.InternacaoForm;
import br.com.his.assistencial.model.Internacao;
import br.com.his.assistencial.model.Observacao;
import br.com.his.assistencial.model.TipoAtendimento;
import br.com.his.assistencial.repository.AtendimentoRepository;
import br.com.his.assistencial.repository.StatusAtendimentoRepository;
import br.com.his.assistencial.service.InternacaoService;
import br.com.his.paciente.validation.CpfUtils;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/ui/internacoes")
public class InternacaoController {

    private final InternacaoService internacaoService;
    private final AtendimentoRepository atendimentoRepository;
    private final StatusAtendimentoRepository statusAtendimentoRepository;
    private final UnidadeContext unidadeContext;
    private final OperationalPermissionService operationalPermissionService;

    public InternacaoController(InternacaoService internacaoService,
                                AtendimentoRepository atendimentoRepository,
                                StatusAtendimentoRepository statusAtendimentoRepository,
                                UnidadeContext unidadeContext,
                                OperationalPermissionService operationalPermissionService) {
        this.internacaoService = internacaoService;
        this.atendimentoRepository = atendimentoRepository;
        this.statusAtendimentoRepository = statusAtendimentoRepository;
        this.unidadeContext = unidadeContext;
        this.operationalPermissionService = operationalPermissionService;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) String nome,
                         @RequestParam(required = false) String cpf,
                         @RequestParam(required = false) TipoAtendimento tipoAtendimento,
                         @RequestParam(required = false) Long statusId,
                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
                         Model model) {
        requirePermission();
        Long unidadeId = unidadeAtual();
        List<Internacao> items = internacaoService.listar().stream()
                .filter(item -> Objects.equals(item.getAtendimento().getUnidade().getId(), unidadeId))
                .filter(item -> matchesNome(item, nome))
                .filter(item -> matchesCpf(item, cpf))
                .filter(item -> tipoAtendimento == null || item.getAtendimento().getTipoAtendimento() == tipoAtendimento)
                .filter(item -> statusId == null || (item.getAtendimento().getStatus() != null
                        && statusId.equals(item.getAtendimento().getStatus().getId())))
                .filter(item -> matchesPeriodo(item, dataInicio, dataFim))
                .toList();
        model.addAttribute("items", items);
        model.addAttribute("leitosMap", internacaoService.mapaLeitoAtual(items));
        model.addAttribute("nome", nome);
        model.addAttribute("cpf", cpf);
        model.addAttribute("tipoAtendimentoSelecionado", tipoAtendimento);
        model.addAttribute("statusSelecionadoId", statusId);
        model.addAttribute("dataInicio", dataInicio);
        model.addAttribute("dataFim", dataFim);
        model.addAttribute("tiposAtendimento", TipoAtendimento.values());
        model.addAttribute("statusAtendimentoOptions", statusAtendimentoRepository.findAllByOrderByDescricaoAsc());
        return "pages/internacoes/list";
    }

    @GetMapping("/novo")
    public String novo(@RequestParam(required = false) Long atendimentoId,
                       @RequestParam(required = false) Long observacaoId,
                       @RequestParam(required = false) Long leitoId,
                       Model model) {
        requirePermission();
        if (!model.containsAttribute("form")) {
            InternacaoForm form = new InternacaoForm();
            form.setAtendimentoId(atendimentoId);
            form.setObservacaoOrigemId(observacaoId);
            form.setLeitoId(leitoId);
            if (observacaoId != null) {
                Observacao observacaoOrigem = internacaoService.buscarObservacao(observacaoId);
                form.setAtendimentoId(observacaoOrigem.getAtendimento().getId());
                form.setDataHoraDecisaoInternacao(LocalDateTime.now());
                form.setDataHoraInicioInternacao(LocalDateTime.now());
                form.setLeitoId(internacaoService.leitoAtualObservacao(observacaoId));
                if (form.getLeitoId() == null) {
                    throw new IllegalArgumentException("Observacao selecionada nao possui leito ativo");
                }
                form.setTipoOcupacaoId(internacaoService
                        .resolverTipoOcupacaoPadraoPorLeito(unidadeAtual(), form.getLeitoId()));
                model.addAttribute("observacaoOrigem", observacaoOrigem);
            }
            if (leitoId != null) {
                form.setTipoOcupacaoId(internacaoService.resolverTipoOcupacaoPadraoPorLeito(unidadeAtual(), leitoId));
            }
            model.addAttribute("form", form);
        } else if (((InternacaoForm) model.getAttribute("form")).getObservacaoOrigemId() != null) {
            Long obsId = ((InternacaoForm) model.getAttribute("form")).getObservacaoOrigemId();
            model.addAttribute("observacaoOrigem", internacaoService.buscarObservacao(obsId));
        }
        InternacaoForm form = (InternacaoForm) model.getAttribute("form");
        model.addAttribute("modoEdicao", false);
        model.addAttribute("atendimentoTravado", atendimentoId != null || form.getObservacaoOrigemId() != null);
        model.addAttribute("leitoTravado", leitoId != null || form.getObservacaoOrigemId() != null);
        populateReferences(model, form);
        return "pages/internacoes/form";
    }

    @PostMapping
    public String criar(@Valid @ModelAttribute("form") InternacaoForm form,
                        BindingResult bindingResult,
                        @RequestParam(defaultValue = "false") boolean atendimentoTravado,
                        @RequestParam(defaultValue = "false") boolean leitoTravado,
                        Model model,
                        RedirectAttributes redirectAttributes) {
        requirePermission();
        aplicarTipoOcupacaoPadraoSeLeitoTravado(form, leitoTravado);
        if (bindingResult.hasErrors()) {
            model.addAttribute("modoEdicao", false);
            model.addAttribute("atendimentoTravado", atendimentoTravado);
            model.addAttribute("leitoTravado", leitoTravado);
            if (form.getObservacaoOrigemId() != null) {
                model.addAttribute("observacaoOrigem", internacaoService.buscarObservacao(form.getObservacaoOrigemId()));
            }
            populateReferences(model, form);
            return "pages/internacoes/form";
        }
        try {
            internacaoService.criar(form);
            redirectAttributes.addFlashAttribute("successMessage", "Internacao cadastrada com sucesso");
            return "redirect:/ui/internacoes";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("modoEdicao", false);
            model.addAttribute("atendimentoTravado", atendimentoTravado);
            model.addAttribute("leitoTravado", leitoTravado);
            model.addAttribute("errorMessage", ex.getMessage());
            if (form.getObservacaoOrigemId() != null) {
                model.addAttribute("observacaoOrigem", internacaoService.buscarObservacao(form.getObservacaoOrigemId()));
            }
            populateReferences(model, form);
            return "pages/internacoes/form";
        }
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        requirePermission();
        model.addAttribute("form", internacaoService.toForm(internacaoService.buscar(id)));
        model.addAttribute("modoEdicao", true);
        model.addAttribute("itemId", id);
        model.addAttribute("atendimentoTravado", false);
        model.addAttribute("leitoTravado", false);
        populateReferences(model, (InternacaoForm) model.getAttribute("form"));
        return "pages/internacoes/form";
    }

    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id,
                            @Valid @ModelAttribute("form") InternacaoForm form,
                            BindingResult bindingResult,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        requirePermission();
        if (bindingResult.hasErrors()) {
            model.addAttribute("modoEdicao", true);
            model.addAttribute("itemId", id);
            model.addAttribute("atendimentoTravado", false);
            model.addAttribute("leitoTravado", false);
            populateReferences(model, form);
            return "pages/internacoes/form";
        }
        try {
            internacaoService.atualizar(id, form);
            redirectAttributes.addFlashAttribute("successMessage", "Internacao atualizada com sucesso");
            return "redirect:/ui/internacoes";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("modoEdicao", true);
            model.addAttribute("itemId", id);
            model.addAttribute("atendimentoTravado", false);
            model.addAttribute("leitoTravado", false);
            model.addAttribute("errorMessage", ex.getMessage());
            populateReferences(model, form);
            return "pages/internacoes/form";
        }
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        requirePermission();
        internacaoService.excluir(id);
        redirectAttributes.addFlashAttribute("successMessage", "Internacao excluida com sucesso");
        return "redirect:/ui/internacoes";
    }

    @PostMapping("/{id}/cancelar")
    public String cancelar(@PathVariable Long id,
                           @RequestParam String motivoCancelamento,
                           RedirectAttributes redirectAttributes) {
        requirePermission();
        try {
            internacaoService.cancelar(id, motivoCancelamento);
            redirectAttributes.addFlashAttribute("successMessage", "Internacao cancelada com sucesso");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/ui/internacoes";
    }

    private void populateReferences(Model model, InternacaoForm form) {
        Long unidadeId = unidadeAtual();
        Long atendimentoId = form.getAtendimentoId();
        model.addAttribute("atendimentos", internacaoService.listarAtendimentosElegiveis(unidadeId, atendimentoId));
        if (atendimentoId != null) {
            atendimentoRepository.findById(atendimentoId).ifPresent(a -> model.addAttribute("atendimentoSelecionado", a));
        }
        model.addAttribute("origensDemanda", internacaoService.listarOrigensAtivas());
        model.addAttribute("perfisInternacao", internacaoService.listarPerfisAtivos());
        model.addAttribute("tiposOcupacao", internacaoService.listarTiposOcupacaoAtivos());
        model.addAttribute("leitosDisponiveis", internacaoService.listarLeitosDisponiveis(unidadeId, form.getLeitoId()));
    }

    private void requirePermission() {
        if (!operationalPermissionService.canGerirPermanencia(
                SecurityContextHolder.getContext().getAuthentication())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Sem permissao para internacao");
        }
    }

    private Long unidadeAtual() {
        return unidadeContext.getUnidadeAtual()
                .orElseThrow(() -> new IllegalArgumentException("Unidade atual nao definida"));
    }

    private void aplicarTipoOcupacaoPadraoSeLeitoTravado(InternacaoForm form, boolean leitoTravado) {
        if (!leitoTravado || form.getLeitoId() == null) {
            return;
        }
        form.setTipoOcupacaoId(
                internacaoService.resolverTipoOcupacaoPadraoPorLeito(unidadeAtual(), form.getLeitoId()));
    }

    private boolean matchesNome(Internacao item, String nome) {
        String filtro = normalizeFiltro(nome);
        if (filtro == null) {
            return true;
        }
        String nomePaciente = item.getAtendimento().getPaciente().getNomeExibicao();
        return nomePaciente != null && nomePaciente.toUpperCase().contains(filtro.toUpperCase());
    }

    private boolean matchesCpf(Internacao item, String cpf) {
        String filtro = normalizeFiltro(cpf);
        if (filtro == null) {
            return true;
        }
        String filtroDigits = CpfUtils.digitsOnly(filtro);
        String cpfPaciente = item.getAtendimento().getPaciente().getCpf();
        String cpfDigits = CpfUtils.digitsOnly(cpfPaciente);
        return filtroDigits != null && cpfDigits != null && cpfDigits.contains(filtroDigits);
    }

    private boolean matchesPeriodo(Internacao item, LocalDate dataInicio, LocalDate dataFim) {
        if (dataInicio == null && dataFim == null) {
            return true;
        }
        LocalDate dataReferencia = item.getDataHoraDecisaoInternacao() != null
                ? item.getDataHoraDecisaoInternacao().toLocalDate()
                : (item.getDataHoraInicioInternacao() != null
                        ? item.getDataHoraInicioInternacao().toLocalDate()
                        : item.getAtendimento().getDataHoraChegada().toLocalDate());
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
