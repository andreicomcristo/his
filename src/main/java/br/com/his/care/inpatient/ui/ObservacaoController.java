package br.com.his.care.inpatient.ui;

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
import br.com.his.care.inpatient.dto.ObservacaoForm;
import br.com.his.care.inpatient.model.Observacao;
import br.com.his.care.attendance.model.TipoAtendimento;
import br.com.his.care.attendance.repository.AtendimentoRepository;
import br.com.his.care.attendance.repository.StatusAtendimentoRepository;
import br.com.his.care.inpatient.service.ObservacaoService;
import br.com.his.patient.validation.CpfUtils;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/ui/observacoes")
public class ObservacaoController {

    private final ObservacaoService observacaoService;
    private final AtendimentoRepository atendimentoRepository;
    private final StatusAtendimentoRepository statusAtendimentoRepository;
    private final UnidadeContext unidadeContext;
    private final OperationalPermissionService operationalPermissionService;

    public ObservacaoController(ObservacaoService observacaoService,
                                AtendimentoRepository atendimentoRepository,
                                StatusAtendimentoRepository statusAtendimentoRepository,
                                UnidadeContext unidadeContext,
                                OperationalPermissionService operationalPermissionService) {
        this.observacaoService = observacaoService;
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
        List<Observacao> items = observacaoService.listar().stream()
                .filter(item -> Objects.equals(item.getAtendimento().getUnidade().getId(), unidadeId))
                .filter(item -> matchesNome(item, nome))
                .filter(item -> matchesCpf(item, cpf))
                .filter(item -> tipoAtendimento == null || item.getAtendimento().getTipoAtendimento() == tipoAtendimento)
                .filter(item -> statusId == null || (item.getAtendimento().getStatus() != null
                        && statusId.equals(item.getAtendimento().getStatus().getId())))
                .filter(item -> matchesPeriodo(item, dataInicio, dataFim))
                .toList();
        model.addAttribute("items", items);
        model.addAttribute("leitosMap", observacaoService.mapaLeitoAtual(items));
        model.addAttribute("nome", nome);
        model.addAttribute("cpf", cpf);
        model.addAttribute("tipoAtendimentoSelecionado", tipoAtendimento);
        model.addAttribute("statusSelecionadoId", statusId);
        model.addAttribute("dataInicio", dataInicio);
        model.addAttribute("dataFim", dataFim);
        model.addAttribute("tiposAtendimento", TipoAtendimento.values());
        model.addAttribute("statusAtendimentoOptions", statusAtendimentoRepository.findAllByOrderByDescricaoAsc());
        return "pages/care/inpatient/observacoes/list";
    }

    @GetMapping("/novo")
    public String novo(@RequestParam(required = false) Long atendimentoId,
                       @RequestParam(required = false) Long leitoId,
                       Model model) {
        requirePermission();
        if (!model.containsAttribute("form")) {
            ObservacaoForm form = new ObservacaoForm();
            form.setAtendimentoId(atendimentoId);
            form.setLeitoId(leitoId);
            model.addAttribute("form", form);
        }
        model.addAttribute("modoEdicao", false);
        model.addAttribute("atendimentoTravado", atendimentoId != null);
        populateReferences(model, (ObservacaoForm) model.getAttribute("form"));
        return "pages/care/inpatient/observacoes/form";
    }

    @PostMapping
    public String criar(@Valid @ModelAttribute("form") ObservacaoForm form,
                        BindingResult bindingResult,
                        @RequestParam(defaultValue = "false") boolean atendimentoTravado,
                        Model model,
                        RedirectAttributes redirectAttributes) {
        requirePermission();
        if (bindingResult.hasErrors()) {
            model.addAttribute("modoEdicao", false);
            model.addAttribute("atendimentoTravado", atendimentoTravado);
            populateReferences(model, form);
            return "pages/care/inpatient/observacoes/form";
        }
        try {
            observacaoService.criar(form);
            redirectAttributes.addFlashAttribute("successMessage", "Observacao cadastrada com sucesso");
            return "redirect:/ui/observacoes";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("modoEdicao", false);
            model.addAttribute("atendimentoTravado", atendimentoTravado);
            model.addAttribute("errorMessage", ex.getMessage());
            populateReferences(model, form);
            return "pages/care/inpatient/observacoes/form";
        }
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        requirePermission();
        model.addAttribute("form", observacaoService.toForm(observacaoService.buscar(id)));
        model.addAttribute("modoEdicao", true);
        model.addAttribute("itemId", id);
        model.addAttribute("atendimentoTravado", false);
        populateReferences(model, (ObservacaoForm) model.getAttribute("form"));
        return "pages/care/inpatient/observacoes/form";
    }

    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id,
                            @Valid @ModelAttribute("form") ObservacaoForm form,
                            BindingResult bindingResult,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        requirePermission();
        if (bindingResult.hasErrors()) {
            model.addAttribute("modoEdicao", true);
            model.addAttribute("itemId", id);
            model.addAttribute("atendimentoTravado", false);
            populateReferences(model, form);
            return "pages/care/inpatient/observacoes/form";
        }
        try {
            observacaoService.atualizar(id, form);
            redirectAttributes.addFlashAttribute("successMessage", "Observacao atualizada com sucesso");
            return "redirect:/ui/observacoes";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("modoEdicao", true);
            model.addAttribute("itemId", id);
            model.addAttribute("atendimentoTravado", false);
            model.addAttribute("errorMessage", ex.getMessage());
            populateReferences(model, form);
            return "pages/care/inpatient/observacoes/form";
        }
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        requirePermission();
        observacaoService.excluir(id);
        redirectAttributes.addFlashAttribute("successMessage", "Observacao excluida com sucesso");
        return "redirect:/ui/observacoes";
    }

    @PostMapping("/{id}/cancelar")
    public String cancelar(@PathVariable Long id,
                           @RequestParam String motivoCancelamento,
                           RedirectAttributes redirectAttributes) {
        requirePermission();
        try {
            observacaoService.cancelar(id, motivoCancelamento);
            redirectAttributes.addFlashAttribute("successMessage", "Observacao cancelada com sucesso");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/ui/observacoes";
    }

    private void populateReferences(Model model, ObservacaoForm form) {
        Long unidadeId = unidadeAtual();
        Long atendimentoId = form.getAtendimentoId();
        model.addAttribute("atendimentos", observacaoService.listarAtendimentosElegiveis(unidadeId, atendimentoId));
        if (atendimentoId != null) {
            atendimentoRepository.findById(atendimentoId).ifPresent(a -> model.addAttribute("atendimentoSelecionado", a));
        }
        model.addAttribute("leitosDisponiveis", observacaoService.listarLeitosDisponiveis(unidadeId, form.getLeitoId()));
    }

    private void requirePermission() {
        if (!operationalPermissionService.canGerirPermanencia(
                SecurityContextHolder.getContext().getAuthentication())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Sem permissao para observacao");
        }
    }

    private Long unidadeAtual() {
        return unidadeContext.getUnidadeAtual()
                .orElseThrow(() -> new IllegalArgumentException("Unidade atual nao definida"));
    }

    private boolean matchesNome(Observacao item, String nome) {
        String filtro = normalizeFiltro(nome);
        if (filtro == null) {
            return true;
        }
        String nomePaciente = item.getAtendimento().getPaciente().getNomeExibicao();
        return nomePaciente != null && nomePaciente.toUpperCase().contains(filtro.toUpperCase());
    }

    private boolean matchesCpf(Observacao item, String cpf) {
        String filtro = normalizeFiltro(cpf);
        if (filtro == null) {
            return true;
        }
        String filtroDigits = CpfUtils.digitsOnly(filtro);
        String cpfPaciente = item.getAtendimento().getPaciente().getCpf();
        String cpfDigits = CpfUtils.digitsOnly(cpfPaciente);
        return filtroDigits != null && cpfDigits != null && cpfDigits.contains(filtroDigits);
    }

    private boolean matchesPeriodo(Observacao item, LocalDate dataInicio, LocalDate dataFim) {
        if (dataInicio == null && dataFim == null) {
            return true;
        }
        LocalDate dataReferencia = item.getDataHoraInicio() != null
                ? item.getDataHoraInicio().toLocalDate()
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
