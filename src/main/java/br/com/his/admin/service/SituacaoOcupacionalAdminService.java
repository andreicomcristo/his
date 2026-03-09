package br.com.his.admin.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.his.admin.dto.SituacaoOcupacionalForm;
import br.com.his.assistencial.model.SituacaoOcupacional;
import br.com.his.assistencial.repository.SituacaoOcupacionalRepository;

@Service
public class SituacaoOcupacionalAdminService {

    private final SituacaoOcupacionalRepository repository;

    public SituacaoOcupacionalAdminService(SituacaoOcupacionalRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<SituacaoOcupacional> listarTodas() {
        return repository.findAllByOrderByDescricaoAsc();
    }

    @Transactional(readOnly = true)
    public List<SituacaoOcupacional> listarAtivas() {
        return repository.findByAtivoTrueOrderByDescricaoAsc();
    }

    @Transactional(readOnly = true)
    public SituacaoOcupacional buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Situacao ocupacional nao encontrada: " + id));
    }

    @Transactional
    public SituacaoOcupacional criar(SituacaoOcupacionalForm form) {
        SituacaoOcupacional entity = new SituacaoOcupacional();
        apply(form, entity);
        return repository.save(entity);
    }

    @Transactional
    public SituacaoOcupacional atualizar(Long id, SituacaoOcupacionalForm form) {
        SituacaoOcupacional entity = buscarPorId(id);
        apply(form, entity);
        return repository.save(entity);
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscarPorId(id));
    }

    public SituacaoOcupacionalForm toForm(SituacaoOcupacional entity) {
        SituacaoOcupacionalForm form = new SituacaoOcupacionalForm();
        form.setDescricao(entity.getDescricao());
        form.setAtivo(entity.isAtivo());
        return form;
    }

    private void apply(SituacaoOcupacionalForm form, SituacaoOcupacional entity) {
        entity.setDescricao(form.getDescricao() == null ? null : form.getDescricao().trim());
        entity.setAtivo(form.isAtivo());
    }
}
