package br.com.his.access.api;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.his.access.api.dto.UsuarioAdminDetalheResponse;
import br.com.his.access.api.dto.UsuarioAdminResponse;
import br.com.his.access.api.dto.UsuarioVinculoRequest;
import br.com.his.access.api.dto.UsuarioVinculoResponse;
import br.com.his.access.dto.UsuarioVinculoForm;
import br.com.his.access.service.UsuarioAdminService;
import br.com.his.patient.api.error.ConflictException;
import br.com.his.patient.api.error.NotFoundException;
import jakarta.validation.Valid;

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
            var vinculos = usuarioAdminService.listarVinculos(id);
            return mapper.toDetalheResponse(usuario, vinculos);
        } catch (IllegalArgumentException ex) {
            throw mapException(ex);
        }
    }

    @GetMapping("/{id}/vinculos")
    public List<UsuarioVinculoResponse> listarVinculos(@PathVariable Long id) {
        try {
            return usuarioAdminService.listarVinculos(id).stream().map(mapper::toResponse).toList();
        } catch (IllegalArgumentException ex) {
            throw mapException(ex);
        }
    }

    @PostMapping("/{id}/vinculos")
    public UsuarioVinculoResponse adicionarVinculo(@PathVariable Long id,
                                                   @Valid @RequestBody UsuarioVinculoRequest request) {
        try {
            return mapper.toResponse(usuarioAdminService.adicionarVinculo(id, toForm(request)));
        } catch (IllegalArgumentException ex) {
            throw mapException(ex);
        }
    }

    @PutMapping("/{id}/vinculos/{vinculoId}")
    public UsuarioVinculoResponse atualizarVinculo(@PathVariable Long id,
                                                   @PathVariable Long vinculoId,
                                                   @Valid @RequestBody UsuarioVinculoRequest request) {
        try {
            if (request.getUnidadeId() != null) {
                var vinculoAtual = usuarioAdminService.listarVinculos(id).stream()
                        .filter(v -> v.getId().equals(vinculoId))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("Vinculo nao encontrado: " + vinculoId));
                if (!vinculoAtual.getUnidade().getId().equals(request.getUnidadeId())) {
                    throw new IllegalArgumentException("Alteracao de unidade do vinculo nao e permitida");
                }
            }
            return mapper.toResponse(usuarioAdminService.atualizarPerfilVinculo(vinculoId, request.getPerfilId()));
        } catch (IllegalArgumentException ex) {
            throw mapException(ex);
        }
    }

    @PostMapping("/{id}/vinculos/{vinculoId}/remover")
    public ResponseEntity<Void> removerVinculo(@PathVariable Long id, @PathVariable Long vinculoId) {
        try {
            usuarioAdminService.removerVinculo(vinculoId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException ex) {
            throw mapException(ex);
        }
    }

    private static UsuarioVinculoForm toForm(UsuarioVinculoRequest request) {
        UsuarioVinculoForm form = new UsuarioVinculoForm();
        form.setUnidadeId(request.getUnidadeId());
        form.setPerfilId(request.getPerfilId());
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
