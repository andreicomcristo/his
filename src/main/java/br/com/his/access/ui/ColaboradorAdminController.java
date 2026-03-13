package br.com.his.access.ui;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

import br.com.his.access.dto.ColaboradorForm;
import br.com.his.access.model.Colaborador;
import br.com.his.access.model.UsuarioColaborador;
import br.com.his.access.repository.UsuarioColaboradorRepository;
import br.com.his.access.service.CargoColaboradorAdminService;
import br.com.his.access.service.ColaboradorAdminService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/ui/admin/colaboradores")
public class ColaboradorAdminController {

    private final ColaboradorAdminService service;
    private final CargoColaboradorAdminService cargoColaboradorAdminService;
    private final UsuarioColaboradorRepository usuarioColaboradorRepository;

    public ColaboradorAdminController(ColaboradorAdminService service,
                                      CargoColaboradorAdminService cargoColaboradorAdminService,
                                      UsuarioColaboradorRepository usuarioColaboradorRepository) {
        this.service = service;
        this.cargoColaboradorAdminService = cargoColaboradorAdminService;
        this.usuarioColaboradorRepository = usuarioColaboradorRepository;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) String q,
                         @RequestParam(required = false) Boolean ativo,
                         Model model) {
        List<Colaborador> items = service.listar(q, ativo);
        model.addAttribute("items", items);
        model.addAttribute("q", q);
        model.addAttribute("ativo", ativo);
        model.addAttribute("usuarioPorColaboradorId", carregarUsuariosPorColaborador(items));
        return "pages/access/admin/colaboradores/list";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new ColaboradorForm());
        }
        popularCombos(model);
        model.addAttribute("modoEdicao", false);
        return "pages/access/admin/colaboradores/form";
    }

    @PostMapping
    public String criar(@Valid @ModelAttribute("form") ColaboradorForm form,
                        BindingResult bindingResult,
                        Model model,
                        RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            popularCombos(model);
            model.addAttribute("modoEdicao", false);
            return "pages/access/admin/colaboradores/form";
        }
        try {
            service.criar(form);
            redirectAttributes.addFlashAttribute("successMessage", "Colaborador cadastrado com sucesso");
            return "redirect:/ui/admin/colaboradores";
        } catch (IllegalArgumentException ex) {
            popularCombos(model);
            model.addAttribute("modoEdicao", false);
            model.addAttribute("errorMessage", ex.getMessage());
            return "pages/access/admin/colaboradores/form";
        }
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("form", service.toForm(service.buscar(id)));
        popularCombos(model);
        model.addAttribute("modoEdicao", true);
        model.addAttribute("itemId", id);
        return "pages/access/admin/colaboradores/form";
    }

    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id,
                            @Valid @ModelAttribute("form") ColaboradorForm form,
                            BindingResult bindingResult,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            popularCombos(model);
            model.addAttribute("modoEdicao", true);
            model.addAttribute("itemId", id);
            return "pages/access/admin/colaboradores/form";
        }
        try {
            service.atualizar(id, form);
            redirectAttributes.addFlashAttribute("successMessage", "Colaborador atualizado com sucesso");
            return "redirect:/ui/admin/colaboradores";
        } catch (IllegalArgumentException ex) {
            popularCombos(model);
            model.addAttribute("modoEdicao", true);
            model.addAttribute("itemId", id);
            model.addAttribute("errorMessage", ex.getMessage());
            return "pages/access/admin/colaboradores/form";
        }
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id,
                          RedirectAttributes redirectAttributes) {
        try {
            service.excluir(id);
            redirectAttributes.addFlashAttribute("successMessage", "Colaborador excluido com sucesso");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/ui/admin/colaboradores";
    }

    private void popularCombos(Model model) {
        model.addAttribute("cargos", cargoColaboradorAdminService.listar(null, null));
    }

    private Map<Long, UsuarioColaborador> carregarUsuariosPorColaborador(List<Colaborador> items) {
        if (items == null || items.isEmpty()) {
            return Map.of();
        }
        List<Long> ids = items.stream().map(Colaborador::getId).toList();
        List<UsuarioColaborador> vinculos = usuarioColaboradorRepository.findByColaboradorIdsComUsuario(ids);
        Map<Long, UsuarioColaborador> mapa = new LinkedHashMap<>();
        for (UsuarioColaborador vinculo : vinculos) {
            mapa.put(vinculo.getColaborador().getId(), vinculo);
        }
        return mapa;
    }
}
