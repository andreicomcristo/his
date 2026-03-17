package br.com.his.reference.location.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
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
            return repository.findAtivosWithUnidadeFederativaOrderByNome();
        }
        return repository.buscarAtivosPorFiltro(filtro);
    }

    @Transactional(readOnly = true)
    public List<Municipio> listarCancelados(String q) {
        String filtro = normalize(q);
        if (filtro == null || filtro.isBlank()) {
            return repository.findCanceladosWithUnidadeFederativaOrderByNome();
        }
        return repository.buscarCanceladosPorFiltro(filtro);
    }

    @Transactional(readOnly = true)
    public Municipio buscar(Long id) {
        return repository.findByIdAndDtCancelamentoIsNull(id)
                .orElseThrow(() -> new IllegalArgumentException("Municipio nao encontrado"));
    }

    @Transactional(readOnly = true)
    public Municipio buscarCancelado(Long id) {
        return repository.findByIdAndDtCancelamentoIsNotNull(id)
                .orElseThrow(() -> new IllegalArgumentException("Municipio cancelado nao encontrado"));
    }

    @Transactional(readOnly = true)
    public Optional<Municipio> buscarCanceladoOpcional(Long id) {
        return repository.findByIdAndDtCancelamentoIsNotNull(id);
    }

    @Transactional(readOnly = true)
    public List<Municipio> listarPorUf(Long unidadeFederativaId) {
        return repository.findByUnidadeFederativaIdOrderByNome(unidadeFederativaId);
    }

    @Transactional
    public Municipio criar(MunicipioForm form) {
        Municipio municipio = new Municipio();
        municipio.setDtCancelamento(null);
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
        Municipio municipio = buscar(id);
        municipio.setDtCancelamento(LocalDateTime.now());
        repository.save(municipio);
    }

    @Transactional
    public void restaurar(Long id) {
        Municipio municipio = buscarCancelado(id);
        municipio.setDtCancelamento(null);
        repository.save(municipio);
    }

    @Transactional
    public void excluirPermanente(Long id) {
        Municipio municipio = buscarCancelado(id);
        try {
            repository.delete(municipio);
            repository.flush();
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("Municipio possui vinculos e nao pode ser excluido permanentemente");
        }
    }

    public MunicipioForm toForm(Municipio municipio) {
        MunicipioForm form = new MunicipioForm();
        form.setNome(municipio.getNome());
        form.setCodigoIbge(municipio.getCodigoIbge());
        form.setUnidadeFederativaId(municipio.getUnidadeFederativa().getId());
        return form;
    }

    private void apply(Municipio municipio, MunicipioForm form) {
        UnidadeFederativa ufAtual = municipio.getUnidadeFederativa();
        UnidadeFederativa uf;
        if (ufAtual != null
                && ufAtual.getId() != null
                && ufAtual.getId().equals(form.getUnidadeFederativaId())) {
            uf = ufAtual;
        } else {
            uf = unidadeFederativaRepository.findByIdAndDtCancelamentoIsNull(form.getUnidadeFederativaId())
                    .orElseThrow(() -> new IllegalArgumentException("UF nao encontrada"));
        }
        municipio.setNome(normalize(form.getNome()).toUpperCase());
        municipio.setCodigoIbge(normalize(form.getCodigoIbge()));
        municipio.setUnidadeFederativa(uf);
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }
}
