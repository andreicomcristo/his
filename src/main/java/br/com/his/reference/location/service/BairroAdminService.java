package br.com.his.reference.location.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.his.reference.location.dto.BairroForm;
import br.com.his.reference.location.model.Bairro;
import br.com.his.reference.location.model.Municipio;
import br.com.his.reference.location.repository.BairroRepository;
import br.com.his.reference.location.repository.MunicipioRepository;

@Service
public class BairroAdminService {

    private final BairroRepository repository;
    private final MunicipioRepository MunicipioRepository;

    public BairroAdminService(BairroRepository repository, MunicipioRepository MunicipioRepository) {
        this.repository = repository;
        this.MunicipioRepository = MunicipioRepository;
    }

    @Transactional(readOnly = true)
    public List<Bairro> listar(String q) {
        String filtro = normalize(q);
        if (filtro == null || filtro.isBlank()) {
            return repository.findAllWithMunicipioOrderByNome();
        }
        return repository.buscarPorFiltro(filtro);
    }

    @Transactional(readOnly = true)
    public Bairro buscar(Long id) {
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Bairro nao encontrado"));
    }

    @Transactional(readOnly = true)
    public List<Bairro> listarPorMunicipio(Long municipioId) {
        return repository.findByMunicipioIdOrderByNome(municipioId);
    }

    @Transactional
    public Bairro criar(BairroForm form) {
        Bairro bairro = new Bairro();
        apply(bairro, form);
        return repository.save(bairro);
    }

    @Transactional
    public Bairro atualizar(Long id, BairroForm form) {
        Bairro bairro = buscar(id);
        apply(bairro, form);
        return repository.save(bairro);
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscar(id));
    }

    public BairroForm toForm(Bairro bairro) {
        BairroForm form = new BairroForm();
        form.setNome(bairro.getNome());
        form.setAtivo(bairro.isAtivo());
        form.setMunicipioId(bairro.getMunicipio().getId());
        form.setUnidadeFederativaId(bairro.getMunicipio().getUnidadeFederativa().getId());
        return form;
    }

    private void apply(Bairro bairro, BairroForm form) {
        Municipio Municipio = MunicipioRepository.findById(form.getMunicipioId())
                .orElseThrow(() -> new IllegalArgumentException("Municipio nao encontrada"));
        if (form.getUnidadeFederativaId() == null || Municipio.getUnidadeFederativa() == null
                || !Municipio.getUnidadeFederativa().getId().equals(form.getUnidadeFederativaId())) {
            throw new IllegalArgumentException("Municipio nao pertence a UF informada");
        }
        bairro.setNome(normalize(form.getNome()).toUpperCase());
        bairro.setMunicipio(Municipio);
        bairro.setAtivo(form.isAtivo());
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }
}

