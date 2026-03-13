package br.com.his.access.service;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.his.access.dto.ColaboradorUnidadeVinculoForm;
import br.com.his.access.model.Colaborador;
import br.com.his.access.model.ColaboradorUnidadeVinculo;
import br.com.his.access.model.TipoVinculoTrabalhista;
import br.com.his.access.model.Unidade;
import br.com.his.access.repository.ColaboradorRepository;
import br.com.his.access.repository.ColaboradorUnidadeVinculoRepository;
import br.com.his.access.repository.TipoVinculoTrabalhistaRepository;
import br.com.his.access.repository.UnidadeRepository;

@Service
public class ColaboradorUnidadeVinculoAdminService {

    private final ColaboradorUnidadeVinculoRepository repository;
    private final ColaboradorRepository colaboradorRepository;
    private final UnidadeRepository unidadeRepository;
    private final TipoVinculoTrabalhistaRepository tipoVinculoTrabalhistaRepository;

    public ColaboradorUnidadeVinculoAdminService(ColaboradorUnidadeVinculoRepository repository,
                                                 ColaboradorRepository colaboradorRepository,
                                                 UnidadeRepository unidadeRepository,
                                                 TipoVinculoTrabalhistaRepository tipoVinculoTrabalhistaRepository) {
        this.repository = repository;
        this.colaboradorRepository = colaboradorRepository;
        this.unidadeRepository = unidadeRepository;
        this.tipoVinculoTrabalhistaRepository = tipoVinculoTrabalhistaRepository;
    }

    @Transactional(readOnly = true)
    public List<ColaboradorUnidadeVinculo> listar(String q, Boolean ativo) {
        String filtro = normalize(q);
        if (filtro == null) {
            return ativo == null
                    ? repository.findAllComDetalhesOrderByColaboradorUnidadeAsc()
                    : repository.findByAtivoComDetalhesOrderByColaboradorUnidadeAsc(ativo);
        }
        return ativo == null
                ? repository.listarPorBusca(filtro)
                : repository.listarPorFiltroComBusca(ativo, filtro);
    }

    @Transactional(readOnly = true)
    public List<ColaboradorUnidadeVinculo> listarAtivos() {
        return repository.findAtivosComDetalhesOrderByColaboradorUnidadeAsc();
    }

    @Transactional(readOnly = true)
    public ColaboradorUnidadeVinculo buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vinculo de unidade nao encontrado"));
    }

    @Transactional
    public ColaboradorUnidadeVinculo criar(ColaboradorUnidadeVinculoForm form) {
        validarVigencia(form.getInicioVigencia(), form.getFimVigencia());
        validarVinculoDuplicado(form.getColaboradorId(), form.getUnidadeId(), null);
        ColaboradorUnidadeVinculo item = new ColaboradorUnidadeVinculo();
        apply(item, form);
        return repository.save(item);
    }

    @Transactional
    public ColaboradorUnidadeVinculo atualizar(Long id, ColaboradorUnidadeVinculoForm form) {
        validarVigencia(form.getInicioVigencia(), form.getFimVigencia());
        validarVinculoDuplicado(form.getColaboradorId(), form.getUnidadeId(), id);
        ColaboradorUnidadeVinculo item = buscar(id);
        apply(item, form);
        return repository.save(item);
    }

    @Transactional
    public void excluir(Long id) {
        ColaboradorUnidadeVinculo item = buscar(id);
        try {
            repository.delete(item);
            repository.flush();
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("Nao foi possivel excluir: vinculo possui atuacoes associadas");
        }
    }

    @Transactional(readOnly = true)
    public ColaboradorUnidadeVinculoForm toForm(ColaboradorUnidadeVinculo item) {
        ColaboradorUnidadeVinculoForm form = new ColaboradorUnidadeVinculoForm();
        form.setColaboradorId(item.getColaborador().getId());
        form.setUnidadeId(item.getUnidade().getId());
        form.setTipoVinculoTrabalhistaId(item.getTipoVinculoTrabalhista() == null
                ? null
                : item.getTipoVinculoTrabalhista().getId());
        form.setInicioVigencia(item.getInicioVigencia());
        form.setFimVigencia(item.getFimVigencia());
        form.setAtivo(item.isAtivo());
        return form;
    }

    private void validarVinculoDuplicado(Long colaboradorId, Long unidadeId, Long idIgnorar) {
        repository.findByColaboradorIdAndUnidadeId(colaboradorId, unidadeId)
                .filter(existente -> idIgnorar == null || !existente.getId().equals(idIgnorar))
                .ifPresent(existente -> {
                    throw new IllegalArgumentException("Ja existe vinculo deste colaborador para a unidade");
                });
    }

    private void apply(ColaboradorUnidadeVinculo item, ColaboradorUnidadeVinculoForm form) {
        item.setColaborador(resolveColaborador(form.getColaboradorId()));
        item.setUnidade(resolveUnidade(form.getUnidadeId()));
        item.setTipoVinculoTrabalhista(resolveTipoVinculoTrabalhista(form.getTipoVinculoTrabalhistaId()));
        item.setInicioVigencia(form.getInicioVigencia());
        item.setFimVigencia(form.getFimVigencia());
        item.setAtivo(form.isAtivo());
    }

    private Colaborador resolveColaborador(Long colaboradorId) {
        return colaboradorRepository.findById(colaboradorId)
                .orElseThrow(() -> new IllegalArgumentException("Colaborador nao encontrado"));
    }

    private Unidade resolveUnidade(Long unidadeId) {
        return unidadeRepository.findById(unidadeId)
                .orElseThrow(() -> new IllegalArgumentException("Unidade nao encontrada"));
    }

    private TipoVinculoTrabalhista resolveTipoVinculoTrabalhista(Long tipoVinculoTrabalhistaId) {
        if (tipoVinculoTrabalhistaId == null) {
            return null;
        }
        return tipoVinculoTrabalhistaRepository.findById(tipoVinculoTrabalhistaId)
                .orElseThrow(() -> new IllegalArgumentException("Tipo de vinculo trabalhista nao encontrado"));
    }

    private static void validarVigencia(java.time.LocalDate inicio, java.time.LocalDate fim) {
        if (inicio != null && fim != null && fim.isBefore(inicio)) {
            throw new IllegalArgumentException("Fim da vigencia nao pode ser anterior ao inicio");
        }
    }

    private static String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

}
