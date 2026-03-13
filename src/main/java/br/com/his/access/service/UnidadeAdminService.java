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
import br.com.his.reference.location.model.Cidade;
import br.com.his.reference.location.repository.CidadeRepository;

@Service
public class UnidadeAdminService {

    private final UnidadeRepository unidadeRepository;
    private final CidadeRepository cidadeRepository;
    private final TipoUnidadeRepository tipoUnidadeRepository;

    public UnidadeAdminService(UnidadeRepository unidadeRepository,
                               CidadeRepository cidadeRepository,
                               TipoUnidadeRepository tipoUnidadeRepository) {
        this.unidadeRepository = unidadeRepository;
        this.cidadeRepository = cidadeRepository;
        this.tipoUnidadeRepository = tipoUnidadeRepository;
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
        apply(form, unidade, cidadeRepository, tipoUnidadeRepository);
        unidade.setAtivo(true);
        return salvar(unidade);
    }

    @Transactional
    public Unidade atualizar(Long id, UnidadeForm form) {
        Unidade unidade = buscarPorId(id);
        apply(form, unidade, cidadeRepository, tipoUnidadeRepository);
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
                              CidadeRepository cidadeRepository,
                              TipoUnidadeRepository tipoUnidadeRepository) {
        Cidade cidade = cidadeRepository.findById(form.getCidadeId())
                .orElseThrow(() -> new IllegalArgumentException("Cidade invalida"));
        TipoUnidade tipoUnidade = tipoUnidadeRepository.findById(form.getTipoUnidadeId())
                .orElseThrow(() -> new IllegalArgumentException("Tipo de unidade invalido"));
        if (form.getUnidadeFederativaId() == null || cidade.getUnidadeFederativa() == null
                || !cidade.getUnidadeFederativa().getId().equals(form.getUnidadeFederativaId())) {
            throw new IllegalArgumentException("Cidade nao pertence a UF informada");
        }
        unidade.setNome(normalizeUpper(form.getNome()));
        unidade.setSigla(normalizeUpper(form.getSigla()));
        unidade.setTipoUnidade(tipoUnidade);
        unidade.setCnes(normalize(form.getCnes()));
        unidade.setCidade(cidade);
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
