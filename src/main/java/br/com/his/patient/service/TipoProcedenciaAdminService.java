package br.com.his.patient.service;

import java.util.List;
import java.util.Locale;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.his.patient.dto.TipoProcedenciaForm;
import br.com.his.patient.model.lookup.TipoProcedencia;
import br.com.his.patient.repository.TipoProcedenciaRepository;

@Service
public class TipoProcedenciaAdminService {

    private final TipoProcedenciaRepository repository;

    public TipoProcedenciaAdminService(TipoProcedenciaRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<TipoProcedencia> listar(String q) {
        String filtro = normalize(q);
        if (filtro == null) {
            return repository.findAllByOrderByDescricaoAsc();
        }
        return repository.buscarPorFiltro(filtro);
    }

    @Transactional(readOnly = true)
    public TipoProcedencia buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tipo de procedencia nao encontrado"));
    }

    @Transactional
    public TipoProcedencia criar(TipoProcedenciaForm form) {
        validarDuplicidade(form.getDescricao(), null);
        TipoProcedencia item = new TipoProcedencia();
        apply(item, form);
        return repository.save(item);
    }

    @Transactional
    public TipoProcedencia atualizar(Long id, TipoProcedenciaForm form) {
        validarDuplicidade(form.getDescricao(), id);
        TipoProcedencia item = buscar(id);
        apply(item, form);
        return repository.save(item);
    }

    @Transactional
    public void excluir(Long id) {
        TipoProcedencia item = buscar(id);
        try {
            repository.delete(item);
            repository.flush();
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("Nao foi possivel excluir: tipo de procedencia em uso");
        }
    }

    @Transactional(readOnly = true)
    public TipoProcedenciaForm toForm(TipoProcedencia item) {
        TipoProcedenciaForm form = new TipoProcedenciaForm();
        form.setDescricao(item.getDescricao());
        return form;
    }

    private void validarDuplicidade(String descricao, Long idIgnorar) {
        String normalized = normalizeUpper(descricao);
        repository.findByDescricaoIgnoreCase(normalized)
                .filter(existente -> idIgnorar == null || !existente.getId().equals(idIgnorar))
                .ifPresent(existente -> {
                    throw new IllegalArgumentException("Ja existe tipo de procedencia com esta descricao");
                });
    }

    private void apply(TipoProcedencia item, TipoProcedenciaForm form) {
        item.setDescricao(normalizeUpper(form.getDescricao()));
    }

    private static String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private static String normalizeUpper(String value) {
        String normalized = normalize(value);
        return normalized == null ? null : normalized.toUpperCase(Locale.ROOT);
    }
}
