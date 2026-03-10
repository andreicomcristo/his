package br.com.his.admin.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.his.admin.dto.FormaChegadaForm;
import br.com.his.care.admission.model.FormaChegada;
import br.com.his.care.admission.model.PerfilChegada;
import br.com.his.care.admission.repository.FormaChegadaRepository;

@Service
public class FormaChegadaAdminService {

    private final FormaChegadaRepository repository;

    public FormaChegadaAdminService(FormaChegadaRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<FormaChegada> listar(String q) {
        String filtro = normalize(q);
        if (filtro == null) {
            return repository.findAllByOrderByDescricaoAsc();
        }
        return repository.buscarPorFiltro(filtro);
    }

    @Transactional(readOnly = true)
    public List<FormaChegada> listarAtivas() {
        return repository.findByAtivoTrueOrderByDescricaoAsc();
    }

    @Transactional(readOnly = true)
    public FormaChegada buscar(Long id) {
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Forma de chegada nao encontrada"));
    }

    @Transactional
    public FormaChegada criar(FormaChegadaForm form) {
        FormaChegada item = new FormaChegada();
        apply(item, form);
        return repository.save(item);
    }

    @Transactional
    public FormaChegada atualizar(Long id, FormaChegadaForm form) {
        FormaChegada item = buscar(id);
        apply(item, form);
        return repository.save(item);
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscar(id));
    }

    @Transactional(readOnly = true)
    public FormaChegadaForm toForm(FormaChegada item) {
        FormaChegadaForm form = new FormaChegadaForm();
        form.setDescricao(item.getDescricao());
        form.setPerfilChegada(item.getPerfilChegada());
        form.setAtivo(item.isAtivo());
        return form;
    }

    private void apply(FormaChegada item, FormaChegadaForm form) {
        item.setDescricao(normalizeUpper(form.getDescricao()));
        item.setPerfilChegada(form.getPerfilChegada() == null ? PerfilChegada.VERTICAL : form.getPerfilChegada());
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
