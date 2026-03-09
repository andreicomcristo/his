package br.com.his.admin.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.his.admin.dto.AvcSinalAlertaForm;
import br.com.his.assistencial.model.AvcSinalAlerta;
import br.com.his.assistencial.repository.AvcSinalAlertaRepository;

@Service
public class AvcSinalAlertaAdminService {

    private final AvcSinalAlertaRepository repository;

    public AvcSinalAlertaAdminService(AvcSinalAlertaRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<AvcSinalAlerta> listar(String q) {
        String filtro = normalize(q);
        if (filtro == null) {
            return repository.findAllByOrderByIdAsc();
        }
        return repository.buscarPorFiltro(filtro);
    }

    @Transactional(readOnly = true)
    public List<AvcSinalAlerta> listarAtivos() {
        return repository.findByAtivoTrueOrderByIdAsc();
    }

    @Transactional(readOnly = true)
    public AvcSinalAlerta buscar(Long id) {
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Sinal de alerta de AVC nao encontrado"));
    }

    @Transactional
    public AvcSinalAlerta criar(AvcSinalAlertaForm form) {
        AvcSinalAlerta item = new AvcSinalAlerta();
        apply(item, form);
        return repository.save(item);
    }

    @Transactional
    public AvcSinalAlerta atualizar(Long id, AvcSinalAlertaForm form) {
        AvcSinalAlerta item = buscar(id);
        apply(item, form);
        return repository.save(item);
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscar(id));
    }

    @Transactional(readOnly = true)
    public AvcSinalAlertaForm toForm(AvcSinalAlerta item) {
        AvcSinalAlertaForm form = new AvcSinalAlertaForm();
        form.setDescricao(item.getDescricao());
        form.setOrdemExibicao(item.getOrdemExibicao());
        form.setAtivo(item.isAtivo());
        return form;
    }

    private void apply(AvcSinalAlerta item, AvcSinalAlertaForm form) {
        String descricao = normalizeUpper(form.getDescricao());
        item.setDescricao(descricao);
        item.setCodigo(resolveCodigo(item.getId(), descricao));
        item.setOrdemExibicao(form.getOrdemExibicao() == null ? 0 : form.getOrdemExibicao());
        item.setAtivo(form.isAtivo());
    }

    private String resolveCodigo(Long id, String descricao) {
        String base = toCodigoBase(descricao);
        String codigo = base;
        int suffix = 2;
        while (codigoEmUso(id, codigo)) {
            codigo = base + "_" + suffix;
            suffix++;
        }
        return codigo;
    }

    private boolean codigoEmUso(Long id, String codigo) {
        if (id == null) {
            return repository.existsByCodigoIgnoreCase(codigo);
        }
        return repository.existsByCodigoIgnoreCaseAndIdNot(codigo, id);
    }

    private static String toCodigoBase(String descricao) {
        String base = descricao == null ? "" : java.text.Normalizer.normalize(descricao, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replaceAll("[^A-Za-z0-9]+", "_")
                .replaceAll("_+", "_")
                .replaceAll("^_|_$", "")
                .toUpperCase();
        return base.isBlank() ? "SINAL_AVC" : base;
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
