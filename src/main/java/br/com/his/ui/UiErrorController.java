package br.com.his.ui;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
public class UiErrorController {

    @GetMapping("/ui/acesso-negado")
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String acessoNegado() {
        return "pages/error/acesso-negado";
    }
}
