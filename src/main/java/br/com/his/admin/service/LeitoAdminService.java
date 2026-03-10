package br.com.his.admin.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.his.access.model.Unidade;
import br.com.his.access.repository.UnidadeRepository;
import br.com.his.admin.dto.LeitoForm;
import br.com.his.care.inpatient.model.Area;
import br.com.his.care.inpatient.model.Leito;
import br.com.his.care.inpatient.model.LeitoModalidade;
import br.com.his.care.inpatient.model.LeitoModalidadeTipo;
import br.com.his.care.inpatient.model.NaturezaOperacionalLeito;
import br.com.his.care.inpatient.model.PerfilLeito;
import br.com.his.care.inpatient.model.TipoLeito;
import br.com.his.care.inpatient.repository.AreaCapacidadeRepository;
import br.com.his.care.inpatient.repository.AreaRepository;
import br.com.his.care.inpatient.repository.LeitoModalidadeRepository;
import br.com.his.care.inpatient.repository.LeitoModalidadeTipoRepository;
import br.com.his.care.inpatient.repository.LeitoRepository;
import br.com.his.care.inpatient.repository.NaturezaOperacionalLeitoRepository;
import br.com.his.care.inpatient.repository.PerfilLeitoRepository;
import br.com.his.care.inpatient.repository.TipoLeitoRepository;

@Service
public class LeitoAdminService {

    private final LeitoRepository leitoRepository;
    private final UnidadeRepository unidadeRepository;
    private final AreaRepository areaRepository;
    private final AreaCapacidadeRepository areaCapacidadeRepository;
    private final TipoLeitoRepository tipoLeitoRepository;
    private final PerfilLeitoRepository perfilLeitoRepository;
    private final NaturezaOperacionalLeitoRepository naturezaOperacionalLeitoRepository;
    private final LeitoModalidadeTipoRepository leitoModalidadeTipoRepository;
    private final LeitoModalidadeRepository leitoModalidadeRepository;

    public LeitoAdminService(LeitoRepository leitoRepository,
                             UnidadeRepository unidadeRepository,
                             AreaRepository areaRepository,
                             AreaCapacidadeRepository areaCapacidadeRepository,
                             TipoLeitoRepository tipoLeitoRepository,
                             PerfilLeitoRepository perfilLeitoRepository,
                             NaturezaOperacionalLeitoRepository naturezaOperacionalLeitoRepository,
                             LeitoModalidadeTipoRepository leitoModalidadeTipoRepository,
                             LeitoModalidadeRepository leitoModalidadeRepository) {
        this.leitoRepository = leitoRepository;
        this.unidadeRepository = unidadeRepository;
        this.areaRepository = areaRepository;
        this.areaCapacidadeRepository = areaCapacidadeRepository;
        this.tipoLeitoRepository = tipoLeitoRepository;
        this.perfilLeitoRepository = perfilLeitoRepository;
        this.naturezaOperacionalLeitoRepository = naturezaOperacionalLeitoRepository;
        this.leitoModalidadeTipoRepository = leitoModalidadeTipoRepository;
        this.leitoModalidadeRepository = leitoModalidadeRepository;
    }

    @Transactional(readOnly = true)
    public List<Leito> listar(String q) {
        String filtro = normalize(q);
        if (filtro == null) {
            return leitoRepository.findAllWithReferencesOrderByNome();
        }
        return leitoRepository.buscarPorFiltro(filtro);
    }

    @Transactional(readOnly = true)
    public List<TipoLeito> listarTiposAtivos() {
        return tipoLeitoRepository.findByAtivoTrueOrderByDescricaoAsc();
    }

    @Transactional(readOnly = true)
    public List<PerfilLeito> listarPerfisAtivos() {
        return perfilLeitoRepository.findByAtivoTrueOrderByDescricaoAsc();
    }

    @Transactional(readOnly = true)
    public List<NaturezaOperacionalLeito> listarNaturezasOperacionaisAtivas() {
        return naturezaOperacionalLeitoRepository.findByAtivoTrueOrderByDescricaoAsc();
    }

    @Transactional(readOnly = true)
    public List<LeitoModalidadeTipo> listarModalidadesAtivas() {
        return leitoModalidadeTipoRepository.findByAtivoTrueOrderByDescricaoAsc();
    }

    @Transactional(readOnly = true)
    public List<Area> listarAreasComLeito() {
        return areaRepository.findAreasAtivasComLeito();
    }

    @Transactional(readOnly = true)
    public Map<Long, String> mapaModalidadesDescricao(List<Leito> leitos) {
        if (leitos == null || leitos.isEmpty()) {
            return Map.of();
        }
        List<Long> leitoIds = leitos.stream().map(Leito::getId).toList();
        Map<Long, List<String>> grouped = new LinkedHashMap<>();
        for (LeitoModalidade item : leitoModalidadeRepository.findByLeitoIdInWithTipoOrderByLeitoIdAscDescricaoAsc(leitoIds)) {
            grouped.computeIfAbsent(item.getLeito().getId(), ignored -> new ArrayList<>())
                    .add(item.getModalidadeTipo().getCodigo());
        }
        Map<Long, String> result = new LinkedHashMap<>();
        for (Long leitoId : leitoIds) {
            List<String> modalidades = grouped.get(leitoId);
            result.put(leitoId, modalidades == null || modalidades.isEmpty() ? "-" : String.join(", ", modalidades));
        }
        return result;
    }

    @Transactional(readOnly = true)
    public Leito buscar(Long id) {
        return leitoRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Leito nao encontrado"));
    }

    @Transactional
    public Leito criar(LeitoForm form) {
        Leito leito = new Leito();
        List<LeitoModalidadeTipo> modalidades = apply(leito, form);
        Leito saved = leitoRepository.save(leito);
        salvarModalidades(saved, modalidades);
        return saved;
    }

    @Transactional
    public Leito atualizar(Long id, LeitoForm form) {
        Leito leito = buscar(id);
        List<LeitoModalidadeTipo> modalidades = apply(leito, form);
        Leito saved = leitoRepository.save(leito);
        salvarModalidades(saved, modalidades);
        return saved;
    }

    @Transactional
    public void excluir(Long id) {
        leitoModalidadeRepository.deleteByLeitoId(id);
        leitoRepository.delete(buscar(id));
    }

    @Transactional(readOnly = true)
    public LeitoForm toForm(Leito leito) {
        LeitoForm form = new LeitoForm();
        form.setUnidadeId(leito.getUnidade().getId());
        form.setAreaId(leito.getArea().getId());
        form.setTipoLeitoId(leito.getTipoLeito().getId());
        form.setPerfilLeitoId(leito.getPerfilLeito() == null ? null : leito.getPerfilLeito().getId());
        form.setCodigo(leito.getCodigo());
        form.setDescricao(leito.getDescricao());
        form.setRecebePs(leito.isRecebePs());
        form.setPermiteDestinoDefinitivo(leito.isPermiteDestinoDefinitivo());
        form.setAssistencial(leito.isAssistencial());
        form.setNaturezaOperacionalId(leito.getNaturezaOperacional() == null
                ? null
                : leito.getNaturezaOperacional().getId());
        form.setAtivo(leito.isAtivo());
        form.setModalidadeIds(leitoModalidadeRepository.findByLeitoIdWithTipoOrderByDescricaoAsc(leito.getId())
                .stream()
                .map(item -> item.getModalidadeTipo().getId())
                .toList());
        return form;
    }

    private List<LeitoModalidadeTipo> apply(Leito leito, LeitoForm form) {
        Unidade unidade = unidadeRepository.findById(form.getUnidadeId())
                .orElseThrow(() -> new IllegalArgumentException("Unidade nao encontrada"));
        Area area = areaRepository.findById(form.getAreaId())
                .orElseThrow(() -> new IllegalArgumentException("Area nao encontrada"));
        TipoLeito tipoLeito = tipoLeitoRepository.findById(form.getTipoLeitoId())
                .orElseThrow(() -> new IllegalArgumentException("Tipo de leito nao encontrado"));
        PerfilLeito perfilLeito = null;
        if (form.getPerfilLeitoId() != null) {
            perfilLeito = perfilLeitoRepository.findById(form.getPerfilLeitoId())
                    .orElseThrow(() -> new IllegalArgumentException("Perfil de leito nao encontrado"));
        }
        if (form.getNaturezaOperacionalId() == null) {
            throw new IllegalArgumentException("Natureza operacional e obrigatoria");
        }
        NaturezaOperacionalLeito naturezaOperacional = naturezaOperacionalLeitoRepository.findById(form.getNaturezaOperacionalId())
                .orElseThrow(() -> new IllegalArgumentException("Natureza operacional nao encontrada"));
        if (!naturezaOperacional.isAtivo()) {
            throw new IllegalArgumentException("Nao e permitido usar natureza operacional inativa");
        }

        if (!area.getUnidade().getId().equals(unidade.getId())) {
            throw new IllegalArgumentException("Area informada nao pertence a unidade selecionada");
        }
        if (!areaCapacidadeRepository.existsByAreaIdAndCapacidadeAreaNomeIgnoreCase(area.getId(), "POSSUI_LEITO")) {
            throw new IllegalArgumentException("Area informada nao possui capacidade de leito");
        }

        Set<Long> modalidadeIds = new LinkedHashSet<>(form.getModalidadeIds() == null ? List.of() : form.getModalidadeIds());
        if (modalidadeIds.isEmpty()) {
            throw new IllegalArgumentException("Selecione ao menos uma modalidade de leito");
        }
        List<LeitoModalidadeTipo> modalidades = leitoModalidadeTipoRepository.findAllById(modalidadeIds);
        if (modalidades.size() != modalidadeIds.size()) {
            throw new IllegalArgumentException("Modalidade de leito invalida");
        }
        if (modalidades.stream().anyMatch(item -> !item.isAtivo())) {
            throw new IllegalArgumentException("Nao e permitido usar modalidade inativa");
        }
        boolean permiteInternacao = modalidades.stream()
                .map(LeitoModalidadeTipo::getCodigo)
                .anyMatch(codigo -> "INTERNACAO".equalsIgnoreCase(codigo));
        if (form.isPermiteDestinoDefinitivo() && !permiteInternacao) {
            throw new IllegalArgumentException("Destino definitivo exige modalidade INTERNACAO");
        }

        String codigo = normalizeUpper(form.getCodigo());
        if (leitoRepository.existsCodigoByUnidade(unidade.getId(), codigo, leito.getId())) {
            throw new IllegalArgumentException("Ja existe leito com este codigo na unidade informada");
        }

        leito.setUnidade(unidade);
        leito.setArea(area);
        leito.setTipoLeito(tipoLeito);
        leito.setPerfilLeito(perfilLeito);
        leito.setCodigo(codigo);
        leito.setDescricao(normalize(form.getDescricao()));
        leito.setRecebePs(form.isRecebePs());
        leito.setPermiteDestinoDefinitivo(form.isPermiteDestinoDefinitivo());
        leito.setAssistencial(form.isAssistencial());
        leito.setNaturezaOperacional(naturezaOperacional);
        leito.setAtivo(form.isAtivo());
        return modalidades;
    }

    private void salvarModalidades(Leito leito, List<LeitoModalidadeTipo> modalidades) {
        leitoModalidadeRepository.deleteByLeitoId(leito.getId());
        leitoModalidadeRepository.flush();
        Set<Long> modalidadeIdsInseridas = new LinkedHashSet<>();
        for (LeitoModalidadeTipo modalidade : modalidades) {
            if (!modalidadeIdsInseridas.add(modalidade.getId())) {
                continue;
            }
            LeitoModalidade rel = new LeitoModalidade();
            rel.setLeito(leito);
            rel.setModalidadeTipo(modalidade);
            leitoModalidadeRepository.save(rel);
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
