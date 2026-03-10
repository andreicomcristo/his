package br.com.his.care.admission.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.his.care.admission.dto.GrauParentescoForm;
import br.com.his.care.admission.model.GrauParentesco;
import br.com.his.care.admission.repository.GrauParentescoRepository;

@Service
public class GrauParentescoAdminService {

    private final GrauParentescoRepository repository;

    public GrauParentescoAdminService(GrauParentescoRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<GrauParentesco> listar(String q) {
        String filtro = normalize(q);
        if (filtro == null) {
            return repository.findAllByOrderByDescricaoAsc();
        }
        return repository.buscarPorFiltro(filtro);
    }

    @Transactional(readOnly = true)
    public List<GrauParentesco> listarAtivos() {
        return repository.findByAtivoTrueOrderByDescricaoAsc();
    }

    @Transactional(readOnly = true)
    public GrauParentesco buscar(Long id) {
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Grau de parentesco nao encontrado"));
    }

    @Transactional
    public GrauParentesco criar(GrauParentescoForm form) {
        GrauParentesco item = new GrauParentesco();
        apply(item, form);
        return repository.save(item);
    }

    @Transactional
    public GrauParentesco atualizar(Long id, GrauParentescoForm form) {
        GrauParentesco item = buscar(id);
        apply(item, form);
        return repository.save(item);
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscar(id));
    }

    @Transactional(readOnly = true)
    public GrauParentescoForm toForm(GrauParentesco item) {
        GrauParentescoForm form = new GrauParentescoForm();
        form.setDescricao(item.getDescricao());
        form.setAtivo(item.isAtivo());
        return form;
    }

    private void apply(GrauParentesco item, GrauParentescoForm form) {
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
