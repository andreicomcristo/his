package br.com.his.reference.location.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.his.reference.location.dto.MunicipioForm;
import br.com.his.reference.location.model.Municipio;
import br.com.his.reference.location.model.UnidadeFederativa;
import br.com.his.reference.location.repository.MunicipioRepository;
import br.com.his.reference.location.repository.UnidadeFederativaRepository;

@Service
public class MunicipioAdminService {

    private final MunicipioRepository repository;
    private final UnidadeFederativaRepository unidadeFederativaRepository;

    public MunicipioAdminService(MunicipioRepository repository, UnidadeFederativaRepository unidadeFederativaRepository) {
        this.repository = repository;
        this.unidadeFederativaRepository = unidadeFederativaRepository;
    }

    @Transactional(readOnly = true)
    public List<Municipio> listar(String q) {
        String filtro = normalize(q);
        if (filtro == null || filtro.isBlank()) {
            return repository.findAllWithUnidadeFederativaOrderByNome();
        }
        return repository.buscarPorFiltro(filtro);
    }

    @Transactional(readOnly = true)
    public Municipio buscar(Long id) {
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Municipio nao encontrado"));
    }

    @Transactional(readOnly = true)
    public List<Municipio> listarPorUf(Long unidadeFederativaId) {
        return repository.findByUnidadeFederativaIdOrderByNome(unidadeFederativaId);
    }

    @Transactional
    public Municipio criar(MunicipioForm form) {
        Municipio municipio = new Municipio();
        apply(municipio, form);
        return repository.save(municipio);
    }

    @Transactional
    public Municipio atualizar(Long id, MunicipioForm form) {
        Municipio municipio = buscar(id);
        apply(municipio, form);
        return repository.save(municipio);
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscar(id));
    }

    public MunicipioForm toForm(Municipio municipio) {
        MunicipioForm form = new MunicipioForm();
        form.setNome(municipio.getNome());
        form.setCodigoIbge(municipio.getCodigoIbge());
        form.setUnidadeFederativaId(municipio.getUnidadeFederativa().getId());
        return form;
    }

    private void apply(Municipio municipio, MunicipioForm form) {
        UnidadeFederativa uf = unidadeFederativaRepository.findById(form.getUnidadeFederativaId())
                .orElseThrow(() -> new IllegalArgumentException("UF nao encontrada"));
        municipio.setNome(normalize(form.getNome()).toUpperCase());
        municipio.setCodigoIbge(normalize(form.getCodigoIbge()));
        municipio.setUnidadeFederativa(uf);
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }
}
