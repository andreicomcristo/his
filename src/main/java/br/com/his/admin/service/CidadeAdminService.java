package br.com.his.admin.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.his.admin.dto.CidadeForm;
import br.com.his.reference.location.model.Cidade;
import br.com.his.reference.location.model.UnidadeFederativa;
import br.com.his.reference.location.repository.CidadeRepository;
import br.com.his.reference.location.repository.UnidadeFederativaRepository;

@Service
public class CidadeAdminService {

    private final CidadeRepository repository;
    private final UnidadeFederativaRepository unidadeFederativaRepository;

    public CidadeAdminService(CidadeRepository repository, UnidadeFederativaRepository unidadeFederativaRepository) {
        this.repository = repository;
        this.unidadeFederativaRepository = unidadeFederativaRepository;
    }

    @Transactional(readOnly = true)
    public List<Cidade> listar(String q) {
        String filtro = normalize(q);
        if (filtro == null || filtro.isBlank()) {
            return repository.findAllWithUnidadeFederativaOrderByNome();
        }
        return repository.buscarPorFiltro(filtro);
    }

    @Transactional(readOnly = true)
    public Cidade buscar(Long id) {
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Cidade nao encontrada"));
    }

    @Transactional(readOnly = true)
    public List<Cidade> listarPorUf(Long unidadeFederativaId) {
        return repository.findByUnidadeFederativaIdOrderByNome(unidadeFederativaId);
    }

    @Transactional
    public Cidade criar(CidadeForm form) {
        Cidade cidade = new Cidade();
        apply(cidade, form);
        return repository.save(cidade);
    }

    @Transactional
    public Cidade atualizar(Long id, CidadeForm form) {
        Cidade cidade = buscar(id);
        apply(cidade, form);
        return repository.save(cidade);
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscar(id));
    }

    public CidadeForm toForm(Cidade cidade) {
        CidadeForm form = new CidadeForm();
        form.setNome(cidade.getNome());
        form.setCodigoIbge(cidade.getCodigoIbge());
        form.setUnidadeFederativaId(cidade.getUnidadeFederativa().getId());
        return form;
    }

    private void apply(Cidade cidade, CidadeForm form) {
        UnidadeFederativa uf = unidadeFederativaRepository.findById(form.getUnidadeFederativaId())
                .orElseThrow(() -> new IllegalArgumentException("UF nao encontrada"));
        cidade.setNome(normalize(form.getNome()).toUpperCase());
        cidade.setCodigoIbge(normalize(form.getCodigoIbge()));
        cidade.setUnidadeFederativa(uf);
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }
}
