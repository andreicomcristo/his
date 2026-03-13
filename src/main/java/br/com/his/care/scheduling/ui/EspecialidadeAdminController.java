package br.com.his.care.scheduling.ui;

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

import br.com.his.access.repository.CargoColaboradorRepository;
import br.com.his.care.scheduling.dto.EspecialidadeForm;
import br.com.his.care.scheduling.service.EspecialidadeAdminService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/ui/admin/especialidades")
public class EspecialidadeAdminController {

    private final EspecialidadeAdminService service;
    private final CargoColaboradorRepository cargoColaboradorRepository;

    public EspecialidadeAdminController(EspecialidadeAdminService service,
                                        CargoColaboradorRepository cargoColaboradorRepository) {
        this.service = service;
        this.cargoColaboradorRepository = cargoColaboradorRepository;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) String q,
                         @RequestParam(required = false) Boolean ativo,
                         Model model) {
        model.addAttribute("items", service.listar(q, ativo));
        model.addAttribute("q", q);
        model.addAttribute("ativo", ativo);
        return "pages/care/scheduling/admin/especialidades/list";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new EspecialidadeForm());
        }
        model.addAttribute("modoEdicao", false);
        populateModel(model);
        return "pages/care/scheduling/admin/especialidades/form";
    }

    @PostMapping
    public String criar(@Valid @ModelAttribute("form") EspecialidadeForm form,
                        BindingResult bindingResult,
                        Model model,
                        RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("modoEdicao", false);
            populateModel(model);
            return "pages/care/scheduling/admin/especialidades/form";
        }
        try {
            service.criar(form);
            redirectAttributes.addFlashAttribute("successMessage", "Especialidade cadastrada com sucesso");
            return "redirect:/ui/admin/especialidades";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("modoEdicao", false);
            model.addAttribute("errorMessage", ex.getMessage());
            populateModel(model);
            return "pages/care/scheduling/admin/especialidades/form";
        }
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("form", service.toForm(service.buscar(id)));
        model.addAttribute("modoEdicao", true);
        model.addAttribute("itemId", id);
        populateModel(model);
        return "pages/care/scheduling/admin/especialidades/form";
    }

    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id,
                            @Valid @ModelAttribute("form") EspecialidadeForm form,
                            BindingResult bindingResult,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("modoEdicao", true);
            model.addAttribute("itemId", id);
            populateModel(model);
            return "pages/care/scheduling/admin/especialidades/form";
        }
        try {
            service.atualizar(id, form);
            redirectAttributes.addFlashAttribute("successMessage", "Especialidade atualizada com sucesso");
            return "redirect:/ui/admin/especialidades";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("modoEdicao", true);
            model.addAttribute("itemId", id);
            model.addAttribute("errorMessage", ex.getMessage());
            populateModel(model);
            return "pages/care/scheduling/admin/especialidades/form";
        }
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        service.excluir(id);
        redirectAttributes.addFlashAttribute("successMessage", "Especialidade excluida com sucesso");
        return "redirect:/ui/admin/especialidades";
    }

    private void populateModel(Model model) {
        model.addAttribute("cargosAssistenciais", cargoColaboradorRepository.findAssistenciaisAtivosOrderByDescricaoAsc());
    }
}
