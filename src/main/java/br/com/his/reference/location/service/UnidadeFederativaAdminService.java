package br.com.his.reference.location.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.his.reference.location.dto.UnidadeFederativaForm;
import br.com.his.reference.location.model.UnidadeFederativa;
import br.com.his.reference.location.repository.UnidadeFederativaRepository;

@Service
public class UnidadeFederativaAdminService {

    private final UnidadeFederativaRepository repository;

    public UnidadeFederativaAdminService(UnidadeFederativaRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<UnidadeFederativa> listar(String q) {
        String filtro = normalize(q);
        if (filtro == null || filtro.isBlank()) {
            return repository.findAtivasOrderByDescricaoAsc();
        }
        return repository.buscarAtivasPorFiltro(filtro);
    }

    @Transactional(readOnly = true)
    public List<UnidadeFederativa> listarCanceladas(String q) {
        String filtro = normalize(q);
        if (filtro == null || filtro.isBlank()) {
            return repository.findCanceladasOrderByDescricaoAsc();
        }
        return repository.buscarCanceladasPorFiltro(filtro);
    }

    @Transactional(readOnly = true)
    public List<UnidadeFederativa> listarTodas() {
        return repository.findAtivasOrderByDescricaoAsc();
    }

    @Transactional(readOnly = true)
    public UnidadeFederativa buscar(Long id) {
        return repository.findByIdAndDtCancelamentoIsNull(id)
                .orElseThrow(() -> new IllegalArgumentException("UF nao encontrada"));
    }

    @Transactional(readOnly = true)
    public UnidadeFederativa buscarCancelada(Long id) {
        return repository.findByIdAndDtCancelamentoIsNotNull(id)
                .orElseThrow(() -> new IllegalArgumentException("UF cancelada nao encontrada"));
    }

    @Transactional(readOnly = true)
    public Optional<UnidadeFederativa> buscarCanceladaOpcional(Long id) {
        return repository.findByIdAndDtCancelamentoIsNotNull(id);
    }

    @Transactional
    public UnidadeFederativa criar(UnidadeFederativaForm form) {
        UnidadeFederativa uf = new UnidadeFederativa();
        uf.setDtCancelamento(null);
        apply(uf, form);
        return repository.save(uf);
    }

    @Transactional
    public UnidadeFederativa atualizar(Long id, UnidadeFederativaForm form) {
        UnidadeFederativa uf = buscar(id);
        apply(uf, form);
        return repository.save(uf);
    }

    @Transactional
    public void excluir(Long id) {
        UnidadeFederativa uf = buscar(id);
        uf.setDtCancelamento(LocalDateTime.now());
        repository.save(uf);
    }

    @Transactional
    public void restaurar(Long id) {
        UnidadeFederativa uf = buscarCancelada(id);
        uf.setDtCancelamento(null);
        repository.save(uf);
    }

    @Transactional
    public void excluirPermanente(Long id) {
        UnidadeFederativa uf = buscarCancelada(id);
        try {
            repository.delete(uf);
            repository.flush();
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("UF possui vinculos e nao pode ser excluida permanentemente");
        }
    }

    public UnidadeFederativaForm toForm(UnidadeFederativa uf) {
        UnidadeFederativaForm form = new UnidadeFederativaForm();
        form.setDescricao(uf.getDescricao());
        form.setSigla(uf.getSigla());
        form.setCodigoIbge(uf.getCodigoIbge());
        return form;
    }

    private void apply(UnidadeFederativa uf, UnidadeFederativaForm form) {
        uf.setDescricao(normalize(form.getDescricao()).toUpperCase());
        uf.setSigla(normalize(form.getSigla()).toUpperCase());
        uf.setCodigoIbge(normalize(form.getCodigoIbge()));
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }
}
