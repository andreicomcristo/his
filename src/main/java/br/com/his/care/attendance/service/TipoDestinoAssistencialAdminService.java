package br.com.his.care.attendance.service;

import java.util.List;
import java.util.Locale;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.his.care.attendance.dto.TipoDestinoAssistencialForm;
import br.com.his.care.attendance.model.TipoDestinoAssistencial;
import br.com.his.care.attendance.repository.DestinoAssistencialRepository;
import br.com.his.care.attendance.repository.TipoDestinoAssistencialRepository;

@Service
public class TipoDestinoAssistencialAdminService {

    private final TipoDestinoAssistencialRepository repository;
    private final DestinoAssistencialRepository destinoAssistencialRepository;

    public TipoDestinoAssistencialAdminService(TipoDestinoAssistencialRepository repository,
                                               DestinoAssistencialRepository destinoAssistencialRepository) {
        this.repository = repository;
        this.destinoAssistencialRepository = destinoAssistencialRepository;
    }

    @Transactional(readOnly = true)
    public List<TipoDestinoAssistencial> listar(String q) {
        String filtro = normalize(q);
        if (filtro == null) {
            return repository.findAllByOrderByOrdemExibicaoAscDescricaoAsc();
        }
        return repository.buscarPorFiltro(filtro);
    }

    @Transactional(readOnly = true)
    public List<TipoDestinoAssistencial> listarAtivos() {
        return repository.findByAtivoTrueOrderByOrdemExibicaoAscDescricaoAsc();
    }

    @Transactional(readOnly = true)
    public TipoDestinoAssistencial buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tipo de destino assistencial nao encontrado"));
    }

    @Transactional(readOnly = true)
    public TipoDestinoAssistencial buscarAtivo(Long id) {
        return repository.findById(id)
                .filter(TipoDestinoAssistencial::isAtivo)
                .orElseThrow(() -> new IllegalArgumentException("Tipo de destino assistencial invalido"));
    }

    @Transactional
    public TipoDestinoAssistencial criar(TipoDestinoAssistencialForm form) {
        validarDuplicidade(form.getCodigo(), form.getDescricao(), null);
        TipoDestinoAssistencial item = new TipoDestinoAssistencial();
        apply(item, form);
        return repository.save(item);
    }

    @Transactional
    public TipoDestinoAssistencial atualizar(Long id, TipoDestinoAssistencialForm form) {
        validarDuplicidade(form.getCodigo(), form.getDescricao(), id);
        if (!form.isAtivo() && destinoAssistencialRepository.existsByTipoDestinoAssistencialId(id)) {
            throw new IllegalArgumentException("Nao foi possivel inativar: tipo vinculado a destinos assistenciais");
        }
        TipoDestinoAssistencial item = buscar(id);
        apply(item, form);
        return repository.save(item);
    }

    @Transactional
    public void excluir(Long id) {
        TipoDestinoAssistencial item = buscar(id);
        if (destinoAssistencialRepository.existsByTipoDestinoAssistencialId(item.getId())) {
            throw new IllegalArgumentException("Nao foi possivel excluir: tipo vinculado a destinos assistenciais");
        }
        try {
            repository.delete(item);
            repository.flush();
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("Nao foi possivel excluir: tipo de destino em uso");
        }
    }

    @Transactional(readOnly = true)
    public TipoDestinoAssistencialForm toForm(TipoDestinoAssistencial item) {
        TipoDestinoAssistencialForm form = new TipoDestinoAssistencialForm();
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
                    throw new IllegalArgumentException("Ja existe tipo de destino com este codigo");
                });
        repository.findDuplicadoDescricao(descricaoNormalizada, idIgnorar)
                .ifPresent(item -> {
                    throw new IllegalArgumentException("Ja existe tipo de destino com esta descricao");
                });
    }

    private void apply(TipoDestinoAssistencial item, TipoDestinoAssistencialForm form) {
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
