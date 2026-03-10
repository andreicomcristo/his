package br.com.his.admin.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.his.admin.dto.DestinoRedeForm;
import br.com.his.care.attendance.model.DestinoRede;
import br.com.his.care.attendance.repository.DestinoRedeRepository;

@Service
public class DestinoRedeAdminService {

    private final DestinoRedeRepository repository;

    public DestinoRedeAdminService(DestinoRedeRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<DestinoRede> listar(String q) {
        String filtro = normalize(q);
        if (filtro == null) {
            return repository.findAllByOrderByDescricaoAsc();
        }
        return repository.buscarPorFiltro(filtro);
    }

    @Transactional(readOnly = true)
    public List<DestinoRede> listarAtivos() {
        return repository.findByAtivoTrueOrderByDescricaoAsc();
    }

    @Transactional(readOnly = true)
    public DestinoRede buscar(Long id) {
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Destino de rede nao encontrado"));
    }

    @Transactional
    public DestinoRede criar(DestinoRedeForm form) {
        DestinoRede item = new DestinoRede();
        apply(item, form);
        return repository.save(item);
    }

    @Transactional
    public DestinoRede atualizar(Long id, DestinoRedeForm form) {
        DestinoRede item = buscar(id);
        apply(item, form);
        return repository.save(item);
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscar(id));
    }

    @Transactional(readOnly = true)
    public DestinoRedeForm toForm(DestinoRede item) {
        DestinoRedeForm form = new DestinoRedeForm();
        form.setDescricao(item.getDescricao());
        form.setAtivo(item.isAtivo());
        return form;
    }

    private void apply(DestinoRede item, DestinoRedeForm form) {
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
