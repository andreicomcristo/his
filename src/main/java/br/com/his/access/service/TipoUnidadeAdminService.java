package br.com.his.access.service;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.his.access.dto.TipoUnidadeForm;
import br.com.his.access.model.TipoUnidade;
import br.com.his.access.repository.TipoUnidadeRepository;

@Service
public class TipoUnidadeAdminService {

    private final TipoUnidadeRepository repository;

    public TipoUnidadeAdminService(TipoUnidadeRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<TipoUnidade> listar(String q, Boolean ativo) {
        String filtro = normalize(q);
        if (filtro == null) {
            return ativo == null
                    ? repository.findAllByOrderByDescricaoAsc()
                    : repository.findByAtivoOrderByDescricaoAsc(ativo);
        }
        return ativo == null
                ? repository.listarPorBusca(filtro)
                : repository.listarPorFiltroComBusca(ativo, filtro);
    }

    @Transactional(readOnly = true)
    public TipoUnidade buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tipo de unidade nao encontrado"));
    }

    @Transactional
    public TipoUnidade criar(TipoUnidadeForm form) {
        validarCodigoDuplicado(form.getCodigo(), null);
        TipoUnidade item = new TipoUnidade();
        apply(item, form);
        return repository.save(item);
    }

    @Transactional
    public TipoUnidade atualizar(Long id, TipoUnidadeForm form) {
        validarCodigoDuplicado(form.getCodigo(), id);
        TipoUnidade item = buscar(id);
        apply(item, form);
        return repository.save(item);
    }

    @Transactional
    public void excluir(Long id) {
        TipoUnidade item = buscar(id);
        try {
            repository.delete(item);
            repository.flush();
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("Nao foi possivel excluir: tipo de unidade em uso");
        }
    }

    @Transactional(readOnly = true)
    public TipoUnidadeForm toForm(TipoUnidade item) {
        TipoUnidadeForm form = new TipoUnidadeForm();
        form.setCodigo(item.getCodigo());
        form.setDescricao(item.getDescricao());
        form.setAtivo(item.isAtivo());
        return form;
    }

    private void validarCodigoDuplicado(String codigo, Long idIgnorar) {
        String normalized = normalizeUpper(codigo);
        repository.findByCodigoIgnoreCase(normalized)
                .filter(existente -> idIgnorar == null || !existente.getId().equals(idIgnorar))
                .ifPresent(existente -> {
                    throw new IllegalArgumentException("Ja existe tipo de unidade com este codigo");
                });
    }

    private void apply(TipoUnidade item, TipoUnidadeForm form) {
        item.setCodigo(normalizeUpper(form.getCodigo()));
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
