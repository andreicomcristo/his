package br.com.his.care.attendance.service;

import java.util.List;
import java.util.Locale;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.his.care.attendance.dto.TipoAtendimentoAdminForm;
import br.com.his.care.attendance.model.TipoAtendimentoCadastro;
import br.com.his.care.attendance.repository.TipoAtendimentoCadastroRepository;
import br.com.his.care.attendance.repository.UnidadeTipoAtendimentoRepository;

@Service
public class TipoAtendimentoAdminService {

    private final TipoAtendimentoCadastroRepository repository;
    private final UnidadeTipoAtendimentoRepository unidadeTipoAtendimentoRepository;

    public TipoAtendimentoAdminService(TipoAtendimentoCadastroRepository repository,
                                       UnidadeTipoAtendimentoRepository unidadeTipoAtendimentoRepository) {
        this.repository = repository;
        this.unidadeTipoAtendimentoRepository = unidadeTipoAtendimentoRepository;
    }

    @Transactional(readOnly = true)
    public List<TipoAtendimentoCadastro> listar(String q) {
        String filtro = normalize(q);
        if (filtro == null) {
            return repository.findAllByOrderByOrdemExibicaoAscDescricaoAsc();
        }
        return repository.buscarPorFiltro(filtro);
    }

    @Transactional(readOnly = true)
    public TipoAtendimentoCadastro buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tipo de atendimento nao encontrado"));
    }

    @Transactional
    public TipoAtendimentoCadastro criar(TipoAtendimentoAdminForm form) {
        validarDuplicidade(form.getCodigo(), form.getDescricao(), null);
        TipoAtendimentoCadastro item = new TipoAtendimentoCadastro();
        apply(item, form);
        return repository.save(item);
    }

    @Transactional
    public TipoAtendimentoCadastro atualizar(Long id, TipoAtendimentoAdminForm form) {
        validarDuplicidade(form.getCodigo(), form.getDescricao(), id);
        TipoAtendimentoCadastro item = buscar(id);
        apply(item, form);
        return repository.save(item);
    }

    @Transactional
    public void excluir(Long id) {
        TipoAtendimentoCadastro item = buscar(id);
        if (unidadeTipoAtendimentoRepository.existsByTipoAtendimentoId(item.getId())) {
            throw new IllegalArgumentException("Nao foi possivel excluir: tipo vinculado a configuracoes de unidade");
        }
        try {
            repository.delete(item);
            repository.flush();
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("Nao foi possivel excluir: tipo em uso");
        }
    }

    @Transactional(readOnly = true)
    public TipoAtendimentoAdminForm toForm(TipoAtendimentoCadastro item) {
        TipoAtendimentoAdminForm form = new TipoAtendimentoAdminForm();
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
                    throw new IllegalArgumentException("Ja existe tipo de atendimento com este codigo");
                });
        repository.findDuplicadoDescricao(descricaoNormalizada, idIgnorar)
                .ifPresent(item -> {
                    throw new IllegalArgumentException("Ja existe tipo de atendimento com esta descricao");
                });
    }

    private void apply(TipoAtendimentoCadastro item, TipoAtendimentoAdminForm form) {
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
