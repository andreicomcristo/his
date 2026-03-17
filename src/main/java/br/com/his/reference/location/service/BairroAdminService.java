package br.com.his.reference.location.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.his.reference.location.dto.BairroForm;
import br.com.his.reference.location.model.Bairro;
import br.com.his.reference.location.model.Municipio;
import br.com.his.reference.location.repository.BairroRepository;
import br.com.his.reference.location.repository.MunicipioRepository;
import br.com.his.reference.location.repository.UnidadeFederativaRepository;

@Service
public class BairroAdminService {

    private final BairroRepository repository;
    private final MunicipioRepository MunicipioRepository;
    private final UnidadeFederativaRepository unidadeFederativaRepository;

    public BairroAdminService(BairroRepository repository,
                              MunicipioRepository MunicipioRepository,
                              UnidadeFederativaRepository unidadeFederativaRepository) {
        this.repository = repository;
        this.MunicipioRepository = MunicipioRepository;
        this.unidadeFederativaRepository = unidadeFederativaRepository;
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
    public List<Bairro> listarCancelados(String q) {
        String filtro = normalize(q);
        if (filtro == null || filtro.isBlank()) {
            return repository.findAllCanceladosWithMunicipioOrderByNome();
        }
        return repository.buscarCanceladosPorFiltro(filtro);
    }

    @Transactional(readOnly = true)
    public Bairro buscar(Long id) {
        return repository.findByIdAndDtCancelamentoIsNull(id)
                .orElseThrow(() -> new IllegalArgumentException("Bairro nao encontrado"));
    }

    @Transactional(readOnly = true)
    public List<Bairro> listarPorMunicipio(Long municipioId) {
        return repository.findAtivosByMunicipioIdOrderByNome(municipioId);
    }

    @Transactional
    public Bairro criar(BairroForm form) {
        Bairro bairro = new Bairro();
        bairro.setDtCancelamento(null);
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
        Bairro bairro = buscar(id);
        bairro.setDtCancelamento(LocalDateTime.now());
        repository.save(bairro);
    }

    @Transactional
    public void restaurar(Long id) {
        Bairro bairro = repository.findByIdAndDtCancelamentoIsNotNull(id)
                .orElseThrow(() -> new IllegalArgumentException("Bairro cancelado nao encontrado"));
        bairro.setDtCancelamento(null);
        repository.save(bairro);
    }

    @Transactional
    public void excluirPermanente(Long id) {
        Bairro bairro = repository.findByIdAndDtCancelamentoIsNotNull(id)
                .orElseThrow(() -> new IllegalArgumentException("Bairro cancelado nao encontrado"));
        try {
            repository.delete(bairro);
            repository.flush();
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("Bairro possui vinculos e nao pode ser excluido permanentemente");
        }
    }

    public BairroForm toForm(Bairro bairro) {
        BairroForm form = new BairroForm();
        form.setNome(bairro.getNome());
        form.setMunicipioId(bairro.getMunicipio().getId());
        form.setUnidadeFederativaId(bairro.getMunicipio().getUnidadeFederativa().getId());
        return form;
    }

    private void apply(Bairro bairro, BairroForm form) {
        Long unidadeFederativaAtualId = bairro.getMunicipio() == null || bairro.getMunicipio().getUnidadeFederativa() == null
                ? null
                : bairro.getMunicipio().getUnidadeFederativa().getId();
        if (unidadeFederativaAtualId == null || !unidadeFederativaAtualId.equals(form.getUnidadeFederativaId())) {
            unidadeFederativaRepository.findByIdAndDtCancelamentoIsNull(form.getUnidadeFederativaId())
                    .orElseThrow(() -> new IllegalArgumentException("UF nao encontrada"));
        }
        Municipio municipioAtual = bairro.getMunicipio();
        Municipio municipio;
        if (municipioAtual != null
                && municipioAtual.getId() != null
                && municipioAtual.getId().equals(form.getMunicipioId())) {
            municipio = municipioAtual;
        } else {
            municipio = MunicipioRepository.findByIdAndDtCancelamentoIsNull(form.getMunicipioId())
                    .orElseThrow(() -> new IllegalArgumentException("Municipio nao encontrado"));
        }
        if (form.getUnidadeFederativaId() == null || municipio.getUnidadeFederativa() == null
                || !municipio.getUnidadeFederativa().getId().equals(form.getUnidadeFederativaId())) {
            throw new IllegalArgumentException("Municipio nao pertence a UF informada");
        }
        bairro.setNome(normalize(form.getNome()).toUpperCase());
        bairro.setMunicipio(municipio);
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }
}
