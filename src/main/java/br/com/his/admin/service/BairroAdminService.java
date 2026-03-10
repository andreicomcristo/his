package br.com.his.admin.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.his.admin.dto.BairroForm;
import br.com.his.reference.location.model.Bairro;
import br.com.his.reference.location.model.Cidade;
import br.com.his.reference.location.repository.BairroRepository;
import br.com.his.reference.location.repository.CidadeRepository;

@Service
public class BairroAdminService {

    private final BairroRepository repository;
    private final CidadeRepository cidadeRepository;

    public BairroAdminService(BairroRepository repository, CidadeRepository cidadeRepository) {
        this.repository = repository;
        this.cidadeRepository = cidadeRepository;
    }

    @Transactional(readOnly = true)
    public List<Bairro> listar(String q) {
        String filtro = normalize(q);
        if (filtro == null || filtro.isBlank()) {
            return repository.findAllWithCidadeOrderByNome();
        }
        return repository.buscarPorFiltro(filtro);
    }

    @Transactional(readOnly = true)
    public Bairro buscar(Long id) {
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Bairro nao encontrado"));
    }

    @Transactional(readOnly = true)
    public List<Bairro> listarPorCidade(Long cidadeId) {
        return repository.findByCidadeIdOrderByNome(cidadeId);
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
        form.setCidadeId(bairro.getCidade().getId());
        form.setUnidadeFederativaId(bairro.getCidade().getUnidadeFederativa().getId());
        return form;
    }

    private void apply(Bairro bairro, BairroForm form) {
        Cidade cidade = cidadeRepository.findById(form.getCidadeId())
                .orElseThrow(() -> new IllegalArgumentException("Cidade nao encontrada"));
        if (form.getUnidadeFederativaId() == null || cidade.getUnidadeFederativa() == null
                || !cidade.getUnidadeFederativa().getId().equals(form.getUnidadeFederativaId())) {
            throw new IllegalArgumentException("Cidade nao pertence a UF informada");
        }
        bairro.setNome(normalize(form.getNome()).toUpperCase());
        bairro.setCidade(cidade);
        bairro.setAtivo(form.isAtivo());
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }
}
