package br.com.his.patient.service;

import java.text.Normalizer;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.his.access.model.Unidade;
import br.com.his.access.repository.UnidadeRepository;
import br.com.his.patient.dto.ProcedenciaAdminForm;
import br.com.his.patient.model.lookup.Procedencia;
import br.com.his.patient.model.lookup.TipoProcedencia;
import br.com.his.patient.repository.ProcedenciaRepository;
import br.com.his.patient.repository.TipoProcedenciaRepository;
import br.com.his.reference.location.model.Bairro;
import br.com.his.reference.location.model.Municipio;
import br.com.his.reference.location.model.UnidadeFederativa;
import br.com.his.reference.location.repository.BairroRepository;
import br.com.his.reference.location.repository.MunicipioRepository;
import br.com.his.reference.location.repository.UnidadeFederativaRepository;

@Service
public class ProcedenciaAdminService {

    private enum TipoCampo {
        CATALOGO,
        BAIRRO,
        MUNICIPIO,
        OUTROS
    }

    private final ProcedenciaRepository procedenciaRepository;
    private final TipoProcedenciaRepository tipoProcedenciaRepository;
    private final UnidadeRepository unidadeRepository;
    private final UnidadeFederativaRepository unidadeFederativaRepository;
    private final MunicipioRepository municipioRepository;
    private final BairroRepository bairroRepository;

    public ProcedenciaAdminService(ProcedenciaRepository procedenciaRepository,
                                   TipoProcedenciaRepository tipoProcedenciaRepository,
                                   UnidadeRepository unidadeRepository,
                                   UnidadeFederativaRepository unidadeFederativaRepository,
                                   MunicipioRepository municipioRepository,
                                   BairroRepository bairroRepository) {
        this.procedenciaRepository = procedenciaRepository;
        this.tipoProcedenciaRepository = tipoProcedenciaRepository;
        this.unidadeRepository = unidadeRepository;
        this.unidadeFederativaRepository = unidadeFederativaRepository;
        this.municipioRepository = municipioRepository;
        this.bairroRepository = bairroRepository;
    }

    @Transactional(readOnly = true)
    public List<Procedencia> listar(String q, Boolean ativo) {
        String filtro = normalize(q);
        if (filtro == null) {
            return ativo == null
                    ? procedenciaRepository.findAllByOrderByDescricaoAsc()
                    : procedenciaRepository.findByAtivoOrderByDescricaoAsc(ativo);
        }
        return ativo == null
                ? procedenciaRepository.buscarPorFiltro(filtro)
                : procedenciaRepository.buscarPorFiltroComAtivo(filtro, ativo);
    }

    @Transactional(readOnly = true)
    public Procedencia buscar(Long id) {
        return procedenciaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Procedencia nao encontrada"));
    }

    @Transactional
    public Procedencia criar(ProcedenciaAdminForm form) {
        Procedencia procedencia = new Procedencia();
        apply(procedencia, form, null);
        return procedenciaRepository.save(procedencia);
    }

    @Transactional
    public Procedencia atualizar(Long id, ProcedenciaAdminForm form) {
        Procedencia procedencia = buscar(id);
        apply(procedencia, form, id);
        return procedenciaRepository.save(procedencia);
    }

    @Transactional
    public void excluir(Long id) {
        Procedencia procedencia = buscar(id);
        try {
            procedenciaRepository.delete(procedencia);
            procedenciaRepository.flush();
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("Nao foi possivel excluir: procedencia em uso");
        }
    }

    @Transactional(readOnly = true)
    public ProcedenciaAdminForm toForm(Procedencia procedencia) {
        ProcedenciaAdminForm form = new ProcedenciaAdminForm();
        form.setTipoProcedenciaId(procedencia.getTipoProcedencia() == null ? null : procedencia.getTipoProcedencia().getId());
        form.setUnidadeId(procedencia.getUnidade() == null ? null : procedencia.getUnidade().getId());
        form.setDescricao(procedencia.getDescricao());
        form.setAtivo(procedencia.isAtivo());
        if (procedencia.getMunicipio() != null) {
            form.setMunicipioId(procedencia.getMunicipio().getId());
            if (procedencia.getMunicipio().getUnidadeFederativa() != null) {
                form.setUnidadeFederativaId(procedencia.getMunicipio().getUnidadeFederativa().getId());
            }
        }
        if (procedencia.getBairro() != null) {
            form.setBairroId(procedencia.getBairro().getId());
            if (procedencia.getBairro().getMunicipio() != null) {
                form.setMunicipioId(procedencia.getBairro().getMunicipio().getId());
                if (procedencia.getBairro().getMunicipio().getUnidadeFederativa() != null) {
                    form.setUnidadeFederativaId(procedencia.getBairro().getMunicipio().getUnidadeFederativa().getId());
                }
            }
        }
        return form;
    }

    @Transactional(readOnly = true)
    public List<TipoProcedencia> listarTiposProcedencia() {
        return tipoProcedenciaRepository.findAll().stream()
                .sorted(Comparator.comparing(TipoProcedencia::getDescricao, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Unidade> listarUnidadesAtivas() {
        return unidadeRepository.findByAtivoTrueOrderByNomeAsc();
    }

    @Transactional(readOnly = true)
    public List<UnidadeFederativa> listarUfs() {
        return unidadeFederativaRepository.findAllByOrderByDescricaoAsc();
    }

    @Transactional(readOnly = true)
    public List<Municipio> listarMunicipiosPorUf(Long unidadeFederativaId) {
        if (unidadeFederativaId == null) {
            return List.of();
        }
        return municipioRepository.findByUnidadeFederativaIdOrderByNome(unidadeFederativaId);
    }

    @Transactional(readOnly = true)
    public List<Bairro> listarBairrosPorMunicipio(Long municipioId) {
        if (municipioId == null) {
            return List.of();
        }
        return bairroRepository.findAtivosByMunicipioIdOrderByNome(municipioId);
    }

    @Transactional(readOnly = true)
    public Map<Long, String> mapearCampoPorTipo() {
        return listarTiposProcedencia().stream()
                .collect(java.util.stream.Collectors.toMap(TipoProcedencia::getId, tipo -> resolveTipoCampo(tipo).name()));
    }

    private void apply(Procedencia procedencia, ProcedenciaAdminForm form, Long idIgnorar) {
        TipoProcedencia tipoProcedencia = tipoProcedenciaRepository.findById(form.getTipoProcedenciaId())
                .orElseThrow(() -> new IllegalArgumentException("Tipo de procedencia nao encontrado"));
        Unidade unidade = resolveUnidade(form.getUnidadeId());
        Long unidadeId = unidade == null ? null : unidade.getId();

        TipoCampo tipoCampo = resolveTipoCampo(tipoProcedencia);
        procedencia.setTipoProcedencia(tipoProcedencia);
        procedencia.setUnidade(unidade);
        procedencia.setAtivo(form.isAtivo());

        switch (tipoCampo) {
            case BAIRRO -> aplicarBairro(procedencia, form, idIgnorar, tipoProcedencia.getId(), unidadeId);
            case MUNICIPIO -> aplicarMunicipio(procedencia, form, idIgnorar, tipoProcedencia.getId(), unidadeId);
            case OUTROS, CATALOGO -> throw new IllegalArgumentException(
                    "Tipo de procedencia nao suportado para cadastro de procedencia. Utilize CIDADE/MUNICIPIO ou BAIRRO");
        }
    }

    private void aplicarBairro(Procedencia procedencia,
                               ProcedenciaAdminForm form,
                               Long idIgnorar,
                               Long tipoProcedenciaId,
                               Long unidadeId) {
        if (form.getBairroId() == null) {
            throw new IllegalArgumentException("Bairro e obrigatorio para este tipo de procedencia");
        }
        Bairro bairro = bairroRepository.findById(form.getBairroId())
                .orElseThrow(() -> new IllegalArgumentException("Bairro nao encontrado"));
        if (form.getMunicipioId() != null && bairro.getMunicipio() != null
                && !bairro.getMunicipio().getId().equals(form.getMunicipioId())) {
            throw new IllegalArgumentException("Bairro nao pertence ao municipio informado");
        }
        if (procedenciaRepository.existsDuplicadaPorTipoEBairro(tipoProcedenciaId, bairro.getId(), unidadeId, idIgnorar)) {
            throw new IllegalArgumentException("Ja existe procedencia para este bairro nesta unidade");
        }
        procedencia.setBairro(bairro);
        procedencia.setMunicipio(null);
        procedencia.setDescricao(normalizeUpper(bairro.getNome()));
    }

    private void aplicarMunicipio(Procedencia procedencia,
                                  ProcedenciaAdminForm form,
                                  Long idIgnorar,
                                  Long tipoProcedenciaId,
                                  Long unidadeId) {
        if (form.getMunicipioId() == null) {
            throw new IllegalArgumentException("Municipio e obrigatorio para este tipo de procedencia");
        }
        Municipio municipio = municipioRepository.findById(form.getMunicipioId())
                .orElseThrow(() -> new IllegalArgumentException("Municipio nao encontrado"));
        if (form.getUnidadeFederativaId() != null && municipio.getUnidadeFederativa() != null
                && !municipio.getUnidadeFederativa().getId().equals(form.getUnidadeFederativaId())) {
            throw new IllegalArgumentException("Municipio nao pertence a UF informada");
        }
        if (procedenciaRepository.existsDuplicadaPorTipoEMunicipio(tipoProcedenciaId, municipio.getId(), unidadeId, idIgnorar)) {
            throw new IllegalArgumentException("Ja existe procedencia para este municipio nesta unidade");
        }
        procedencia.setBairro(null);
        procedencia.setMunicipio(municipio);
        procedencia.setDescricao(normalizeUpper(municipio.getNome()));
    }

    private Unidade resolveUnidade(Long unidadeId) {
        if (unidadeId == null) {
            return null;
        }
        return unidadeRepository.findById(unidadeId)
                .orElseThrow(() -> new IllegalArgumentException("Unidade nao encontrada"));
    }

    private TipoCampo resolveTipoCampo(TipoProcedencia tipoProcedencia) {
        String normalized = normalizeComparable(tipoProcedencia == null ? null : tipoProcedencia.getDescricao());
        if (normalized.contains("BAIRRO")) {
            return TipoCampo.BAIRRO;
        }
        if (normalized.contains("MUNICIPIO") || normalized.contains("CIDADE")) {
            return TipoCampo.MUNICIPIO;
        }
        if (normalized.contains("OUTRO")) {
            return TipoCampo.OUTROS;
        }
        return TipoCampo.CATALOGO;
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

    private static String normalizeComparable(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        String ascii = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "");
        return ascii.trim().toUpperCase(Locale.ROOT);
    }
}
