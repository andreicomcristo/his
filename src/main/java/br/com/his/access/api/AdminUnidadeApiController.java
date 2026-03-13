package br.com.his.access.api;

import java.net.URI;
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

import br.com.his.access.api.dto.UnidadeAdminRequest;
import br.com.his.access.api.dto.UnidadeAdminResponse;
import br.com.his.access.dto.UnidadeForm;
import br.com.his.access.service.UnidadeAdminService;
import br.com.his.patient.api.error.ConflictException;
import br.com.his.patient.api.error.NotFoundException;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/unidades")
@Validated
public class AdminUnidadeApiController {

    private final UnidadeAdminService unidadeAdminService;
    private final AdminApiMapper mapper;

    public AdminUnidadeApiController(UnidadeAdminService unidadeAdminService, AdminApiMapper mapper) {
        this.unidadeAdminService = unidadeAdminService;
        this.mapper = mapper;
    }

    @GetMapping
    public List<UnidadeAdminResponse> listar(@RequestParam(required = false, name = "q") String filtro) {
        return unidadeAdminService.listar(filtro).stream().map(mapper::toResponse).toList();
    }

    @GetMapping("/{id}")
    public UnidadeAdminResponse buscar(@PathVariable Long id) {
        try {
            return mapper.toResponse(unidadeAdminService.buscarPorId(id));
        } catch (IllegalArgumentException ex) {
            throw mapException(ex);
        }
    }

    @PostMapping
    public ResponseEntity<UnidadeAdminResponse> criar(@Valid @RequestBody UnidadeAdminRequest request) {
        try {
            UnidadeForm form = toForm(request);
            var unidade = unidadeAdminService.criar(form);
            return ResponseEntity.created(URI.create("/api/admin/unidades/" + unidade.getId()))
                    .body(mapper.toResponse(unidade));
        } catch (IllegalArgumentException ex) {
            throw mapException(ex);
        }
    }

    @PutMapping("/{id}")
    public UnidadeAdminResponse atualizar(@PathVariable Long id, @Valid @RequestBody UnidadeAdminRequest request) {
        try {
            return mapper.toResponse(unidadeAdminService.atualizar(id, toForm(request)));
        } catch (IllegalArgumentException ex) {
            throw mapException(ex);
        }
    }

    @PostMapping("/{id}/ativar-desativar")
    public ResponseEntity<Void> ativarDesativar(@PathVariable Long id) {
        try {
            unidadeAdminService.ativarDesativar(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException ex) {
            throw mapException(ex);
        }
    }

    private static UnidadeForm toForm(UnidadeAdminRequest request) {
        UnidadeForm form = new UnidadeForm();
        form.setNome(request.getNome());
        form.setTipoUnidadeId(request.getTipoUnidadeId());
        form.setSigla(request.getSigla());
        form.setCnes(request.getCnes());
        form.setUnidadeFederativaId(request.getUnidadeFederativaId());
        form.setCidadeId(request.getCidadeId());
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
