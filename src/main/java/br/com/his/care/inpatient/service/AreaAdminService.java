package br.com.his.care.inpatient.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.his.access.model.Unidade;
import br.com.his.access.repository.UnidadeRepository;
import br.com.his.access.service.UsuarioAuditoriaService;
import br.com.his.care.inpatient.dto.AreaForm;
import br.com.his.care.inpatient.model.Area;
import br.com.his.care.inpatient.repository.AreaRepository;

@Service
public class AreaAdminService {

    private final AreaRepository repository;
    private final UnidadeRepository unidadeRepository;
    private final UsuarioAuditoriaService usuarioAuditoriaService;

    public AreaAdminService(AreaRepository repository,
                            UnidadeRepository unidadeRepository,
                            UsuarioAuditoriaService usuarioAuditoriaService) {
        this.repository = repository;
        this.unidadeRepository = unidadeRepository;
        this.usuarioAuditoriaService = usuarioAuditoriaService;
    }

    @Transactional(readOnly = true)
    public List<Area> listar(String q) {
        String filtro = normalize(q);
        if (filtro == null || filtro.isBlank()) {
            return repository.findAllAtivasWithUnidadeOrderByDescricao();
        }
        return repository.buscarPorFiltro(filtro);
    }

    @Transactional(readOnly = true)
    public List<Area> listarCanceladas(String q) {
        String filtro = normalize(q);
        if (filtro == null || filtro.isBlank()) {
            return repository.findAllCanceladasWithUnidadeOrderByDescricao();
        }
        return repository.buscarCanceladosPorFiltro(filtro);
    }

    @Transactional(readOnly = true)
    public List<Area> listarAreasRecebemEntrada(Long unidadeId) {
        return repository.findAreasAtivasRecebemEntradaByUnidadeId(unidadeId);
    }

    @Transactional(readOnly = true)
    public Area buscar(Long id) {
        return repository.findByIdAndDtCancelamentoIsNull(id)
                .orElseThrow(() -> new IllegalArgumentException("Area nao encontrada"));
    }

    @Transactional(readOnly = true)
    public Area buscarCancelada(Long id) {
        return repository.findByIdAndDtCancelamentoIsNotNull(id)
                .orElseThrow(() -> new IllegalArgumentException("Area cancelada nao encontrada"));
    }

    @Transactional(readOnly = true)
    public Optional<Area> buscarCanceladaOpcional(Long id) {
        return repository.findByIdAndDtCancelamentoIsNotNull(id);
    }

    @Transactional
    public Area criar(AreaForm form) {
        LocalDateTime now = LocalDateTime.now();
        Long usuarioAtualId = currentUserId();
        Area area = new Area();
        area.setDtCadastro(now);
        area.setDtAtualizacao(now);
        area.setCadastroUserId(usuarioAtualId);
        area.setAtualizacaoUserId(usuarioAtualId);
        area.setDtCancelamento(null);
        area.setCancelamentoUserId(null);
        apply(area, form);
        return repository.save(area);
    }

    @Transactional
    public Area atualizar(Long id, AreaForm form) {
        LocalDateTime now = LocalDateTime.now();
        Long usuarioAtualId = currentUserId();
        Area area = buscar(id);
        apply(area, form);
        area.setDtAtualizacao(now);
        area.setAtualizacaoUserId(usuarioAtualId);
        return repository.save(area);
    }

    @Transactional
    public void excluir(Long id) {
        LocalDateTime now = LocalDateTime.now();
        Long usuarioAtualId = currentUserId();
        Area area = buscar(id);
        area.setDtCancelamento(now);
        area.setCancelamentoUserId(usuarioAtualId);
        area.setDtAtualizacao(now);
        area.setAtualizacaoUserId(usuarioAtualId);
        repository.save(area);
    }

    @Transactional
    public void restaurar(Long id) {
        LocalDateTime now = LocalDateTime.now();
        Long usuarioAtualId = currentUserId();
        Area area = buscarCancelada(id);
        area.setDtCancelamento(null);
        area.setCancelamentoUserId(null);
        area.setDtAtualizacao(now);
        area.setAtualizacaoUserId(usuarioAtualId);
        repository.save(area);
    }

    @Transactional
    public void excluirPermanente(Long id) {
        Area area = buscarCancelada(id);
        try {
            repository.delete(area);
            repository.flush();
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("Area possui vinculos e nao pode ser excluida permanentemente");
        }
    }

    @Transactional(readOnly = true)
    public AreaForm toForm(Area area) {
        AreaForm form = new AreaForm();
        form.setUnidadeId(area.getUnidade().getId());
        form.setDescricao(area.getDescricao());
        form.setDetalhamento(area.getDetalhamento());
        return form;
    }

    private void apply(Area area, AreaForm form) {
        Unidade unidade = unidadeRepository.findById(form.getUnidadeId())
                .orElseThrow(() -> new IllegalArgumentException("Unidade nao encontrada"));
        area.setUnidade(unidade);
        area.setDescricao(normalizeUpper(form.getDescricao()));
        area.setDetalhamento(normalize(form.getDetalhamento()));
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
