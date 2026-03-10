package br.com.his.admin.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.his.admin.dto.AlergiaSeveridadeForm;
import br.com.his.care.triage.model.AlergiaSeveridade;
import br.com.his.care.triage.repository.AlergiaSeveridadeRepository;

@Service
public class AlergiaSeveridadeAdminService {

    private final AlergiaSeveridadeRepository repository;

    public AlergiaSeveridadeAdminService(AlergiaSeveridadeRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<AlergiaSeveridade> listar(String q) {
        String filtro = normalize(q);
        if (filtro == null) {
            return repository.findAllByOrderByDescricaoAsc();
        }
        return repository.buscarPorFiltro(filtro);
    }

    @Transactional(readOnly = true)
    public List<AlergiaSeveridade> listarAtivos() {
        return repository.findByAtivoTrueOrderByDescricaoAsc();
    }

    @Transactional(readOnly = true)
    public AlergiaSeveridade buscar(Long id) {
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Severidade de alergia nao encontrada"));
    }

    @Transactional
    public AlergiaSeveridade criar(AlergiaSeveridadeForm form) {
        AlergiaSeveridade item = new AlergiaSeveridade();
        apply(item, form);
        return repository.save(item);
    }

    @Transactional
    public AlergiaSeveridade atualizar(Long id, AlergiaSeveridadeForm form) {
        AlergiaSeveridade item = buscar(id);
        apply(item, form);
        return repository.save(item);
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscar(id));
    }

    @Transactional(readOnly = true)
    public AlergiaSeveridadeForm toForm(AlergiaSeveridade item) {
        AlergiaSeveridadeForm form = new AlergiaSeveridadeForm();
        form.setDescricao(item.getDescricao());
        form.setAtivo(item.isAtivo());
        return form;
    }

    private void apply(AlergiaSeveridade item, AlergiaSeveridadeForm form) {
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
