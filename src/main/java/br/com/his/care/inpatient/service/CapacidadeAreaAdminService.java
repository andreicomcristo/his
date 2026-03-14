package br.com.his.care.inpatient.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.his.care.inpatient.dto.CapacidadeAreaForm;
import br.com.his.care.inpatient.model.CapacidadeArea;
import br.com.his.care.inpatient.repository.CapacidadeAreaRepository;

@Service
public class CapacidadeAreaAdminService {

    private final CapacidadeAreaRepository repository;

    public CapacidadeAreaAdminService(CapacidadeAreaRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<CapacidadeArea> listar(String q) {
        String filtro = normalize(q);
        if (filtro == null) {
            return repository.findAllByOrderByNomeAsc();
        }
        return repository.buscarPorFiltro(filtro);
    }

    @Transactional(readOnly = true)
    public List<CapacidadeArea> listarTodas() {
        return repository.findAllByOrderByNomeAsc();
    }

    @Transactional(readOnly = true)
    public CapacidadeArea buscar(Long id) {
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Capacidade nao encontrada"));
    }

    @Transactional
    public CapacidadeArea criar(CapacidadeAreaForm form) {
        CapacidadeArea item = new CapacidadeArea();
        apply(item, form);
        return repository.save(item);
    }

    @Transactional
    public CapacidadeArea atualizar(Long id, CapacidadeAreaForm form) {
        CapacidadeArea item = buscar(id);
        apply(item, form);
        return repository.save(item);
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscar(id));
    }

    @Transactional(readOnly = true)
    public CapacidadeAreaForm toForm(CapacidadeArea item) {
        CapacidadeAreaForm form = new CapacidadeAreaForm();
        form.setNome(item.getNome());
        form.setDescricao(item.getDescricao());
        form.setAtivo(item.isAtivo());
        return form;
    }

    private void apply(CapacidadeArea item, CapacidadeAreaForm form) {
        item.setNome(normalizeUpper(form.getNome()));
        item.setDescricao(normalize(form.getDescricao()));
        item.setAtivo(form.isAtivo());
    }

    private static String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private static String normalizeUpper(String value) {
        String normalized = normalize(value);
        return normalized == null ? null : normalized.toUpperCase();
    }
}


