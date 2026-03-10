package br.com.his.care.attendance.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.his.care.attendance.dto.StatusAtendimentoForm;
import br.com.his.care.attendance.model.StatusAtendimento;
import br.com.his.care.attendance.repository.StatusAtendimentoRepository;

@Service
public class StatusAtendimentoAdminService {

    private final StatusAtendimentoRepository repository;

    public StatusAtendimentoAdminService(StatusAtendimentoRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<StatusAtendimento> listar(String q) {
        String filtro = normalize(q);
        if (filtro == null) {
            return repository.findAllByOrderByDescricaoAsc();
        }
        return repository.buscarPorFiltro(filtro);
    }

    @Transactional(readOnly = true)
    public StatusAtendimento buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Status de atendimento nao encontrado"));
    }

    @Transactional
    public StatusAtendimento criar(StatusAtendimentoForm form) {
        StatusAtendimento item = new StatusAtendimento();
        apply(item, form);
        return repository.save(item);
    }

    @Transactional
    public StatusAtendimento atualizar(Long id, StatusAtendimentoForm form) {
        StatusAtendimento item = buscar(id);
        apply(item, form);
        return repository.save(item);
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscar(id));
    }

    @Transactional(readOnly = true)
    public StatusAtendimentoForm toForm(StatusAtendimento item) {
        StatusAtendimentoForm form = new StatusAtendimentoForm();
        form.setCodigo(item.getCodigo());
        form.setDescricao(item.getDescricao());
        form.setCor(item.getCor());
        form.setAtivo(item.isAtivo());
        return form;
    }

    private void apply(StatusAtendimento item, StatusAtendimentoForm form) {
        item.setCodigo(normalizeUpper(form.getCodigo()));
        item.setDescricao(normalizeUpper(form.getDescricao()));
        item.setCor(normalizeUpperHex(form.getCor()));
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

    private static String normalizeUpperHex(String value) {
        String normalized = normalize(value);
        return normalized == null ? null : normalized.toUpperCase();
    }
}
