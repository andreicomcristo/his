package br.com.his.care.attendance.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.his.care.attendance.dto.TipoDesfechoForm;
import br.com.his.care.attendance.model.TipoDesfecho;
import br.com.his.care.attendance.repository.TipoDesfechoRepository;

@Service
public class TipoDesfechoAdminService {

    private final TipoDesfechoRepository repository;

    public TipoDesfechoAdminService(TipoDesfechoRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<TipoDesfecho> listar(String q) {
        String filtro = normalize(q);
        if (filtro == null) {
            return repository.findAllByOrderByDescricaoAsc();
        }
        return repository.buscarPorFiltro(filtro);
    }

    @Transactional(readOnly = true)
    public List<TipoDesfecho> listarAtivos() {
        return repository.findByAtivoTrueOrderByDescricaoAsc();
    }

    @Transactional(readOnly = true)
    public TipoDesfecho buscar(Long id) {
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Tipo de desfecho nao encontrado"));
    }

    @Transactional
    public TipoDesfecho criar(TipoDesfechoForm form) {
        TipoDesfecho item = new TipoDesfecho();
        apply(item, form);
        return repository.save(item);
    }

    @Transactional
    public TipoDesfecho atualizar(Long id, TipoDesfechoForm form) {
        TipoDesfecho item = buscar(id);
        apply(item, form);
        return repository.save(item);
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscar(id));
    }

    @Transactional(readOnly = true)
    public TipoDesfechoForm toForm(TipoDesfecho item) {
        TipoDesfechoForm form = new TipoDesfechoForm();
        form.setDescricao(item.getDescricao());
        form.setAtivo(item.isAtivo());
        return form;
    }

    private void apply(TipoDesfecho item, TipoDesfechoForm form) {
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
