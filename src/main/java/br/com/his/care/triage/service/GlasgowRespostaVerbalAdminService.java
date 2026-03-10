package br.com.his.care.triage.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.his.care.triage.dto.GlasgowRespostaVerbalForm;
import br.com.his.care.triage.model.GlasgowRespostaVerbal;
import br.com.his.care.triage.repository.GlasgowRespostaVerbalRepository;

@Service
public class GlasgowRespostaVerbalAdminService {

    private final GlasgowRespostaVerbalRepository repository;

    public GlasgowRespostaVerbalAdminService(GlasgowRespostaVerbalRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<GlasgowRespostaVerbal> listar(String q) {
        String filtro = normalize(q);
        if (filtro == null) {
            return repository.findAllByOrderByPontosDesc();
        }
        return repository.buscarPorFiltro(filtro);
    }

    @Transactional(readOnly = true)
    public List<GlasgowRespostaVerbal> listarAtivos() {
        return repository.findByAtivoTrueOrderByPontosDesc();
    }

    @Transactional(readOnly = true)
    public GlasgowRespostaVerbal buscar(Long id) {
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Item de resposta verbal nao encontrado"));
    }

    @Transactional
    public GlasgowRespostaVerbal criar(GlasgowRespostaVerbalForm form) {
        GlasgowRespostaVerbal item = new GlasgowRespostaVerbal();
        apply(item, form);
        return repository.save(item);
    }

    @Transactional
    public GlasgowRespostaVerbal atualizar(Long id, GlasgowRespostaVerbalForm form) {
        GlasgowRespostaVerbal item = buscar(id);
        apply(item, form);
        return repository.save(item);
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscar(id));
    }

    @Transactional(readOnly = true)
    public GlasgowRespostaVerbalForm toForm(GlasgowRespostaVerbal item) {
        GlasgowRespostaVerbalForm form = new GlasgowRespostaVerbalForm();
        form.setPontos(item.getPontos());
        form.setDescricao(item.getDescricao());
        form.setAtivo(item.isAtivo());
        return form;
    }

    private void apply(GlasgowRespostaVerbal item, GlasgowRespostaVerbalForm form) {
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
