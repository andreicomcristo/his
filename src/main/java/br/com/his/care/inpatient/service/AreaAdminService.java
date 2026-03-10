package br.com.his.care.inpatient.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.his.access.model.Unidade;
import br.com.his.access.repository.UnidadeRepository;
import br.com.his.care.inpatient.dto.AreaForm;
import br.com.his.care.inpatient.model.Area;
import br.com.his.care.inpatient.repository.AreaRepository;

@Service
public class AreaAdminService {

    private final AreaRepository repository;
    private final UnidadeRepository unidadeRepository;

    public AreaAdminService(AreaRepository repository, UnidadeRepository unidadeRepository) {
        this.repository = repository;
        this.unidadeRepository = unidadeRepository;
    }

    @Transactional(readOnly = true)
    public List<Area> listar(String q) {
        String filtro = normalize(q);
        if (filtro == null) {
            return repository.findAllWithUnidadeOrderByNome();
        }
        return repository.buscarPorFiltro(filtro);
    }

    @Transactional(readOnly = true)
    public List<Area> listarAreasRecebemEntrada(Long unidadeId) {
        return repository.findAreasAtivasRecebemEntradaByUnidadeId(unidadeId);
    }

    @Transactional(readOnly = true)
    public Area buscar(Long id) {
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Area nao encontrada"));
    }

    @Transactional
    public Area criar(AreaForm form) {
        Area area = new Area();
        apply(area, form);
        return repository.save(area);
    }

    @Transactional
    public Area atualizar(Long id, AreaForm form) {
        Area area = buscar(id);
        apply(area, form);
        return repository.save(area);
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscar(id));
    }

    @Transactional(readOnly = true)
    public AreaForm toForm(Area area) {
        AreaForm form = new AreaForm();
        form.setUnidadeId(area.getUnidade().getId());
        form.setNome(area.getNome());
        form.setDescricao(area.getDescricao());
        form.setAtivo(area.isAtivo());
        return form;
    }

    private void apply(Area area, AreaForm form) {
        Unidade unidade = unidadeRepository.findById(form.getUnidadeId())
                .orElseThrow(() -> new IllegalArgumentException("Unidade nao encontrada"));
        area.setUnidade(unidade);
        area.setNome(normalizeUpper(form.getNome()));
        area.setDescricao(normalize(form.getDescricao()));
        area.setAtivo(form.isAtivo());
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
