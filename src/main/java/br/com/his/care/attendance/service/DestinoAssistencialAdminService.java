package br.com.his.care.attendance.service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.his.access.model.Unidade;
import br.com.his.access.repository.UnidadeRepository;
import br.com.his.care.attendance.dto.DestinoAssistencialAreaForm;
import br.com.his.care.attendance.dto.DestinoAssistencialForm;
import br.com.his.care.attendance.model.DestinoAssistencial;
import br.com.his.care.attendance.model.DestinoAssistencialArea;
import br.com.his.care.attendance.model.TipoDestinoAssistencial;
import br.com.his.care.attendance.repository.DestinoAssistencialAreaRepository;
import br.com.his.care.attendance.repository.DestinoAssistencialRepository;
import br.com.his.care.attendance.repository.TipoDestinoAssistencialRepository;
import br.com.his.care.inpatient.model.Area;
import br.com.his.care.inpatient.repository.AreaRepository;

@Service
public class DestinoAssistencialAdminService {

    private final DestinoAssistencialRepository repository;
    private final DestinoAssistencialAreaRepository destinoAreaRepository;
    private final UnidadeRepository unidadeRepository;
    private final TipoDestinoAssistencialRepository tipoDestinoAssistencialRepository;
    private final AreaRepository areaRepository;

    public DestinoAssistencialAdminService(DestinoAssistencialRepository repository,
                                           DestinoAssistencialAreaRepository destinoAreaRepository,
                                           UnidadeRepository unidadeRepository,
                                           TipoDestinoAssistencialRepository tipoDestinoAssistencialRepository,
                                           AreaRepository areaRepository) {
        this.repository = repository;
        this.destinoAreaRepository = destinoAreaRepository;
        this.unidadeRepository = unidadeRepository;
        this.tipoDestinoAssistencialRepository = tipoDestinoAssistencialRepository;
        this.areaRepository = areaRepository;
    }

    @Transactional(readOnly = true)
    public List<DestinoAssistencial> listar(String q) {
        String filtro = normalize(q);
        if (filtro == null) {
            return repository.listarTodosOrdenado();
        }
        return repository.buscarPorFiltro(filtro);
    }

    @Transactional(readOnly = true)
    public DestinoAssistencial buscar(Long id) {
        return repository.buscarComRelacionamentos(id)
                .orElseThrow(() -> new IllegalArgumentException("Destino assistencial nao encontrado"));
    }

    @Transactional
    public DestinoAssistencial criar(DestinoAssistencialForm form) {
        validarDuplicidade(form, null);
        DestinoAssistencial item = new DestinoAssistencial();
        apply(item, form);
        return repository.save(item);
    }

    @Transactional
    public DestinoAssistencial atualizar(Long id, DestinoAssistencialForm form) {
        validarDuplicidade(form, id);
        DestinoAssistencial item = buscar(id);
        apply(item, form);
        return repository.save(item);
    }

    @Transactional
    public void excluir(Long id) {
        DestinoAssistencial item = buscar(id);
        item.setAtivo(false);
        repository.save(item);
    }

    @Transactional(readOnly = true)
    public DestinoAssistencialForm toForm(DestinoAssistencial item) {
        DestinoAssistencialForm form = new DestinoAssistencialForm();
        form.setUnidadeId(item.getUnidade().getId());
        form.setTipoDestinoAssistencialId(item.getTipoDestinoAssistencial().getId());
        form.setCodigo(item.getCodigo());
        form.setDescricao(item.getDescricao());
        form.setObservacao(item.getObservacao());
        form.setOrdemExibicao(item.getOrdemExibicao());
        form.setAtivo(item.isAtivo());
        return form;
    }

    @Transactional(readOnly = true)
    public List<Area> listarAreasDisponiveis(Long destinoAssistencialId) {
        DestinoAssistencial destino = buscar(destinoAssistencialId);
        return areaRepository.findByUnidadeIdAndDtCancelamentoIsNullOrderByDescricaoAsc(destino.getUnidade().getId());
    }

    @Transactional(readOnly = true)
    public List<DestinoAssistencialArea> listarAreasVinculadas(Long destinoAssistencialId) {
        return destinoAreaRepository.listarPorDestinoOrdenado(destinoAssistencialId);
    }

    @Transactional(readOnly = true)
    public DestinoAssistencialAreaForm toAreaForm(Long destinoAssistencialId) {
        DestinoAssistencialAreaForm form = new DestinoAssistencialAreaForm();
        form.setAreaIds(destinoAreaRepository.listarPorDestinoOrdenado(destinoAssistencialId).stream()
                .filter(DestinoAssistencialArea::isAtivo)
                .map(item -> item.getArea().getId())
                .toList());
        return form;
    }

    @Transactional
    public void salvarAreas(Long destinoAssistencialId, DestinoAssistencialAreaForm form) {
        DestinoAssistencial destino = buscar(destinoAssistencialId);
        Set<Long> areaIds = new LinkedHashSet<>(form.getAreaIds() == null ? List.of() : form.getAreaIds());
        destinoAreaRepository.deleteByDestinoAssistencialId(destinoAssistencialId);
        if (areaIds.isEmpty()) {
            return;
        }
        int prioridade = 10;
        for (Long areaId : areaIds) {
            Area area = areaRepository.findByIdAndUnidadeIdAndDtCancelamentoIsNull(areaId, destino.getUnidade().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Area invalida para a unidade do destino"));
            DestinoAssistencialArea rel = new DestinoAssistencialArea();
            rel.setDestinoAssistencial(destino);
            rel.setArea(area);
            rel.setPrioridade(prioridade);
            rel.setAtivo(true);
            destinoAreaRepository.save(rel);
            prioridade += 10;
        }
    }

    private void validarDuplicidade(DestinoAssistencialForm form, Long idIgnorar) {
        Long unidadeId = form.getUnidadeId();
        Long tipoDestinoId = form.getTipoDestinoAssistencialId();
        String codigo = normalizeUpper(form.getCodigo());
        String descricao = normalizeUpper(form.getDescricao());

        repository.findDuplicadoCodigo(unidadeId, codigo, idIgnorar)
                .ifPresent(item -> {
                    throw new IllegalArgumentException("Ja existe destino assistencial com este codigo na unidade");
                });
        repository.findDuplicadoDescricao(unidadeId, tipoDestinoId, descricao, idIgnorar)
                .ifPresent(item -> {
                    throw new IllegalArgumentException("Ja existe destino assistencial com esta descricao para este tipo na unidade");
                });
    }

    private void apply(DestinoAssistencial item, DestinoAssistencialForm form) {
        Unidade unidade = unidadeRepository.findById(form.getUnidadeId())
                .orElseThrow(() -> new IllegalArgumentException("Unidade nao encontrada"));
        TipoDestinoAssistencial tipoDestino = tipoDestinoAssistencialRepository.findById(form.getTipoDestinoAssistencialId())
                .filter(TipoDestinoAssistencial::isAtivo)
                .orElseThrow(() -> new IllegalArgumentException("Tipo de destino assistencial invalido"));
        item.setUnidade(unidade);
        item.setTipoDestinoAssistencial(tipoDestino);
        item.setCodigo(normalizeUpper(form.getCodigo()));
        item.setDescricao(normalizeUpper(form.getDescricao()));
        item.setObservacao(normalize(form.getObservacao()));
        item.setOrdemExibicao(form.getOrdemExibicao() == null ? 100 : form.getOrdemExibicao());
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
        return normalized == null ? null : normalized.toUpperCase(Locale.ROOT);
    }
}
