package br.com.his.reference.location.service;

import java.util.List;

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
            return repository.findAllByOrderByDescricaoAsc();
        }
        return repository.buscarPorFiltro(filtro);
    }

    @Transactional(readOnly = true)
    public List<UnidadeFederativa> listarTodas() {
        return repository.findAllByOrderByDescricaoAsc();
    }

    @Transactional(readOnly = true)
    public UnidadeFederativa buscar(Long id) {
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("UF nao encontrada"));
    }

    @Transactional
    public UnidadeFederativa criar(UnidadeFederativaForm form) {
        UnidadeFederativa uf = new UnidadeFederativa();
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
        repository.delete(buscar(id));
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
