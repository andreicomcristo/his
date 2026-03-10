package br.com.his.patient.api;

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

import br.com.his.patient.model.Paciente;
import br.com.his.patient.service.PacienteService;
import br.com.his.patient.api.dto.PacienteMergeRequest;
import br.com.his.patient.api.dto.PacienteRequest;
import br.com.his.patient.api.dto.PacienteResponse;
import br.com.his.patient.api.dto.PacienteTemporarioRequest;
import br.com.his.patient.api.error.ConflictException;
import br.com.his.patient.api.error.NotFoundException;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/pacientes")
@Validated
public class PacienteApiController {

    private final PacienteService pacienteService;
    private final PacienteApiMapper mapper;

    public PacienteApiController(PacienteService pacienteService, PacienteApiMapper mapper) {
        this.pacienteService = pacienteService;
        this.mapper = mapper;
    }

    @GetMapping
    public List<PacienteResponse> listar(@RequestParam(required = false) String cpf,
                                         @RequestParam(required = false) String cns,
                                         @RequestParam(required = false) String nome) {
        return pacienteService.buscarPorCpfCnsNome(nome, cpf, cns)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public PacienteResponse buscarPorId(@PathVariable Long id) {
        try {
            return mapper.toResponse(pacienteService.buscarPorId(id));
        } catch (IllegalArgumentException ex) {
            throw mapServiceException(ex);
        }
    }

    @PostMapping
    public ResponseEntity<PacienteResponse> criarDefinitivo(@Valid @RequestBody PacienteRequest request) {
        try {
            Paciente saved = pacienteService.criarPacienteDefinitivo(mapper.toForm(request));
            return ResponseEntity.created(URI.create("/api/pacientes/" + saved.getId()))
                    .body(mapper.toResponse(saved));
        } catch (IllegalArgumentException ex) {
            throw mapServiceException(ex);
        }
    }

    @PostMapping("/temporario")
    public ResponseEntity<PacienteResponse> criarTemporario(@Valid @RequestBody PacienteTemporarioRequest request) {
        try {
            Paciente saved = pacienteService.criarPacienteTemporario(mapper.toForm(request));
            return ResponseEntity.created(URI.create("/api/pacientes/" + saved.getId()))
                    .body(mapper.toResponse(saved));
        } catch (IllegalArgumentException ex) {
            throw mapServiceException(ex);
        }
    }

    @PutMapping("/{id}")
    public PacienteResponse atualizar(@PathVariable Long id,
                                      @Valid @RequestBody PacienteRequest request) {
        try {
            Paciente updated = pacienteService.atualizarPaciente(id, mapper.toForm(request));
            return mapper.toResponse(updated);
        } catch (IllegalArgumentException ex) {
            throw mapServiceException(ex);
        }
    }

    @PostMapping("/{id}/cancelar")
    public ResponseEntity<Void> cancelar(@PathVariable Long id) {
        try {
            pacienteService.cancelarPaciente(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException ex) {
            throw mapServiceException(ex);
        }
    }

    @PostMapping("/{id}/restaurar")
    public ResponseEntity<Void> restaurar(@PathVariable Long id) {
        try {
            pacienteService.restaurarPaciente(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException ex) {
            throw mapServiceException(ex);
        }
    }

    @PostMapping("/merge")
    public ResponseEntity<Void> merge(@Valid @RequestBody PacienteMergeRequest request) {
        try {
            pacienteService.mergePaciente(request.getFromId(), request.getToId(), request.getMotivo());
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException ex) {
            throw mapServiceException(ex);
        }
    }

    private RuntimeException mapServiceException(IllegalArgumentException ex) {
        String message = ex.getMessage() == null ? "Erro de negocio" : ex.getMessage();
        String lower = message.toLowerCase();

        if (lower.contains("nao encontrado")) {
            return new NotFoundException(message);
        }

        return new ConflictException(message);
    }
}
