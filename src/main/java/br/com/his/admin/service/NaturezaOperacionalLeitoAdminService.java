package br.com.his.admin.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.his.admin.dto.NaturezaOperacionalLeitoForm;
import br.com.his.care.inpatient.model.NaturezaOperacionalLeito;
import br.com.his.care.inpatient.repository.LeitoRepository;
import br.com.his.care.inpatient.repository.NaturezaOperacionalLeitoRepository;

@Service
public class NaturezaOperacionalLeitoAdminService {

    private final NaturezaOperacionalLeitoRepository repository;
    private final LeitoRepository leitoRepository;

    public NaturezaOperacionalLeitoAdminService(NaturezaOperacionalLeitoRepository repository,
                                                LeitoRepository leitoRepository) {
        this.repository = repository;
        this.leitoRepository = leitoRepository;
    }

    @Transactional(readOnly = true)
    public List<NaturezaOperacionalLeito> listar(String q) {
        String filtro = normalize(q);
        if (filtro == null) {
            return repository.findAllByOrderByDescricaoAsc();
        }
        return repository.buscarPorFiltro(filtro);
    }

    @Transactional(readOnly = true)
    public NaturezaOperacionalLeito buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Natureza operacional nao encontrada"));
    }

    @Transactional
    public NaturezaOperacionalLeito criar(NaturezaOperacionalLeitoForm form) {
        validarCodigoDuplicado(form.getCodigo(), null);
        NaturezaOperacionalLeito item = new NaturezaOperacionalLeito();
        apply(item, form);
        return repository.save(item);
    }

    @Transactional
    public NaturezaOperacionalLeito atualizar(Long id, NaturezaOperacionalLeitoForm form) {
        validarCodigoDuplicado(form.getCodigo(), id);
        if (!form.isAtivo() && leitoRepository.existsByNaturezaOperacionalId(id)) {
            throw new IllegalArgumentException("Nao e possivel inativar: existe leito usando esta natureza operacional");
        }
        NaturezaOperacionalLeito item = buscar(id);
        apply(item, form);
        return repository.save(item);
    }

    @Transactional
    public void excluir(Long id) {
        NaturezaOperacionalLeito item = buscar(id);
        if (leitoRepository.existsByNaturezaOperacionalId(id)) {
            throw new IllegalArgumentException("Nao e possivel excluir: existe leito usando esta natureza operacional");
        }
        repository.delete(item);
    }

    @Transactional(readOnly = true)
    public NaturezaOperacionalLeitoForm toForm(NaturezaOperacionalLeito item) {
        NaturezaOperacionalLeitoForm form = new NaturezaOperacionalLeitoForm();
        form.setCodigo(item.getCodigo());
        form.setDescricao(item.getDescricao());
        form.setConsideraTaxaNominal(item.isConsideraTaxaNominal());
        form.setConsideraTaxaOperacional(item.isConsideraTaxaOperacional());
        form.setVirtualSuperlotacao(item.isVirtualSuperlotacao());
        form.setAtivo(item.isAtivo());
        return form;
    }

    private void apply(NaturezaOperacionalLeito item, NaturezaOperacionalLeitoForm form) {
        item.setCodigo(normalizeUpper(form.getCodigo()));
        item.setDescricao(normalizeUpper(form.getDescricao()));
        item.setConsideraTaxaNominal(form.isConsideraTaxaNominal());
        item.setConsideraTaxaOperacional(form.isConsideraTaxaOperacional());
        item.setVirtualSuperlotacao(form.isVirtualSuperlotacao());
        item.setAtivo(form.isAtivo());
    }

    private void validarCodigoDuplicado(String codigo, Long ignoreId) {
        String normalizedCodigo = normalizeUpper(codigo);
        if (normalizedCodigo == null) {
            return;
        }
        boolean duplicado = ignoreId == null
                ? repository.existsByCodigoIgnoreCase(normalizedCodigo)
                : repository.existsByCodigoIgnoreCaseAndIdNot(normalizedCodigo, ignoreId);
        if (duplicado) {
            throw new IllegalArgumentException("Ja existe natureza operacional com este codigo");
        }
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
