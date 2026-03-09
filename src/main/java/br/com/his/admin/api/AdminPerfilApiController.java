package br.com.his.admin.api;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.his.admin.api.dto.PerfilAdminRequest;
import br.com.his.admin.api.dto.PerfilAdminResponse;
import br.com.his.admin.api.dto.PerfilPermissoesRequest;
import br.com.his.admin.dto.PerfilForm;
import br.com.his.admin.service.PerfilAdminService;
import br.com.his.patient.api.error.ConflictException;
import br.com.his.patient.api.error.NotFoundException;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/perfis")
@Validated
public class AdminPerfilApiController {

    private final PerfilAdminService perfilAdminService;
    private final AdminApiMapper mapper;

    public AdminPerfilApiController(PerfilAdminService perfilAdminService, AdminApiMapper mapper) {
        this.perfilAdminService = perfilAdminService;
        this.mapper = mapper;
    }

    @GetMapping
    public List<PerfilAdminResponse> listar(@RequestParam(required = false, name = "q") String filtro) {
        return perfilAdminService.listar(filtro).stream()
                .map(perfil -> mapper.toResponse(perfil,
                        perfilAdminService.listarIdsPermissoesPerfil(perfil.getId()).stream().toList()))
                .toList();
    }

    @GetMapping("/{id}")
    public PerfilAdminResponse buscar(@PathVariable Long id) {
        try {
            var perfil = perfilAdminService.buscarPorId(id);
            return mapper.toResponse(perfil, perfilAdminService.listarIdsPermissoesPerfil(id).stream().toList());
        } catch (IllegalArgumentException ex) {
            throw mapException(ex);
        }
    }

    @PostMapping
    public ResponseEntity<PerfilAdminResponse> criar(@Valid @RequestBody PerfilAdminRequest request) {
        try {
            var perfil = perfilAdminService.criar(toForm(request));
            var body = mapper.toResponse(perfil, List.of());
            return ResponseEntity.created(URI.create("/api/admin/perfis/" + perfil.getId())).body(body);
        } catch (IllegalArgumentException ex) {
            throw mapException(ex);
        }
    }

    @PutMapping("/{id}")
    public PerfilAdminResponse atualizar(@PathVariable Long id, @Valid @RequestBody PerfilAdminRequest request) {
        try {
            var perfil = perfilAdminService.atualizar(id, toForm(request));
            return mapper.toResponse(perfil, perfilAdminService.listarIdsPermissoesPerfil(id).stream().toList());
        } catch (IllegalArgumentException ex) {
            throw mapException(ex);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        try {
            perfilAdminService.remover(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException ex) {
            throw mapException(ex);
        }
    }

    @PutMapping("/{id}/permissoes")
    public ResponseEntity<Void> atualizarPermissoes(@PathVariable Long id,
                                                    @RequestBody PerfilPermissoesRequest request) {
        try {
            perfilAdminService.atualizarPermissoes(id, request.getPermissaoIds());
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException ex) {
            throw mapException(ex);
        }
    }

    private static PerfilForm toForm(PerfilAdminRequest request) {
        PerfilForm form = new PerfilForm();
        form.setNome(request.getNome());
        return form;
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
