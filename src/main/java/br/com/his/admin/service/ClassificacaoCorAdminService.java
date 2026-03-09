package br.com.his.admin.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.his.admin.dto.ClassificacaoCorForm;
import br.com.his.assistencial.model.ClassificacaoCor;
import br.com.his.assistencial.repository.ClassificacaoCorRepository;

@Service
public class ClassificacaoCorAdminService {

    private final ClassificacaoCorRepository repository;

    public ClassificacaoCorAdminService(ClassificacaoCorRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<ClassificacaoCor> listar(String q) {
        String filtro = normalize(q);
        if (filtro == null) {
            return repository.findAllByOrderByOrdemExibicaoAscDescricaoAsc();
        }
        return repository.buscarPorFiltro(filtro);
    }

    @Transactional(readOnly = true)
    public List<ClassificacaoCor> listarAtivos() {
        return repository.findByAtivoTrueOrderByOrdemExibicaoAscDescricaoAsc();
    }

    @Transactional(readOnly = true)
    public ClassificacaoCor buscar(Long id) {
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Classificacao de cor nao encontrada"));
    }

    @Transactional
    public ClassificacaoCor criar(ClassificacaoCorForm form) {
        ClassificacaoCor item = new ClassificacaoCor();
        apply(item, form);
        return repository.save(item);
    }

    @Transactional
    public ClassificacaoCor atualizar(Long id, ClassificacaoCorForm form) {
        ClassificacaoCor item = buscar(id);
        apply(item, form);
        return repository.save(item);
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscar(id));
    }

    @Transactional(readOnly = true)
    public ClassificacaoCorForm toForm(ClassificacaoCor item) {
        ClassificacaoCorForm form = new ClassificacaoCorForm();
        form.setDescricao(item.getDescricao());
        form.setCor(item.getCor());
        form.setOrdemExibicao(item.getOrdemExibicao());
        form.setRiscoMaior(item.isRiscoMaior());
        form.setAtivo(item.isAtivo());
        return form;
    }

    private void apply(ClassificacaoCor item, ClassificacaoCorForm form) {
        item.setDescricao(normalizeUpper(form.getDescricao()));
        item.setCor(normalizeUpperHex(form.getCor()));
        item.setOrdemExibicao(form.getOrdemExibicao() == null ? 0 : form.getOrdemExibicao());
        item.setRiscoMaior(form.isRiscoMaior());
        item.setAtivo(form.isAtivo());
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

    private static String normalizeUpperHex(String value) {
        String normalized = normalize(value);
        return normalized == null ? null : normalized.toUpperCase();
    }
}
