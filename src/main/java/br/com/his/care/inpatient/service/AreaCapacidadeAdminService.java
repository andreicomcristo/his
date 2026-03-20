package br.com.his.care.inpatient.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.his.care.inpatient.dto.AreaCapacidadeForm;
import br.com.his.care.inpatient.model.Area;
import br.com.his.care.inpatient.model.AreaCapacidade;
import br.com.his.care.inpatient.model.CapacidadeArea;
import br.com.his.care.inpatient.repository.AreaCapacidadeRepository;
import br.com.his.care.inpatient.repository.AreaRepository;
import br.com.his.care.inpatient.repository.CapacidadeAreaRepository;

@Service
public class AreaCapacidadeAdminService {

    private final AreaRepository areaRepository;
    private final CapacidadeAreaRepository capacidadeAreaRepository;
    private final AreaCapacidadeRepository areaCapacidadeRepository;

    public AreaCapacidadeAdminService(AreaRepository areaRepository,
                                      CapacidadeAreaRepository capacidadeAreaRepository,
                                      AreaCapacidadeRepository areaCapacidadeRepository) {
        this.areaRepository = areaRepository;
        this.capacidadeAreaRepository = capacidadeAreaRepository;
        this.areaCapacidadeRepository = areaCapacidadeRepository;
    }

    @Transactional(readOnly = true)
    public Area buscarArea(Long areaId) {
        return areaRepository.findByIdAndDtCancelamentoIsNull(areaId)
                .orElseThrow(() -> new IllegalArgumentException("Area nao encontrada"));
    }

    @Transactional(readOnly = true)
    public AreaCapacidadeForm toForm(Long areaId) {
        AreaCapacidadeForm form = new AreaCapacidadeForm();
        form.setCapacidadeIds(areaCapacidadeRepository.findByAreaIdOrderByCapacidadeAreaNomeAsc(areaId)
                .stream()
                .map(rel -> rel.getCapacidadeArea().getId())
                .toList());
        return form;
    }

    @Transactional
    public void salvar(Long areaId, AreaCapacidadeForm form) {
        Area area = buscarArea(areaId);
        Set<Long> ids = new HashSet<>(form.getCapacidadeIds() == null ? List.of() : form.getCapacidadeIds());
        areaCapacidadeRepository.deleteByAreaId(areaId);
        if (ids.isEmpty()) {
            return;
        }
        List<CapacidadeArea> capacidades = capacidadeAreaRepository.findAllById(ids);
        for (CapacidadeArea capacidade : capacidades) {
            AreaCapacidade rel = new AreaCapacidade();
            rel.setArea(area);
            rel.setCapacidadeArea(capacidade);
            areaCapacidadeRepository.save(rel);
        }
    }
}


