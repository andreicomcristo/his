package br.com.his.access.service;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.his.access.dto.TipoVinculoTrabalhistaForm;
import br.com.his.access.model.TipoVinculoTrabalhista;
import br.com.his.access.repository.TipoVinculoTrabalhistaRepository;

@Service
public class TipoVinculoTrabalhistaAdminService {

    private final TipoVinculoTrabalhistaRepository repository;

    public TipoVinculoTrabalhistaAdminService(TipoVinculoTrabalhistaRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<TipoVinculoTrabalhista> listar(String q, Boolean ativo) {
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
    public TipoVinculoTrabalhista buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tipo de vinculo trabalhista nao encontrado"));
    }

    @Transactional
    public TipoVinculoTrabalhista criar(TipoVinculoTrabalhistaForm form) {
        validarCodigoDuplicado(form.getCodigo(), null);
        TipoVinculoTrabalhista item = new TipoVinculoTrabalhista();
        apply(item, form);
        return repository.save(item);
    }

    @Transactional
    public TipoVinculoTrabalhista atualizar(Long id, TipoVinculoTrabalhistaForm form) {
        validarCodigoDuplicado(form.getCodigo(), id);
        TipoVinculoTrabalhista item = buscar(id);
        apply(item, form);
        return repository.save(item);
    }

    @Transactional
    public void excluir(Long id) {
        TipoVinculoTrabalhista item = buscar(id);
        try {
            repository.delete(item);
            repository.flush();
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("Nao foi possivel excluir: tipo de vinculo em uso");
        }
    }

    @Transactional(readOnly = true)
    public TipoVinculoTrabalhistaForm toForm(TipoVinculoTrabalhista item) {
        TipoVinculoTrabalhistaForm form = new TipoVinculoTrabalhistaForm();
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
                    throw new IllegalArgumentException("Ja existe tipo de vinculo com este codigo");
                });
    }

    private void apply(TipoVinculoTrabalhista item, TipoVinculoTrabalhistaForm form) {
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
