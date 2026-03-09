package br.com.his.access.ui;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import br.com.his.access.service.OperationalPermissionService;

@ControllerAdvice
public class CurrentOperationalPermissionViewAdvice {

    private final OperationalPermissionService operationalPermissionService;

    public CurrentOperationalPermissionViewAdvice(OperationalPermissionService operationalPermissionService) {
        this.operationalPermissionService = operationalPermissionService;
    }

    @ModelAttribute("canAtendimentoAcessar")
    public boolean canAtendimentoAcessar(Authentication authentication) {
        return operationalPermissionService.has(authentication, OperationalPermissionService.PERM_ATENDIMENTO_ACESSAR);
    }

    @ModelAttribute("canTriagemExecutar")
    public boolean canTriagemExecutar(Authentication authentication) {
        return operationalPermissionService.has(authentication, OperationalPermissionService.PERM_TRIAGEM_EXECUTAR);
    }

    @ModelAttribute("canRecepcaoExecutar")
    public boolean canRecepcaoExecutar(Authentication authentication) {
        return operationalPermissionService.has(authentication, OperationalPermissionService.PERM_RECEPCAO_EXECUTAR);
    }

    @ModelAttribute("canCriarAtendimento")
    public boolean canCriarAtendimento(Authentication authentication) {
        return operationalPermissionService.canCriarAtendimento(authentication);
    }

    @ModelAttribute("canAbrirEpisodio")
    public boolean canAbrirEpisodio(Authentication authentication) {
        return operationalPermissionService.has(authentication, OperationalPermissionService.PERM_EPISODIO_ABRIR);
    }

    @ModelAttribute("canRegistrarEntrada")
    public boolean canRegistrarEntrada(Authentication authentication) {
        return operationalPermissionService.has(authentication, OperationalPermissionService.PERM_ENTRADA_REGISTRAR);
    }

    @ModelAttribute("canPacienteIdentificar")
    public boolean canPacienteIdentificar(Authentication authentication) {
        return operationalPermissionService.has(authentication, OperationalPermissionService.PERM_PACIENTE_IDENTIFICAR);
    }

    @ModelAttribute("canBurocrataExecutar")
    public boolean canBurocrataExecutar(Authentication authentication) {
        return operationalPermissionService.has(authentication, OperationalPermissionService.PERM_BUROCRATA_EXECUTAR);
    }
}
