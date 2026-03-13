package br.com.his.access.service;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.his.access.dto.TipoCargoForm;
import br.com.his.access.model.TipoCargo;
import br.com.his.access.repository.TipoCargoRepository;

@Service
public class TipoCargoAdminService {

    private final TipoCargoRepository repository;

    public TipoCargoAdminService(TipoCargoRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<TipoCargo> listar(String q, Boolean ativo) {
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
    public TipoCargo buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tipo de cargo nao encontrado"));
    }

    @Transactional
    public TipoCargo criar(TipoCargoForm form) {
        validarCodigoDuplicado(form.getCodigo(), null);
        TipoCargo item = new TipoCargo();
        apply(item, form);
        return repository.save(item);
    }

    @Transactional
    public TipoCargo atualizar(Long id, TipoCargoForm form) {
        validarCodigoDuplicado(form.getCodigo(), id);
        TipoCargo item = buscar(id);
        apply(item, form);
        return repository.save(item);
    }

    @Transactional
    public void excluir(Long id) {
        TipoCargo item = buscar(id);
        try {
            repository.delete(item);
            repository.flush();
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("Nao foi possivel excluir: tipo de cargo em uso");
        }
    }

    @Transactional(readOnly = true)
    public TipoCargoForm toForm(TipoCargo item) {
        TipoCargoForm form = new TipoCargoForm();
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
                    throw new IllegalArgumentException("Ja existe tipo de cargo com este codigo");
                });
    }

    private void apply(TipoCargo item, TipoCargoForm form) {
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
