package br.com.his.access.api;

import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.his.access.api.dto.UsuarioAdminDetalheResponse;
import br.com.his.access.api.dto.UsuarioAdminResponse;
import br.com.his.access.service.UsuarioAdminService;
import br.com.his.patient.api.error.ConflictException;
import br.com.his.patient.api.error.NotFoundException;

@RestController
@RequestMapping("/api/admin/usuarios")
@Validated
public class AdminUsuarioApiController {

    private final UsuarioAdminService usuarioAdminService;
    private final AdminApiMapper mapper;

    public AdminUsuarioApiController(UsuarioAdminService usuarioAdminService, AdminApiMapper mapper) {
        this.usuarioAdminService = usuarioAdminService;
        this.mapper = mapper;
    }

    @GetMapping
    public List<UsuarioAdminResponse> listar(@RequestParam(required = false, name = "q") String filtro) {
        return usuarioAdminService.listar(filtro).stream().map(mapper::toResponse).toList();
    }

    @GetMapping("/{id}")
    public UsuarioAdminDetalheResponse detalhe(@PathVariable Long id) {
        try {
            var usuario = usuarioAdminService.buscarPorId(id);
            return mapper.toDetalheResponse(usuario);
        } catch (IllegalArgumentException ex) {
            throw mapException(ex);
        }
    }

    private RuntimeException mapException(IllegalArgumentException ex) {
        String message = ex.getMessage() == null ? "Erro de negocio" : ex.getMessage();
        String lower = message.toLowerCase();
        if (lower.contains("nao encontrada") || lower.contains("nao encontrado")) {
            return new NotFoundException(message);
        }
        return new ConflictException(message);
    }
}
