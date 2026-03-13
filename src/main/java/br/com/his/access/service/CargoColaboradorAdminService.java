package br.com.his.access.service;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.his.access.dto.CargoColaboradorForm;
import br.com.his.access.model.CargoColaborador;
import br.com.his.access.model.TipoCargo;
import br.com.his.access.repository.CargoColaboradorRepository;
import br.com.his.access.repository.TipoCargoRepository;

@Service
public class CargoColaboradorAdminService {

    private final CargoColaboradorRepository repository;
    private final TipoCargoRepository tipoCargoRepository;

    public CargoColaboradorAdminService(CargoColaboradorRepository repository,
                                        TipoCargoRepository tipoCargoRepository) {
        this.repository = repository;
        this.tipoCargoRepository = tipoCargoRepository;
    }

    @Transactional(readOnly = true)
    public List<CargoColaborador> listar(String q, Boolean ativo) {
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
    public CargoColaborador buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cargo do colaborador nao encontrado"));
    }

    @Transactional
    public CargoColaborador criar(CargoColaboradorForm form) {
        validarCodigoDuplicado(form.getCodigo(), null);
        CargoColaborador item = new CargoColaborador();
        apply(item, form);
        return repository.save(item);
    }

    @Transactional
    public CargoColaborador atualizar(Long id, CargoColaboradorForm form) {
        validarCodigoDuplicado(form.getCodigo(), id);
        CargoColaborador item = buscar(id);
        apply(item, form);
        return repository.save(item);
    }

    @Transactional
    public void excluir(Long id) {
        CargoColaborador item = buscar(id);
        try {
            repository.delete(item);
            repository.flush();
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("Nao foi possivel excluir: cargo em uso por colaborador");
        }
    }

    @Transactional(readOnly = true)
    public CargoColaboradorForm toForm(CargoColaborador item) {
        CargoColaboradorForm form = new CargoColaboradorForm();
        form.setCodigo(item.getCodigo());
        form.setDescricao(item.getDescricao());
        form.setTipoCargoId(item.getTipoCargo().getId());
        form.setExigeEspecialidadeAgendamento(item.isExigeEspecialidadeAgendamento());
        form.setAtivo(item.isAtivo());
        return form;
    }

    private void validarCodigoDuplicado(String codigo, Long idIgnorar) {
        String normalized = normalizeUpper(codigo);
        repository.findByCodigoIgnoreCase(normalized)
                .filter(existente -> idIgnorar == null || !existente.getId().equals(idIgnorar))
                .ifPresent(existente -> {
                    throw new IllegalArgumentException("Ja existe cargo com este codigo");
                });
    }

    private void apply(CargoColaborador item, CargoColaboradorForm form) {
        item.setCodigo(normalizeUpper(form.getCodigo()));
        item.setDescricao(normalizeUpper(form.getDescricao()));
        TipoCargo tipoCargo = resolveTipoCargo(form.getTipoCargoId());
        item.setTipoCargo(tipoCargo);
        boolean assistencial = tipoCargo.getCodigo() != null
                && "ASSISTENCIAL".equalsIgnoreCase(tipoCargo.getCodigo());
        item.setExigeEspecialidadeAgendamento(assistencial && form.isExigeEspecialidadeAgendamento());
        item.setAtivo(form.isAtivo());
    }

    private TipoCargo resolveTipoCargo(Long tipoCargoId) {
        return tipoCargoRepository.findById(tipoCargoId)
                .orElseThrow(() -> new IllegalArgumentException("Tipo de cargo nao encontrado"));
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
