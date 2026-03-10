package br.com.his.admin.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.his.admin.dto.ComorbidadeForm;
import br.com.his.care.triage.model.Comorbidade;
import br.com.his.care.triage.repository.ComorbidadeRepository;

@Service
public class ComorbidadeAdminService {

    private final ComorbidadeRepository repository;

    public ComorbidadeAdminService(ComorbidadeRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Comorbidade> listar(String q) {
        String filtro = normalize(q);
        if (filtro == null) {
            return repository.findAllByOrderByDescricaoAsc();
        }
        return repository.buscarPorFiltro(filtro);
    }

    @Transactional(readOnly = true)
    public List<Comorbidade> listarAtivos() {
        return repository.findByAtivoTrueOrderByDescricaoAsc();
    }

    @Transactional(readOnly = true)
    public Comorbidade buscar(Long id) {
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Comorbidade nao encontrada"));
    }

    @Transactional
    public Comorbidade criar(ComorbidadeForm form) {
        Comorbidade item = new Comorbidade();
        apply(item, form);
        return repository.save(item);
    }

    @Transactional
    public Comorbidade atualizar(Long id, ComorbidadeForm form) {
        Comorbidade item = buscar(id);
        apply(item, form);
        return repository.save(item);
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscar(id));
    }

    @Transactional(readOnly = true)
    public ComorbidadeForm toForm(Comorbidade item) {
        ComorbidadeForm form = new ComorbidadeForm();
        form.setDescricao(item.getDescricao());
        form.setAtivo(item.isAtivo());
        return form;
    }

    private void apply(Comorbidade item, ComorbidadeForm form) {
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
