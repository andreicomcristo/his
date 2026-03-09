package br.com.his.admin.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.his.admin.dto.AlergiaSubstanciaForm;
import br.com.his.assistencial.model.AlergiaSubstancia;
import br.com.his.assistencial.repository.AlergiaSubstanciaRepository;

@Service
public class AlergiaSubstanciaAdminService {

    private final AlergiaSubstanciaRepository repository;

    public AlergiaSubstanciaAdminService(AlergiaSubstanciaRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<AlergiaSubstancia> listar(String q) {
        String filtro = normalize(q);
        if (filtro == null) {
            return repository.findAllByOrderByDescricaoAsc();
        }
        return repository.buscarPorFiltro(filtro);
    }

    @Transactional(readOnly = true)
    public List<AlergiaSubstancia> listarAtivos() {
        return repository.findByAtivoTrueOrderByDescricaoAsc();
    }

    @Transactional(readOnly = true)
    public AlergiaSubstancia buscar(Long id) {
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Substancia de alergia nao encontrada"));
    }

    @Transactional
    public AlergiaSubstancia criar(AlergiaSubstanciaForm form) {
        AlergiaSubstancia item = new AlergiaSubstancia();
        apply(item, form);
        return repository.save(item);
    }

    @Transactional
    public AlergiaSubstancia atualizar(Long id, AlergiaSubstanciaForm form) {
        AlergiaSubstancia item = buscar(id);
        apply(item, form);
        return repository.save(item);
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscar(id));
    }

    @Transactional(readOnly = true)
    public AlergiaSubstanciaForm toForm(AlergiaSubstancia item) {
        AlergiaSubstanciaForm form = new AlergiaSubstanciaForm();
        form.setDescricao(item.getDescricao());
        form.setAtivo(item.isAtivo());
        return form;
    }

    private void apply(AlergiaSubstancia item, AlergiaSubstanciaForm form) {
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
