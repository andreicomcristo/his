package br.com.his.admin.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.his.admin.dto.GlasgowRespostaPupilarForm;
import br.com.his.care.triage.model.GlasgowRespostaPupilar;
import br.com.his.care.triage.repository.GlasgowRespostaPupilarRepository;

@Service
public class GlasgowRespostaPupilarAdminService {

    private final GlasgowRespostaPupilarRepository repository;

    public GlasgowRespostaPupilarAdminService(GlasgowRespostaPupilarRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<GlasgowRespostaPupilar> listar(String q) {
        String filtro = normalize(q);
        if (filtro == null) {
            return repository.findAllByOrderByPontosAsc();
        }
        return repository.buscarPorFiltro(filtro);
    }

    @Transactional(readOnly = true)
    public List<GlasgowRespostaPupilar> listarAtivos() {
        return repository.findByAtivoTrueOrderByPontosAsc();
    }

    @Transactional(readOnly = true)
    public GlasgowRespostaPupilar buscar(Long id) {
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Item de resposta pupilar nao encontrado"));
    }

    @Transactional
    public GlasgowRespostaPupilar criar(GlasgowRespostaPupilarForm form) {
        GlasgowRespostaPupilar item = new GlasgowRespostaPupilar();
        apply(item, form);
        return repository.save(item);
    }

    @Transactional
    public GlasgowRespostaPupilar atualizar(Long id, GlasgowRespostaPupilarForm form) {
        GlasgowRespostaPupilar item = buscar(id);
        apply(item, form);
        return repository.save(item);
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscar(id));
    }

    @Transactional(readOnly = true)
    public GlasgowRespostaPupilarForm toForm(GlasgowRespostaPupilar item) {
        GlasgowRespostaPupilarForm form = new GlasgowRespostaPupilarForm();
        form.setPontos(item.getPontos());
        form.setDescricao(item.getDescricao());
        form.setAtivo(item.isAtivo());
        return form;
    }

    private void apply(GlasgowRespostaPupilar item, GlasgowRespostaPupilarForm form) {
        item.setPontos(form.getPontos());
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
