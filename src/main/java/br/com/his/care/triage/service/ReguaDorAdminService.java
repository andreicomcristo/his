package br.com.his.care.triage.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.his.care.triage.dto.ReguaDorForm;
import br.com.his.care.triage.model.ReguaDor;
import br.com.his.care.triage.repository.ReguaDorRepository;

@Service
public class ReguaDorAdminService {

    private final ReguaDorRepository repository;

    public ReguaDorAdminService(ReguaDorRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<ReguaDor> listar(String q) {
        String filtro = normalize(q);
        if (filtro == null) {
            return repository.findAllByOrderByValorAsc();
        }
        return repository.buscarPorFiltro(filtro);
    }

    @Transactional(readOnly = true)
    public List<ReguaDor> listarAtivos() {
        return repository.findByAtivoTrueOrderByValorAsc();
    }

    @Transactional(readOnly = true)
    public ReguaDor buscar(Long id) {
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Regua de dor nao encontrada"));
    }

    @Transactional
    public ReguaDor criar(ReguaDorForm form) {
        ReguaDor item = new ReguaDor();
        apply(item, form);
        return repository.save(item);
    }

    @Transactional
    public ReguaDor atualizar(Long id, ReguaDorForm form) {
        ReguaDor item = buscar(id);
        apply(item, form);
        return repository.save(item);
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscar(id));
    }

    @Transactional(readOnly = true)
    public ReguaDorForm toForm(ReguaDor item) {
        ReguaDorForm form = new ReguaDorForm();
        form.setValor(item.getValor());
        form.setDescricao(item.getDescricao());
        form.setAtivo(item.isAtivo());
        return form;
    }

    private void apply(ReguaDor item, ReguaDorForm form) {
        item.setValor(form.getValor());
        item.setDescricao(normalizeUpper(form.getDescricao()));
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
