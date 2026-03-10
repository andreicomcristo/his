package br.com.his.admin.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.his.admin.dto.SexoForm;
import br.com.his.patient.model.lookup.Sexo;
import br.com.his.patient.repository.SexoRepository;

@Service
public class SexoAdminService {

    private final SexoRepository repository;

    public SexoAdminService(SexoRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Sexo> listar(String q) {
        String filtro = normalize(q);
        if (filtro == null) {
            return repository.findAllByOrderByDescricaoAsc();
        }
        return repository.buscarPorFiltro(filtro);
    }

    @Transactional(readOnly = true)
    public Sexo buscar(Long id) {
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Sexo nao encontrado"));
    }

    @Transactional
    public Sexo criar(SexoForm form) {
        validarNovo(form);
        Sexo item = new Sexo();
        apply(item, form);
        return repository.save(item);
    }

    @Transactional
    public Sexo atualizar(Long id, SexoForm form) {
        Sexo item = buscar(id);
        validarAtualizacao(id, form);
        apply(item, form);
        return repository.save(item);
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscar(id));
    }

    @Transactional(readOnly = true)
    public SexoForm toForm(Sexo item) {
        SexoForm form = new SexoForm();
        form.setCodigo(item.getCodigo());
        form.setDescricao(item.getDescricao());
        form.setAtivo(item.isAtivo());
        return form;
    }

    private void apply(Sexo item, SexoForm form) {
        item.setCodigo(normalizeUpper(form.getCodigo()));
        item.setDescricao(normalizeUpper(form.getDescricao()));
        item.setAtivo(form.isAtivo());
    }

    private void validarNovo(SexoForm form) {
        String codigo = normalizeUpper(form.getCodigo());
        String descricao = normalizeUpper(form.getDescricao());
        repository.findByCodigoIgnoreCase(codigo)
                .ifPresent(item -> {
                    throw new IllegalArgumentException("Ja existe sexo com esse codigo");
                });
        repository.findByDescricaoIgnoreCase(descricao)
                .ifPresent(item -> {
                    throw new IllegalArgumentException("Ja existe sexo com essa descricao");
                });
    }

    private void validarAtualizacao(Long id, SexoForm form) {
        String codigo = normalizeUpper(form.getCodigo());
        String descricao = normalizeUpper(form.getDescricao());
        repository.findByCodigoIgnoreCase(codigo)
                .filter(item -> !item.getId().equals(id))
                .ifPresent(item -> {
                    throw new IllegalArgumentException("Ja existe sexo com esse codigo");
                });
        repository.findByDescricaoIgnoreCase(descricao)
                .filter(item -> !item.getId().equals(id))
                .ifPresent(item -> {
                    throw new IllegalArgumentException("Ja existe sexo com essa descricao");
                });
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
