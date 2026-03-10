package br.com.his.admin.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.his.admin.dto.GlasgowAberturaOcularForm;
import br.com.his.care.triage.model.GlasgowAberturaOcular;
import br.com.his.care.triage.repository.GlasgowAberturaOcularRepository;

@Service
public class GlasgowAberturaOcularAdminService {

    private final GlasgowAberturaOcularRepository repository;

    public GlasgowAberturaOcularAdminService(GlasgowAberturaOcularRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<GlasgowAberturaOcular> listar(String q) {
        String filtro = normalize(q);
        if (filtro == null) {
            return repository.findAllByOrderByPontosDesc();
        }
        return repository.buscarPorFiltro(filtro);
    }

    @Transactional(readOnly = true)
    public List<GlasgowAberturaOcular> listarAtivos() {
        return repository.findByAtivoTrueOrderByPontosDesc();
    }

    @Transactional(readOnly = true)
    public GlasgowAberturaOcular buscar(Long id) {
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Item de abertura ocular nao encontrado"));
    }

    @Transactional
    public GlasgowAberturaOcular criar(GlasgowAberturaOcularForm form) {
        GlasgowAberturaOcular item = new GlasgowAberturaOcular();
        apply(item, form);
        return repository.save(item);
    }

    @Transactional
    public GlasgowAberturaOcular atualizar(Long id, GlasgowAberturaOcularForm form) {
        GlasgowAberturaOcular item = buscar(id);
        apply(item, form);
        return repository.save(item);
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscar(id));
    }

    @Transactional(readOnly = true)
    public GlasgowAberturaOcularForm toForm(GlasgowAberturaOcular item) {
        GlasgowAberturaOcularForm form = new GlasgowAberturaOcularForm();
        form.setPontos(item.getPontos());
        form.setDescricao(item.getDescricao());
        form.setAtivo(item.isAtivo());
        return form;
    }

    private void apply(GlasgowAberturaOcular item, GlasgowAberturaOcularForm form) {
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
