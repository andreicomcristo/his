package br.com.his.access.service;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.his.access.dto.ColaboradorForm;
import br.com.his.access.model.CargoColaborador;
import br.com.his.access.model.Colaborador;
import br.com.his.access.repository.CargoColaboradorRepository;
import br.com.his.access.repository.ColaboradorRepository;

@Service
public class ColaboradorAdminService {

    private final ColaboradorRepository repository;
    private final CargoColaboradorRepository cargoColaboradorRepository;

    public ColaboradorAdminService(ColaboradorRepository repository,
                                   CargoColaboradorRepository cargoColaboradorRepository) {
        this.repository = repository;
        this.cargoColaboradorRepository = cargoColaboradorRepository;
    }

    @Transactional(readOnly = true)
    public List<Colaborador> listar(String q, Boolean ativo) {
        String filtro = normalize(q);
        if (filtro == null) {
            return ativo == null
                    ? repository.findAllComCargoOrderByNomeAsc()
                    : repository.findByAtivoComCargoOrderByNomeAsc(ativo);
        }
        return ativo == null
                ? repository.listarPorBusca(filtro)
                : repository.listarPorFiltroComBusca(ativo, filtro);
    }

    @Transactional(readOnly = true)
    public List<Colaborador> listarAtivos() {
        return repository.findAtivosOrderByNomeAsc();
    }

    @Transactional(readOnly = true)
    public Colaborador buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Colaborador nao encontrado"));
    }

    @Transactional
    public Colaborador criar(ColaboradorForm form) {
        validarCpfDuplicado(form.getCpf(), null);
        Colaborador item = new Colaborador();
        apply(item, form);
        return repository.save(item);
    }

    @Transactional
    public Colaborador atualizar(Long id, ColaboradorForm form) {
        validarCpfDuplicado(form.getCpf(), id);
        Colaborador item = buscar(id);
        apply(item, form);
        return repository.save(item);
    }

    @Transactional
    public void excluir(Long id) {
        Colaborador item = buscar(id);
        try {
            repository.delete(item);
            repository.flush();
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("Nao foi possivel excluir: colaborador em uso por vinculos/atuacoes");
        }
    }

    @Transactional(readOnly = true)
    public ColaboradorForm toForm(Colaborador item) {
        ColaboradorForm form = new ColaboradorForm();
        form.setNome(item.getNome());
        form.setCpf(item.getCpf());
        form.setCargoColaboradorId(item.getCargoColaborador() == null ? null : item.getCargoColaborador().getId());
        form.setAtivo(item.isAtivo());
        return form;
    }

    private void validarCpfDuplicado(String cpf, Long idIgnorar) {
        String normalized = normalizeUpper(cpf);
        if (normalized == null) {
            return;
        }
        repository.findByCpfIgnoreCase(normalized)
                .filter(existente -> idIgnorar == null || !existente.getId().equals(idIgnorar))
                .ifPresent(existente -> {
                    throw new IllegalArgumentException("Ja existe colaborador com este CPF");
                });
    }

    private void apply(Colaborador item, ColaboradorForm form) {
        item.setNome(normalizeUpper(form.getNome()));
        item.setCpf(normalizeUpper(form.getCpf()));
        item.setCargoColaborador(resolveCargo(form.getCargoColaboradorId()));
        item.setAtivo(form.isAtivo());
    }

    private CargoColaborador resolveCargo(Long cargoColaboradorId) {
        if (cargoColaboradorId == null) {
            return null;
        }
        return cargoColaboradorRepository.findById(cargoColaboradorId)
                .orElseThrow(() -> new IllegalArgumentException("Cargo de colaborador nao encontrado"));
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
