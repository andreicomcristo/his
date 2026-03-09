package br.com.his.admin.ui;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import br.com.his.admin.dto.PacienteCatalogoTipo;

@Controller
@RequestMapping("/ui/admin/configuracoes")
public class ConfiguracaoAdminController {

    @GetMapping
    public String index(Model model) {
        model.addAttribute("catalogosPaciente", PacienteCatalogoTipo.visiveis());
        return "pages/admin/configuracoes/index";
    }
}
