package br.com.his.ui;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/ui/minha-conta")
public class AccountController {

    @GetMapping("/trocar-senha")
    public String trocarSenha() {
        return "redirect:/oauth2/authorization/keycloak?kc_action=UPDATE_PASSWORD";
    }
}
