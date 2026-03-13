package br.com.his.care.scheduling.service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.his.access.model.CargoColaborador;
import br.com.his.access.repository.CargoColaboradorRepository;
import br.com.his.care.scheduling.dto.EspecialidadeForm;
import br.com.his.care.scheduling.model.CargoColaboradorEspecialidade;
import br.com.his.care.scheduling.model.Especialidade;
import br.com.his.care.scheduling.repository.CargoColaboradorEspecialidadeRepository;
import br.com.his.care.scheduling.repository.EspecialidadeRepository;

@Service
public class EspecialidadeAdminService {

    private final EspecialidadeRepository repository;
    private final CargoColaboradorRepository cargoColaboradorRepository;
    private final CargoColaboradorEspecialidadeRepository cargoColaboradorEspecialidadeRepository;

    public EspecialidadeAdminService(EspecialidadeRepository repository,
                                     CargoColaboradorRepository cargoColaboradorRepository,
                                     CargoColaboradorEspecialidadeRepository cargoColaboradorEspecialidadeRepository) {
        this.repository = repository;
        this.cargoColaboradorRepository = cargoColaboradorRepository;
        this.cargoColaboradorEspecialidadeRepository = cargoColaboradorEspecialidadeRepository;
    }

    @Transactional(readOnly = true)
    public List<Especialidade> listar(String q, Boolean ativo) {
        String filtro = normalize(q);
        if (filtro == null) {
            return ativo == null
                    ? repository.findAllByOrderByDescricaoAsc()
                    : repository.findByAtivoOrderByDescricaoAsc(ativo);
        }
        return ativo == null
                ? repository.listarPorBusca(filtro)
                : repository.listarPorFiltroComBusca(ativo, filtro);
    }

    @Transactional(readOnly = true)
    public List<Especialidade> listarAtivas() {
        return repository.findByAtivoTrueOrderByDescricaoAsc();
    }

    @Transactional(readOnly = true)
    public Especialidade buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Especialidade nao encontrada"));
    }

    @Transactional
    public Especialidade criar(EspecialidadeForm form) {
        validarCodigoDuplicado(form.getCodigo(), null);
        Especialidade item = new Especialidade();
        apply(item, form);
        Especialidade salvo = repository.save(item);
        sincronizarCargosAssistenciais(salvo, form.getCargoColaboradorIds());
        return salvo;
    }

    @Transactional
    public Especialidade atualizar(Long id, EspecialidadeForm form) {
        validarCodigoDuplicado(form.getCodigo(), id);
        Especialidade item = buscar(id);
        apply(item, form);
        Especialidade salvo = repository.save(item);
        sincronizarCargosAssistenciais(salvo, form.getCargoColaboradorIds());
        return salvo;
    }

    @Transactional
    public void excluir(Long id) {
        cargoColaboradorEspecialidadeRepository.deleteByEspecialidadeId(id);
        repository.delete(buscar(id));
    }

    @Transactional(readOnly = true)
    public EspecialidadeForm toForm(Especialidade item) {
        EspecialidadeForm form = new EspecialidadeForm();
        form.setCodigo(item.getCodigo());
        form.setDescricao(item.getDescricao());
        form.setCargoColaboradorIds(cargoColaboradorEspecialidadeRepository.listarCargoIdsAtivosPorEspecialidade(item.getId()));
        form.setAtivo(item.isAtivo());
        return form;
    }

    private void validarCodigoDuplicado(String codigo, Long idIgnorar) {
        String normalized = normalizeUpper(codigo);
        repository.findByCodigoIgnoreCase(normalized)
                .filter(existente -> idIgnorar == null || !existente.getId().equals(idIgnorar))
                .ifPresent(existente -> {
                    throw new IllegalArgumentException("Ja existe especialidade com este codigo");
                });
    }

    private void apply(Especialidade item, EspecialidadeForm form) {
        item.setCodigo(normalizeUpper(form.getCodigo()));
        item.setDescricao(normalizeUpper(form.getDescricao()));
        item.setAtivo(form.isAtivo());
    }

    private void sincronizarCargosAssistenciais(Especialidade especialidade, List<Long> cargoIds) {
        Set<Long> idsSelecionados = normalizarIds(cargoIds);
        List<CargoColaboradorEspecialidade> vinculos = cargoColaboradorEspecialidadeRepository
                .findByEspecialidadeIdWithCargo(especialidade.getId());

        Set<Long> idsRemanescentes = new LinkedHashSet<>(idsSelecionados);
        for (CargoColaboradorEspecialidade vinculo : vinculos) {
            Long cargoId = vinculo.getCargoColaborador().getId();
            boolean deveFicarAtivo = idsRemanescentes.remove(cargoId);
            if (vinculo.isAtivo() != deveFicarAtivo) {
                vinculo.setAtivo(deveFicarAtivo);
                cargoColaboradorEspecialidadeRepository.save(vinculo);
            }
        }

        for (Long cargoId : idsRemanescentes) {
            CargoColaborador cargo = resolveCargoAssistencialAtivo(cargoId);
            CargoColaboradorEspecialidade novo = new CargoColaboradorEspecialidade();
            novo.setCargoColaborador(cargo);
            novo.setEspecialidade(especialidade);
            novo.setAtivo(true);
            cargoColaboradorEspecialidadeRepository.save(novo);
        }
    }

    private CargoColaborador resolveCargoAssistencialAtivo(Long cargoId) {
        CargoColaborador cargo = cargoColaboradorRepository.findById(cargoId)
                .orElseThrow(() -> new IllegalArgumentException("Cargo de colaborador nao encontrado"));
        if (!cargo.isAtivo() || cargo.getTipoCargo() == null || !cargo.getTipoCargo().isAtivo()
                || cargo.getTipoCargo().getCodigo() == null
                || !"ASSISTENCIAL".equalsIgnoreCase(cargo.getTipoCargo().getCodigo())) {
            throw new IllegalArgumentException("Somente cargos assistenciais ativos podem ser vinculados a especialidade");
        }
        return cargo;
    }

    private static Set<Long> normalizarIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Set.of();
        }
        LinkedHashSet<Long> normalized = new LinkedHashSet<>();
        for (Long id : ids) {
            if (Objects.nonNull(id)) {
                normalized.add(id);
            }
        }
        return normalized;
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
