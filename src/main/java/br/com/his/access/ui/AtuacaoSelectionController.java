package br.com.his.access.ui;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/ui")
public class AtuacaoSelectionController {

    @GetMapping("/escolher-atuacao")
    public String escolherAtuacao() {
        return "redirect:/ui/escolher-unidade";
    }

    @PostMapping("/escolher-atuacao")
    public String confirmarAtuacao() {
        return "redirect:/ui/escolher-unidade";
    }
}
