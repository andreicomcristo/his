package br.com.his.access.service;

import java.time.LocalDateTime;
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
    private final UsuarioAuditoriaService usuarioAuditoriaService;

    public TipoUnidadeAdminService(TipoUnidadeRepository repository,
                                   UsuarioAuditoriaService usuarioAuditoriaService) {
        this.repository = repository;
        this.usuarioAuditoriaService = usuarioAuditoriaService;
    }

    @Transactional(readOnly = true)
    public List<TipoUnidade> listar(String q) {
        String filtro = normalize(q);
        return filtro == null
                ? repository.findByDtCancelamentoIsNullOrderByDescricaoAsc()
                : repository.listarAtivosPorBusca(filtro);
    }

    @Transactional(readOnly = true)
    public List<TipoUnidade> listarCancelados(String q) {
        String filtro = normalize(q);
        return filtro == null
                ? repository.listarCancelados()
                : repository.listarCanceladosPorBusca(filtro);
    }

    @Transactional(readOnly = true)
    public TipoUnidade buscarAtivo(Long id) {
        return repository.findByIdAndDtCancelamentoIsNull(id)
                .orElseThrow(() -> new IllegalArgumentException("Tipo de unidade nao encontrado"));
    }

    @Transactional(readOnly = true)
    public TipoUnidade buscarCancelado(Long id) {
        return repository.findByIdAndDtCancelamentoIsNotNull(id)
                .orElseThrow(() -> new IllegalArgumentException("Tipo de unidade cancelado nao encontrado"));
    }

    @Transactional(readOnly = true)
    public TipoUnidade buscarParaVinculoAtivo(Long id) {
        return repository.findByIdAndDtCancelamentoIsNull(id)
                .orElseThrow(() -> new IllegalArgumentException("Tipo de unidade invalido"));
    }

    @Transactional
    public TipoUnidade criar(TipoUnidadeForm form) {
        LocalDateTime now = LocalDateTime.now();
        Long usuarioAtualId = currentUserId();
        validarCodigoDuplicado(form.getCodigo(), null);
        TipoUnidade item = new TipoUnidade();
        item.setDtCadastro(now);
        item.setDtAtualizacao(now);
        item.setCadastroUserId(usuarioAtualId);
        item.setAtualizacaoUserId(usuarioAtualId);
        apply(item, form);
        item.setDtCancelamento(null);
        item.setCancelamentoUserId(null);
        return repository.save(item);
    }

    @Transactional
    public TipoUnidade atualizar(Long id, TipoUnidadeForm form) {
        LocalDateTime now = LocalDateTime.now();
        Long usuarioAtualId = currentUserId();
        validarCodigoDuplicado(form.getCodigo(), id);
        TipoUnidade item = buscarAtivo(id);
        apply(item, form);
        item.setDtAtualizacao(now);
        item.setAtualizacaoUserId(usuarioAtualId);
        return repository.save(item);
    }

    @Transactional
    public void excluir(Long id) {
        LocalDateTime now = LocalDateTime.now();
        Long usuarioAtualId = currentUserId();
        TipoUnidade item = buscarAtivo(id);
        item.setDtCancelamento(now);
        item.setCancelamentoUserId(usuarioAtualId);
        item.setDtAtualizacao(now);
        item.setAtualizacaoUserId(usuarioAtualId);
        repository.save(item);
    }

    @Transactional
    public void restaurar(Long id) {
        LocalDateTime now = LocalDateTime.now();
        Long usuarioAtualId = currentUserId();
        TipoUnidade item = buscarCancelado(id);
        item.setDtCancelamento(null);
        item.setCancelamentoUserId(null);
        item.setDtAtualizacao(now);
        item.setAtualizacaoUserId(usuarioAtualId);
        repository.save(item);
    }

    @Transactional
    public void excluirPermanente(Long id) {
        TipoUnidade item = buscarCancelado(id);
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

    private Long currentUserId() {
        return usuarioAuditoriaService.usuarioAtual()
                .map(usuario -> usuario.getId())
                .orElse(null);
    }
}
