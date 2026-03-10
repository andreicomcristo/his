package br.com.his.care.episode.ui;

import br.com.his.care.attendance.api.dto.*;
import br.com.his.care.attendance.dto.*;
import br.com.his.care.attendance.model.*;
import br.com.his.care.attendance.repository.*;
import br.com.his.care.attendance.service.*;
import br.com.his.care.admission.dto.*;
import br.com.his.care.admission.model.*;
import br.com.his.care.admission.repository.*;
import br.com.his.care.triage.dto.*;
import br.com.his.care.triage.model.*;
import br.com.his.care.triage.repository.*;
import br.com.his.care.inpatient.dto.*;
import br.com.his.care.inpatient.model.*;
import br.com.his.care.inpatient.repository.*;
import br.com.his.care.inpatient.service.*;
import br.com.his.care.episode.model.*;
import br.com.his.care.episode.repository.*;
import br.com.his.care.timeline.dto.*;
import br.com.his.care.timeline.model.*;
import br.com.his.care.timeline.repository.*;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.his.access.service.OperationalPermissionService;
import org.springframework.http.HttpStatus;

@Controller
@RequestMapping("/ui/episodios")
public class EpisodioController {

    private final OperationalPermissionService operationalPermissionService;

    public EpisodioController(OperationalPermissionService operationalPermissionService) {
        this.operationalPermissionService = operationalPermissionService;
    }

    @GetMapping("/abrir/{atendimentoId}")
    public String abrir(@PathVariable Long atendimentoId, RedirectAttributes redirectAttributes) {
        requirePermission();
        redirectAttributes.addFlashAttribute("successMessage",
                "Preencha e salve a entrada do atendimento.");
        return "redirect:/ui/entradas/atendimento/" + atendimentoId;
    }

    private void requirePermission() {
        if (!operationalPermissionService.has(
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication(),
                OperationalPermissionService.PERM_EPISODIO_ABRIR)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Sem permissao para abrir episodio");
        }
    }
}
