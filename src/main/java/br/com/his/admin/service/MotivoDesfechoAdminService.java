package br.com.his.admin.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.his.admin.dto.MotivoDesfechoForm;
import br.com.his.assistencial.model.MotivoDesfecho;
import br.com.his.assistencial.repository.MotivoDesfechoRepository;

@Service
public class MotivoDesfechoAdminService {

    private final MotivoDesfechoRepository repository;

    public MotivoDesfechoAdminService(MotivoDesfechoRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<MotivoDesfecho> listar(String q) {
        String filtro = normalize(q);
        if (filtro == null) {
            return repository.findAllByOrderByDescricaoAsc();
        }
        return repository.buscarPorFiltro(filtro);
    }

    @Transactional(readOnly = true)
    public List<MotivoDesfecho> listarAtivos() {
        return repository.findByAtivoTrueOrderByDescricaoAsc();
    }

    @Transactional(readOnly = true)
    public MotivoDesfecho buscar(Long id) {
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Motivo de desfecho nao encontrado"));
    }

    @Transactional
    public MotivoDesfecho criar(MotivoDesfechoForm form) {
        MotivoDesfecho item = new MotivoDesfecho();
        apply(item, form);
        return repository.save(item);
    }

    @Transactional
    public MotivoDesfecho atualizar(Long id, MotivoDesfechoForm form) {
        MotivoDesfecho item = buscar(id);
        apply(item, form);
        return repository.save(item);
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscar(id));
    }

    @Transactional(readOnly = true)
    public MotivoDesfechoForm toForm(MotivoDesfecho item) {
        MotivoDesfechoForm form = new MotivoDesfechoForm();
        form.setDescricao(item.getDescricao());
        form.setCor(item.getCor());
        form.setAtivo(item.isAtivo());
        return form;
    }

    private void apply(MotivoDesfecho item, MotivoDesfechoForm form) {
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
