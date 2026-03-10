package br.com.his.care.admission.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.his.care.admission.dto.MotivoEntradaForm;
import br.com.his.care.admission.model.MotivoEntrada;
import br.com.his.care.admission.repository.MotivoEntradaRepository;

@Service
public class MotivoEntradaAdminService {

    private final MotivoEntradaRepository repository;

    public MotivoEntradaAdminService(MotivoEntradaRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<MotivoEntrada> listar(String q) {
        String filtro = normalize(q);
        if (filtro == null) {
            return repository.findAllByOrderByDescricaoAsc();
        }
        return repository.buscarPorFiltro(filtro);
    }

    @Transactional(readOnly = true)
    public List<MotivoEntrada> listarAtivos() {
        return repository.findByAtivoTrueOrderByDescricaoAsc();
    }

    @Transactional(readOnly = true)
    public MotivoEntrada buscar(Long id) {
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Motivo da entrada nao encontrado"));
    }

    @Transactional
    public MotivoEntrada criar(MotivoEntradaForm form) {
        MotivoEntrada item = new MotivoEntrada();
        apply(item, form);
        return repository.save(item);
    }

    @Transactional
    public MotivoEntrada atualizar(Long id, MotivoEntradaForm form) {
        MotivoEntrada item = buscar(id);
        apply(item, form);
        return repository.save(item);
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscar(id));
    }

    @Transactional(readOnly = true)
    public MotivoEntradaForm toForm(MotivoEntrada item) {
        MotivoEntradaForm form = new MotivoEntradaForm();
        form.setDescricao(item.getDescricao());
        form.setAtivo(item.isAtivo());
        return form;
    }

    private void apply(MotivoEntrada item, MotivoEntradaForm form) {
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
