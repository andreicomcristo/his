package br.com.his.admin.service;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.his.access.model.Unidade;
import br.com.his.access.repository.UnidadeRepository;
import br.com.his.admin.dto.UnidadeForm;
import br.com.his.configuracao.model.Cidade;
import br.com.his.configuracao.repository.CidadeRepository;

@Service
public class UnidadeAdminService {

    private final UnidadeRepository unidadeRepository;
    private final CidadeRepository cidadeRepository;

    public UnidadeAdminService(UnidadeRepository unidadeRepository,
                              CidadeRepository cidadeRepository) {
        this.unidadeRepository = unidadeRepository;
        this.cidadeRepository = cidadeRepository;
    }

    @Transactional(readOnly = true)
    public List<Unidade> listar(String filtro) {
        String normalized = normalize(filtro);
        if (normalized == null) {
            return unidadeRepository.findAllByOrderByNomeAsc();
        }
        return unidadeRepository.findByNomeContainingIgnoreCaseOrCnesContainingIgnoreCaseOrderByNomeAsc(
                normalized, normalized);
    }

    @Transactional(readOnly = true)
    public Unidade buscarPorId(Long id) {
        return unidadeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Unidade nao encontrada: " + id));
    }

    @Transactional
    public Unidade criar(UnidadeForm form) {
        Unidade unidade = new Unidade();
        apply(form, unidade, cidadeRepository);
        unidade.setAtivo(true);
        return salvar(unidade);
    }

    @Transactional
    public Unidade atualizar(Long id, UnidadeForm form) {
        Unidade unidade = buscarPorId(id);
        apply(form, unidade, cidadeRepository);
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

    private static void apply(UnidadeForm form, Unidade unidade, CidadeRepository cidadeRepository) {
        Cidade cidade = cidadeRepository.findById(form.getCidadeId())
                .orElseThrow(() -> new IllegalArgumentException("Cidade invalida"));
        if (form.getUnidadeFederativaId() == null || cidade.getUnidadeFederativa() == null
                || !cidade.getUnidadeFederativa().getId().equals(form.getUnidadeFederativaId())) {
            throw new IllegalArgumentException("Cidade nao pertence a UF informada");
        }
        unidade.setNome(normalizeUpper(form.getNome()));
        unidade.setTipoEstabelecimento(normalizeUpper(form.getTipoEstabelecimento()));
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
