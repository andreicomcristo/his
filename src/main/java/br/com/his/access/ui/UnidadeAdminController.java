package br.com.his.access.ui;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.his.access.dto.UnidadeForm;
import br.com.his.access.repository.TipoUnidadeRepository;
import br.com.his.reference.location.service.UnidadeFederativaAdminService;
import br.com.his.access.service.UnidadeAdminService;
import br.com.his.reference.location.repository.CidadeRepository;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/ui/admin/unidades")
public class UnidadeAdminController {

    private final UnidadeAdminService unidadeAdminService;
    private final CidadeRepository cidadeRepository;
    private final TipoUnidadeRepository tipoUnidadeRepository;
    private final UnidadeFederativaAdminService unidadeFederativaAdminService;

    public UnidadeAdminController(UnidadeAdminService unidadeAdminService,
                                  CidadeRepository cidadeRepository,
                                  TipoUnidadeRepository tipoUnidadeRepository,
                                  UnidadeFederativaAdminService unidadeFederativaAdminService) {
        this.unidadeAdminService = unidadeAdminService;
        this.cidadeRepository = cidadeRepository;
        this.tipoUnidadeRepository = tipoUnidadeRepository;
        this.unidadeFederativaAdminService = unidadeFederativaAdminService;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) String q, Model model) {
        model.addAttribute("unidades", unidadeAdminService.listar(q));
        model.addAttribute("q", q);
        return "pages/access/admin/unidades/list";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new UnidadeForm());
        }
        populateModel(model, (UnidadeForm) model.getAttribute("form"));
        model.addAttribute("modoEdicao", false);
        return "pages/access/admin/unidades/form";
    }

    @PostMapping
    public String criar(@Valid @ModelAttribute("form") UnidadeForm form,
                        BindingResult bindingResult,
                        Model model,
        RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            populateModel(model, form);
            model.addAttribute("modoEdicao", false);
            return "pages/access/admin/unidades/form";
        }
        try {
            unidadeAdminService.criar(form);
            redirectAttributes.addFlashAttribute("successMessage", "Unidade criada com sucesso");
            return "redirect:/ui/admin/unidades";
        } catch (IllegalArgumentException ex) {
            bindingResult.reject("unidade", ex.getMessage());
            populateModel(model, form);
            model.addAttribute("modoEdicao", false);
            return "pages/access/admin/unidades/form";
        }
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        var unidade = unidadeAdminService.buscarPorId(id);
        UnidadeForm form = new UnidadeForm();
        form.setNome(unidade.getNome());
        form.setTipoUnidadeId(unidade.getTipoUnidade() == null ? null : unidade.getTipoUnidade().getId());
        form.setSigla(unidade.getSigla());
        form.setCnes(unidade.getCnes());
        form.setUnidadeFederativaId(unidade.getCidade() == null ? null : unidade.getCidade().getUnidadeFederativa().getId());
        form.setCidadeId(unidade.getCidade() == null ? null : unidade.getCidade().getId());
        model.addAttribute("form", form);
        populateModel(model, form);
        model.addAttribute("modoEdicao", true);
        model.addAttribute("unidadeId", id);
        return "pages/access/admin/unidades/form";
    }

    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id,
                            @Valid @ModelAttribute("form") UnidadeForm form,
                            BindingResult bindingResult,
                            Model model,
        RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            populateModel(model, form);
            model.addAttribute("modoEdicao", true);
            model.addAttribute("unidadeId", id);
            return "pages/access/admin/unidades/form";
        }
        try {
            unidadeAdminService.atualizar(id, form);
            redirectAttributes.addFlashAttribute("successMessage", "Unidade atualizada com sucesso");
            return "redirect:/ui/admin/unidades";
        } catch (IllegalArgumentException ex) {
            bindingResult.reject("unidade", ex.getMessage());
            populateModel(model, form);
            model.addAttribute("modoEdicao", true);
            model.addAttribute("unidadeId", id);
            return "pages/access/admin/unidades/form";
        }
    }

    @PostMapping("/{id}/ativar-desativar")
    public String ativarDesativar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        unidadeAdminService.ativarDesativar(id);
        redirectAttributes.addFlashAttribute("successMessage", "Status da unidade atualizado");
        return "redirect:/ui/admin/unidades";
    }

    private void populateModel(Model model, UnidadeForm form) {
        model.addAttribute("ufs", unidadeFederativaAdminService.listarTodas());
        model.addAttribute("tiposUnidade", tipoUnidadeRepository.findByAtivoOrderByDescricaoAsc(true));
        Long unidadeFederativaId = form == null ? null : form.getUnidadeFederativaId();
        model.addAttribute("cidades", unidadeFederativaId == null
                ? java.util.List.of()
                : cidadeRepository.findByUnidadeFederativaIdOrderByNome(unidadeFederativaId));
    }
}
