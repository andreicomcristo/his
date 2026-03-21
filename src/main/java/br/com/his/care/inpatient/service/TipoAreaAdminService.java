package br.com.his.care.inpatient.service;

import java.util.List;
import java.util.Locale;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.his.care.inpatient.dto.TipoAreaForm;
import br.com.his.care.inpatient.model.TipoArea;
import br.com.his.care.inpatient.repository.AreaRepository;
import br.com.his.care.inpatient.repository.TipoAreaRepository;

@Service
public class TipoAreaAdminService {

    private final TipoAreaRepository repository;
    private final AreaRepository areaRepository;

    public TipoAreaAdminService(TipoAreaRepository repository,
                                AreaRepository areaRepository) {
        this.repository = repository;
        this.areaRepository = areaRepository;
    }

    @Transactional(readOnly = true)
    public List<TipoArea> listar(String q) {
        String filtro = normalize(q);
        if (filtro == null) {
            return repository.findAllByOrderByOrdemExibicaoAscDescricaoAsc();
        }
        return repository.buscarPorFiltro(filtro);
    }

    @Transactional(readOnly = true)
    public List<TipoArea> listarAtivos() {
        return repository.findByAtivoTrueOrderByOrdemExibicaoAscDescricaoAsc();
    }

    @Transactional(readOnly = true)
    public TipoArea buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tipo de area nao encontrado"));
    }

    @Transactional(readOnly = true)
    public TipoArea buscarAtivo(Long id) {
        return repository.findById(id)
                .filter(TipoArea::isAtivo)
                .orElseThrow(() -> new IllegalArgumentException("Tipo de area inativo ou inexistente"));
    }

    @Transactional
    public TipoArea criar(TipoAreaForm form) {
        validarDuplicidade(form.getCodigo(), form.getDescricao(), null);
        TipoArea item = new TipoArea();
        apply(item, form);
        return repository.save(item);
    }

    @Transactional
    public TipoArea atualizar(Long id, TipoAreaForm form) {
        validarDuplicidade(form.getCodigo(), form.getDescricao(), id);
        if (!form.isAtivo() && areaRepository.existsByTipoAreaId(id)) {
            throw new IllegalArgumentException("Nao foi possivel inativar: tipo de area vinculado a areas");
        }
        TipoArea item = buscar(id);
        apply(item, form);
        return repository.save(item);
    }

    @Transactional
    public void excluir(Long id) {
        TipoArea item = buscar(id);
        if (areaRepository.existsByTipoAreaId(item.getId())) {
            throw new IllegalArgumentException("Nao foi possivel excluir: tipo de area vinculado a areas");
        }
        try {
            repository.delete(item);
            repository.flush();
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("Nao foi possivel excluir: tipo de area em uso");
        }
    }

    @Transactional(readOnly = true)
    public TipoAreaForm toForm(TipoArea item) {
        TipoAreaForm form = new TipoAreaForm();
        form.setCodigo(item.getCodigo());
        form.setDescricao(item.getDescricao());
        form.setOrdemExibicao(item.getOrdemExibicao());
        form.setAtivo(item.isAtivo());
        return form;
    }

    private void validarDuplicidade(String codigo, String descricao, Long idIgnorar) {
        String codigoNormalizado = normalizeUpper(codigo);
        String descricaoNormalizada = normalizeUpper(descricao);
        repository.findDuplicadoCodigo(codigoNormalizado, idIgnorar)
                .ifPresent(item -> {
                    throw new IllegalArgumentException("Ja existe tipo de area com este codigo");
                });
        repository.findDuplicadoDescricao(descricaoNormalizada, idIgnorar)
                .ifPresent(item -> {
                    throw new IllegalArgumentException("Ja existe tipo de area com esta descricao");
                });
    }

    private void apply(TipoArea item, TipoAreaForm form) {
        item.setCodigo(normalizeUpper(form.getCodigo()));
        item.setDescricao(normalizeUpper(form.getDescricao()));
        item.setOrdemExibicao(form.getOrdemExibicao() == null ? 100 : form.getOrdemExibicao());
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
        return normalized == null ? null : normalized.toUpperCase(Locale.ROOT);
    }
}
