package br.com.his.access.service;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.his.access.dto.FuncaoUnidadeForm;
import br.com.his.access.model.FuncaoUnidade;
import br.com.his.access.repository.FuncaoUnidadeRepository;

@Service
public class FuncaoUnidadeAdminService {

    private final FuncaoUnidadeRepository repository;

    public FuncaoUnidadeAdminService(FuncaoUnidadeRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<FuncaoUnidade> listar(String q, Boolean ativo) {
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
    public FuncaoUnidade buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Funcao da unidade nao encontrada"));
    }

    @Transactional
    public FuncaoUnidade criar(FuncaoUnidadeForm form) {
        validarCodigoDuplicado(form.getCodigo(), null);
        FuncaoUnidade item = new FuncaoUnidade();
        apply(item, form);
        return repository.save(item);
    }

    @Transactional
    public FuncaoUnidade atualizar(Long id, FuncaoUnidadeForm form) {
        validarCodigoDuplicado(form.getCodigo(), id);
        FuncaoUnidade item = buscar(id);
        apply(item, form);
        return repository.save(item);
    }

    @Transactional
    public void excluir(Long id) {
        FuncaoUnidade item = buscar(id);
        try {
            repository.delete(item);
            repository.flush();
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("Nao foi possivel excluir: funcao em uso por atuacao");
        }
    }

    @Transactional(readOnly = true)
    public FuncaoUnidadeForm toForm(FuncaoUnidade item) {
        FuncaoUnidadeForm form = new FuncaoUnidadeForm();
        form.setCodigo(item.getCodigo());
        form.setDescricao(item.getDescricao());
        form.setTipoFuncao(item.getTipoFuncao());
        form.setRequerEspecialidade(item.isRequerEspecialidade());
        form.setAtivo(item.isAtivo());
        return form;
    }

    private void validarCodigoDuplicado(String codigo, Long idIgnorar) {
        String normalized = normalizeUpper(codigo);
        repository.findByCodigoIgnoreCase(normalized)
                .filter(existente -> idIgnorar == null || !existente.getId().equals(idIgnorar))
                .ifPresent(existente -> {
                    throw new IllegalArgumentException("Ja existe funcao com este codigo");
                });
    }

    private void apply(FuncaoUnidade item, FuncaoUnidadeForm form) {
        item.setCodigo(normalizeUpper(form.getCodigo()));
        item.setDescricao(normalizeUpper(form.getDescricao()));
        item.setTipoFuncao(form.getTipoFuncao());
        item.setRequerEspecialidade(form.isRequerEspecialidade());
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
