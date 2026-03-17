package br.com.his.access.service;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.his.access.model.Unidade;
import br.com.his.access.model.TipoUnidade;
import br.com.his.access.repository.UnidadeRepository;
import br.com.his.access.repository.TipoUnidadeRepository;
import br.com.his.access.dto.UnidadeForm;
import br.com.his.reference.location.model.Municipio;
import br.com.his.reference.location.repository.MunicipioRepository;
import br.com.his.reference.location.repository.UnidadeFederativaRepository;

@Service
public class UnidadeAdminService {

    private final UnidadeRepository unidadeRepository;
    private final MunicipioRepository municipioRepository;
    private final TipoUnidadeRepository tipoUnidadeRepository;
    private final UnidadeFederativaRepository unidadeFederativaRepository;

    public UnidadeAdminService(UnidadeRepository unidadeRepository,
                               MunicipioRepository municipioRepository,
                               TipoUnidadeRepository tipoUnidadeRepository,
                               UnidadeFederativaRepository unidadeFederativaRepository) {
        this.unidadeRepository = unidadeRepository;
        this.municipioRepository = municipioRepository;
        this.tipoUnidadeRepository = tipoUnidadeRepository;
        this.unidadeFederativaRepository = unidadeFederativaRepository;
    }

    @Transactional(readOnly = true)
    public List<Unidade> listar(String filtro) {
        String normalized = normalize(filtro);
        if (normalized == null) {
            return unidadeRepository.findAllByOrderByNomeAsc();
        }
        return unidadeRepository.findByNomeContainingIgnoreCaseOrSiglaContainingIgnoreCaseOrCnesContainingIgnoreCaseOrderByNomeAsc(
                normalized, normalized, normalized);
    }

    @Transactional(readOnly = true)
    public Unidade buscarPorId(Long id) {
        return unidadeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Unidade nao encontrada: " + id));
    }

    @Transactional
    public Unidade criar(UnidadeForm form) {
        Unidade unidade = new Unidade();
        apply(form, unidade, municipioRepository, tipoUnidadeRepository, unidadeFederativaRepository);
        unidade.setAtivo(true);
        return salvar(unidade);
    }

    @Transactional
    public Unidade atualizar(Long id, UnidadeForm form) {
        Unidade unidade = buscarPorId(id);
        apply(form, unidade, municipioRepository, tipoUnidadeRepository, unidadeFederativaRepository);
        return salvar(unidade);
    }

    @Transactional
    public void ativarDesativar(Long id) {
        Unidade unidade = buscarPorId(id);
        unidade.setAtivo(!unidade.isAtivo());
        unidadeRepository.save(unidade);
    }

    private Unidade salvar(Unidade unidade) {
        try {
            return unidadeRepository.save(unidade);
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("CNES ja cadastrado em outra unidade");
        }
    }

    private static void apply(UnidadeForm form,
                              Unidade unidade,
                              MunicipioRepository municipioRepository,
                              TipoUnidadeRepository tipoUnidadeRepository,
                              UnidadeFederativaRepository unidadeFederativaRepository) {
        Municipio municipioAtual = unidade.getMunicipio();
        Municipio municipio;
        if (municipioAtual != null
                && municipioAtual.getId() != null
                && municipioAtual.getId().equals(form.getMunicipioId())) {
            municipio = municipioAtual;
        } else {
            municipio = municipioRepository.findByIdAndDtCancelamentoIsNull(form.getMunicipioId())
                    .orElseThrow(() -> new IllegalArgumentException("Municipio invalido"));
        }
        Long unidadeFederativaAtualId = unidade.getMunicipio() == null || unidade.getMunicipio().getUnidadeFederativa() == null
                ? null
                : unidade.getMunicipio().getUnidadeFederativa().getId();
        if (unidadeFederativaAtualId == null || !unidadeFederativaAtualId.equals(form.getUnidadeFederativaId())) {
            unidadeFederativaRepository.findByIdAndDtCancelamentoIsNull(form.getUnidadeFederativaId())
                    .orElseThrow(() -> new IllegalArgumentException("UF invalida"));
        }
        TipoUnidade tipoUnidadeAtual = unidade.getTipoUnidade();
        TipoUnidade tipoUnidade;
        if (tipoUnidadeAtual != null
                && tipoUnidadeAtual.getId() != null
                && tipoUnidadeAtual.getId().equals(form.getTipoUnidadeId())) {
            tipoUnidade = tipoUnidadeAtual;
        } else {
            tipoUnidade = tipoUnidadeRepository.findByIdAndDtCancelamentoIsNull(form.getTipoUnidadeId())
                    .orElseThrow(() -> new IllegalArgumentException("Tipo de unidade invalido"));
        }
        if (form.getUnidadeFederativaId() == null || municipio.getUnidadeFederativa() == null
                || !municipio.getUnidadeFederativa().getId().equals(form.getUnidadeFederativaId())) {
            throw new IllegalArgumentException("Municipio nao pertence a UF informada");
        }
        unidade.setNome(normalizeUpper(form.getNome()));
        unidade.setSigla(normalizeUpper(form.getSigla()));
        unidade.setTipoUnidade(tipoUnidade);
        unidade.setCnes(normalize(form.getCnes()));
        unidade.setMunicipio(municipio);
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
}
