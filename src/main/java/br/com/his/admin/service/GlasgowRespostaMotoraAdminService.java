package br.com.his.admin.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.his.admin.dto.GlasgowRespostaMotoraForm;
import br.com.his.care.triage.model.GlasgowRespostaMotora;
import br.com.his.care.triage.repository.GlasgowRespostaMotoraRepository;

@Service
public class GlasgowRespostaMotoraAdminService {

    private final GlasgowRespostaMotoraRepository repository;

    public GlasgowRespostaMotoraAdminService(GlasgowRespostaMotoraRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<GlasgowRespostaMotora> listar(String q) {
        String filtro = normalize(q);
        if (filtro == null) {
            return repository.findAllByOrderByPontosDesc();
        }
        return repository.buscarPorFiltro(filtro);
    }

    @Transactional(readOnly = true)
    public List<GlasgowRespostaMotora> listarAtivos() {
        return repository.findByAtivoTrueOrderByPontosDesc();
    }

    @Transactional(readOnly = true)
    public GlasgowRespostaMotora buscar(Long id) {
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Item de resposta motora nao encontrado"));
    }

    @Transactional
    public GlasgowRespostaMotora criar(GlasgowRespostaMotoraForm form) {
        GlasgowRespostaMotora item = new GlasgowRespostaMotora();
        apply(item, form);
        return repository.save(item);
    }

    @Transactional
    public GlasgowRespostaMotora atualizar(Long id, GlasgowRespostaMotoraForm form) {
        GlasgowRespostaMotora item = buscar(id);
        apply(item, form);
        return repository.save(item);
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscar(id));
    }

    @Transactional(readOnly = true)
    public GlasgowRespostaMotoraForm toForm(GlasgowRespostaMotora item) {
        GlasgowRespostaMotoraForm form = new GlasgowRespostaMotoraForm();
        form.setPontos(item.getPontos());
        form.setDescricao(item.getDescricao());
        form.setAtivo(item.isAtivo());
        return form;
    }

    private void apply(GlasgowRespostaMotora item, GlasgowRespostaMotoraForm form) {
        item.setPontos(form.getPontos());
        item.setDescricao(normalizeUpper(form.getDescricao()));
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
}
