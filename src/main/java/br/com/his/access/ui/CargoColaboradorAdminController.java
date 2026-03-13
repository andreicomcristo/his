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

import br.com.his.access.dto.CargoColaboradorForm;
import br.com.his.access.service.CargoColaboradorAdminService;
import br.com.his.access.service.TipoCargoAdminService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/ui/admin/cargos-colaborador")
public class CargoColaboradorAdminController {

    private final CargoColaboradorAdminService service;
    private final TipoCargoAdminService tipoCargoAdminService;

    public CargoColaboradorAdminController(CargoColaboradorAdminService service,
                                           TipoCargoAdminService tipoCargoAdminService) {
        this.service = service;
        this.tipoCargoAdminService = tipoCargoAdminService;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) String q,
                         @RequestParam(required = false) Boolean ativo,
                         Model model) {
        model.addAttribute("items", service.listar(q, ativo));
        model.addAttribute("q", q);
        model.addAttribute("ativo", ativo);
        return "pages/access/admin/cargos-colaborador/list";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new CargoColaboradorForm());
        }
        popularCombos(model);
        model.addAttribute("modoEdicao", false);
        return "pages/access/admin/cargos-colaborador/form";
    }

    @PostMapping
    public String criar(@Valid @ModelAttribute("form") CargoColaboradorForm form,
                        BindingResult bindingResult,
                        Model model,
                        RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            popularCombos(model);
            model.addAttribute("modoEdicao", false);
            return "pages/access/admin/cargos-colaborador/form";
        }
        try {
            service.criar(form);
            redirectAttributes.addFlashAttribute("successMessage", "Cargo cadastrado com sucesso");
            return "redirect:/ui/admin/cargos-colaborador";
        } catch (IllegalArgumentException ex) {
            popularCombos(model);
            model.addAttribute("modoEdicao", false);
            model.addAttribute("errorMessage", ex.getMessage());
            return "pages/access/admin/cargos-colaborador/form";
        }
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("form", service.toForm(service.buscar(id)));
        popularCombos(model);
        model.addAttribute("modoEdicao", true);
        model.addAttribute("itemId", id);
        return "pages/access/admin/cargos-colaborador/form";
    }

    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id,
                            @Valid @ModelAttribute("form") CargoColaboradorForm form,
                            BindingResult bindingResult,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            popularCombos(model);
            model.addAttribute("modoEdicao", true);
            model.addAttribute("itemId", id);
            return "pages/access/admin/cargos-colaborador/form";
        }
        try {
            service.atualizar(id, form);
            redirectAttributes.addFlashAttribute("successMessage", "Cargo atualizado com sucesso");
            return "redirect:/ui/admin/cargos-colaborador";
        } catch (IllegalArgumentException ex) {
            popularCombos(model);
            model.addAttribute("modoEdicao", true);
            model.addAttribute("itemId", id);
            model.addAttribute("errorMessage", ex.getMessage());
            return "pages/access/admin/cargos-colaborador/form";
        }
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id,
                          RedirectAttributes redirectAttributes) {
        try {
            service.excluir(id);
            redirectAttributes.addFlashAttribute("successMessage", "Cargo excluido com sucesso");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/ui/admin/cargos-colaborador";
    }

    private void popularCombos(Model model) {
        model.addAttribute("tipos", tipoCargoAdminService.listar(null, true));
    }
}
