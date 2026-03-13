package br.com.his.access.ui;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.his.access.context.UnidadeContext;
import br.com.his.access.service.AccessContextService;
import br.com.his.access.service.ColaboradorAtuacaoService;
import br.com.his.access.service.UserIdentity;

@Controller
@RequestMapping("/ui")
public class UnidadeSelectionController {

    private final AccessContextService accessContextService;
    private final ColaboradorAtuacaoService colaboradorAtuacaoService;
    private final UnidadeContext unidadeContext;

    public UnidadeSelectionController(AccessContextService accessContextService,
                                      ColaboradorAtuacaoService colaboradorAtuacaoService,
                                      UnidadeContext unidadeContext) {
        this.accessContextService = accessContextService;
        this.colaboradorAtuacaoService = colaboradorAtuacaoService;
        this.unidadeContext = unidadeContext;
    }

    @GetMapping("/escolher-unidade")
    public String escolherUnidade(Authentication authentication, Model model) {
        Optional<UserIdentity> userOpt = accessContextService.resolveAuthenticatedUser(authentication);
        if (userOpt.isEmpty()) {
            return "redirect:/login";
        }

        List<ColaboradorAtuacaoService.AtuacaoResumo> contextos =
                colaboradorAtuacaoService.listarContextosAtivosDoUsuario(userOpt.get().keycloakId());
        if (contextos.isEmpty()) {
            model.addAttribute("mensagem", "Usuario sem atuacao ativa nas unidades vinculadas.");
            return "pages/error/sem-unidade";
        }

        if (contextos.size() == 1) {
            ColaboradorAtuacaoService.AtuacaoResumo unico = contextos.getFirst();
            unidadeContext.setUnidadeAtual(unico.unidadeId());
            unidadeContext.setAtuacaoAtual(unico.id());
            return "redirect:/ui/home";
        }

        Long unidadeAtualId = unidadeContext.getUnidadeAtual().orElse(null);
        Long colaboradorUnidadeAtuacaoId = unidadeContext.getAtuacaoAtual().orElse(null);
        boolean contextoAtualValido = false;
        if (unidadeAtualId != null && colaboradorUnidadeAtuacaoId != null) {
            for (ColaboradorAtuacaoService.AtuacaoResumo contexto : contextos) {
                if (contexto.unidadeId().equals(unidadeAtualId) && contexto.id().equals(colaboradorUnidadeAtuacaoId)) {
                    contextoAtualValido = true;
                    break;
                }
            }
        }
        if (!contextoAtualValido) {
            unidadeContext.clear();
            unidadeAtualId = null;
            colaboradorUnidadeAtuacaoId = null;
        }

        model.addAttribute("unidades", agruparPorUnidade(contextos));
        model.addAttribute("unidadeAtualId", unidadeAtualId);
        model.addAttribute("colaboradorUnidadeAtuacaoId", colaboradorUnidadeAtuacaoId);
        return "pages/unidade/escolher";
    }

    @PostMapping("/escolher-unidade")
    public String confirmarUnidade(@RequestParam Long unidadeId,
                                   @RequestParam Long colaboradorUnidadeAtuacaoId,
                                   Authentication authentication,
                                   RedirectAttributes redirectAttributes) {
        Optional<UserIdentity> userOpt = accessContextService.resolveAuthenticatedUser(authentication);
        if (userOpt.isEmpty()) {
            return "redirect:/login";
        }

        boolean podeUsar = colaboradorAtuacaoService.usuarioPossuiAtuacaoAtiva(
                userOpt.get().keycloakId(), unidadeId, colaboradorUnidadeAtuacaoId);
        if (!podeUsar) {
            redirectAttributes.addFlashAttribute("errorMessage", "Contexto de unidade/atuacao invalido para o usuario logado.");
            return "redirect:/ui/escolher-unidade";
        }

        unidadeContext.setUnidadeAtual(unidadeId);
        unidadeContext.setAtuacaoAtual(colaboradorUnidadeAtuacaoId);
        return "redirect:/ui/home";
    }

    @GetMapping("/sem-unidade")
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String semUnidade(Model model) {
        if (!model.containsAttribute("mensagem")) {
            model.addAttribute("mensagem", "Usuario sem unidade vinculada.");
        }
        return "pages/error/sem-unidade";
    }

    @GetMapping("/sem-atuacao")
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String semAtuacao(Model model) {
        if (!model.containsAttribute("mensagem")) {
            model.addAttribute("mensagem", "Usuario sem atuacao ativa na unidade selecionada.");
        }
        return "pages/error/sem-unidade";
    }

    private List<UnidadeContextoResumo> agruparPorUnidade(List<ColaboradorAtuacaoService.AtuacaoResumo> contextos) {
        Map<Long, List<ColaboradorAtuacaoService.AtuacaoResumo>> atuacoesPorUnidade = new LinkedHashMap<>();
        Map<Long, ColaboradorAtuacaoService.AtuacaoResumo> contextoBasePorUnidade = new LinkedHashMap<>();
        for (ColaboradorAtuacaoService.AtuacaoResumo contexto : contextos) {
            contextoBasePorUnidade.putIfAbsent(contexto.unidadeId(), contexto);
            atuacoesPorUnidade.computeIfAbsent(contexto.unidadeId(), id -> new ArrayList<>()).add(contexto);
        }

        List<UnidadeContextoResumo> resultado = new ArrayList<>();
        for (Map.Entry<Long, List<ColaboradorAtuacaoService.AtuacaoResumo>> entry : atuacoesPorUnidade.entrySet()) {
            ColaboradorAtuacaoService.AtuacaoResumo base = contextoBasePorUnidade.get(entry.getKey());
            resultado.add(new UnidadeContextoResumo(
                    base.unidadeId(),
                    base.unidadeNome(),
                    base.unidadeTipoEstabelecimento(),
                    base.unidadeCnes(),
                    entry.getValue()));
        }
        return resultado;
    }

    public record UnidadeContextoResumo(Long unidadeId,
                                        String unidadeNome,
                                        String unidadeTipoEstabelecimento,
                                        String unidadeCnes,
                                        List<ColaboradorAtuacaoService.AtuacaoResumo> atuacoes) {
    }
}
