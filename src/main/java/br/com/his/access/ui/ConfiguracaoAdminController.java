package br.com.his.access.ui;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import br.com.his.patient.dto.PacienteCatalogoTipo;

@Controller
@RequestMapping("/ui/admin/configuracoes")
public class ConfiguracaoAdminController {

    @GetMapping
    public String index(Model model) {
        model.addAttribute("catalogosPaciente", PacienteCatalogoTipo.visiveis());
        return "pages/access/admin/configuracoes/index";
    }
}
