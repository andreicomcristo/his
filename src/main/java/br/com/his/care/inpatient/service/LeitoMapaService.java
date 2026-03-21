package br.com.his.care.inpatient.service;

import br.com.his.care.attendance.api.dto.*;
import br.com.his.care.attendance.dto.*;
import br.com.his.care.attendance.model.*;
import br.com.his.care.attendance.repository.*;
import br.com.his.care.attendance.service.*;
import br.com.his.care.admission.dto.*;
import br.com.his.care.admission.model.*;
import br.com.his.care.admission.repository.*;
import br.com.his.care.triage.dto.*;
import br.com.his.care.triage.model.*;
import br.com.his.care.triage.repository.*;
import br.com.his.care.inpatient.dto.*;
import br.com.his.care.inpatient.model.*;
import br.com.his.care.inpatient.repository.*;
import br.com.his.care.inpatient.service.*;
import br.com.his.care.episode.model.*;
import br.com.his.care.episode.repository.*;
import br.com.his.care.timeline.dto.*;
import br.com.his.care.timeline.model.*;
import br.com.his.care.timeline.repository.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.his.care.inpatient.dto.LeitoMapaAreaDto;
import br.com.his.care.inpatient.dto.LeitoMapaItemDto;
import br.com.his.care.attendance.model.Atendimento;
import br.com.his.care.inpatient.model.Leito;
import br.com.his.care.inpatient.model.LeitoModalidade;
import br.com.his.care.inpatient.model.LeitoOcupacao;
import br.com.his.care.inpatient.repository.InternacaoRepository;
import br.com.his.care.inpatient.repository.LeitoModalidadeRepository;
import br.com.his.care.inpatient.repository.LeitoRepository;

@Service
public class LeitoMapaService {

    private final LeitoRepository leitoRepository;
    private final LeitoModalidadeRepository leitoModalidadeRepository;
    private final InternacaoRepository internacaoRepository;
    private final LeitoOcupacaoService leitoOcupacaoService;

    public LeitoMapaService(LeitoRepository leitoRepository,
                            LeitoModalidadeRepository leitoModalidadeRepository,
                            InternacaoRepository internacaoRepository,
                            LeitoOcupacaoService leitoOcupacaoService) {
        this.leitoRepository = leitoRepository;
        this.leitoModalidadeRepository = leitoModalidadeRepository;
        this.internacaoRepository = internacaoRepository;
        this.leitoOcupacaoService = leitoOcupacaoService;
    }

    @Transactional(readOnly = true)
    public List<LeitoMapaAreaDto> montarMapaPorUnidade(Long unidadeId) {
        if (unidadeId == null) {
            return List.of();
        }
        List<Leito> leitos = leitoRepository.findAtivosByUnidadeIdOrderByAreaENome(unidadeId);
        if (leitos.isEmpty()) {
            return List.of();
        }

        List<Long> leitoIds = leitos.stream().map(Leito::getId).toList();
        Map<Long, LeitoOcupacao> ocupacaoAbertaByLeito = leitoOcupacaoService
                .listarOcupacoesAbertasDetalhadasPorLeitos(leitoIds)
                .stream()
                .collect(Collectors.toMap(item -> item.getLeito().getId(), item -> item, (a, b) -> a));
        List<Long> atendimentoIdsOcupados = ocupacaoAbertaByLeito.values().stream()
                .map(item -> {
                    if (item.getObservacaoAtendimento() != null) {
                        return item.getObservacaoAtendimento().getAtendimento().getId();
                    }
                    if (item.getInternacao() != null) {
                        return item.getInternacao().getAtendimento().getId();
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        List<Long> atendimentoIdsComInternacao = atendimentoIdsOcupados.isEmpty()
                ? List.of()
                : internacaoRepository.findAtendimentoIdsComInternacao(atendimentoIdsOcupados);

        Map<Long, List<LeitoModalidade>> modalidadesByLeito = new HashMap<>();
        for (LeitoModalidade item : leitoModalidadeRepository.findByLeitoIdInWithTipoOrderByLeitoIdAscDescricaoAsc(leitoIds)) {
            modalidadesByLeito.computeIfAbsent(item.getLeito().getId(), ignored -> new ArrayList<>()).add(item);
        }

        Map<Long, LeitoMapaAreaDto> areas = new LinkedHashMap<>();
        for (Leito leito : leitos) {
            LeitoMapaAreaDto area = areas.computeIfAbsent(leito.getArea().getId(), ignored -> {
                LeitoMapaAreaDto dto = new LeitoMapaAreaDto();
                dto.setAreaId(leito.getArea().getId());
                dto.setAreaNome(leito.getArea().getDescricao());
                return dto;
            });

            LeitoMapaItemDto item = new LeitoMapaItemDto();
            item.setLeitoId(leito.getId());
            item.setCodigo(leito.getCodigo());
            item.setDescricao(leito.getDescricao());
            item.setAreaNome(leito.getArea().getDescricao());
            item.setNaturezaOperacionalDescricao(
                    leito.getNaturezaOperacional() == null ? "-" : leito.getNaturezaOperacional().getDescricao());
            item.setVirtualSuperlotacao(
                    leito.getNaturezaOperacional() != null && leito.getNaturezaOperacional().isVirtualSuperlotacao());

            List<LeitoModalidade> modalidades = modalidadesByLeito.getOrDefault(leito.getId(), List.of());
            String modalidadesDescricao = modalidades.stream()
                    .map(rel -> rel.getModalidadeTipo().getDescricao())
                    .collect(Collectors.joining(", "));
            item.setModalidadesDescricao(modalidadesDescricao.isBlank() ? "-" : modalidadesDescricao);
            item.setPermiteObservacao(modalidades.stream()
                    .anyMatch(rel -> LeitoOcupacaoService.MODALIDADE_OBSERVACAO.equalsIgnoreCase(rel.getModalidadeTipo().getCodigo())));
            item.setPermiteInternacao(modalidades.stream()
                    .anyMatch(rel -> LeitoOcupacaoService.MODALIDADE_INTERNACAO.equalsIgnoreCase(rel.getModalidadeTipo().getCodigo())));

            LeitoOcupacao ocupacao = ocupacaoAbertaByLeito.get(leito.getId());
            boolean livre = ocupacao == null;
            item.setLivre(livre);
            area.setTotalLeitos(area.getTotalLeitos() + 1);
            if (item.isVirtualSuperlotacao()) {
                area.setLeitosVirtuais(area.getLeitosVirtuais() + 1);
            } else {
                area.setLeitosFixos(area.getLeitosFixos() + 1);
            }
            if (livre) {
                area.setLeitosLivres(area.getLeitosLivres() + 1);
            } else {
                item.setOcupacaoId(ocupacao.getId());
                item.setTipoOcupacaoDescricao(
                        ocupacao.getTipoOcupacao() == null ? "-" : ocupacao.getTipoOcupacao().getDescricao());
                Atendimento atendimento = null;
                if (ocupacao.getObservacaoAtendimento() != null) {
                    item.setContexto("OBSERVACAO");
                    area.setOcupadosObservacao(area.getOcupadosObservacao() + 1);
                    item.setObservacaoId(ocupacao.getObservacaoAtendimento().getId());
                    atendimento = ocupacao.getObservacaoAtendimento().getAtendimento();
                } else if (ocupacao.getInternacao() != null) {
                    item.setContexto("INTERNACAO");
                    area.setOcupadosInternacao(area.getOcupadosInternacao() + 1);
                    atendimento = ocupacao.getInternacao().getAtendimento();
                }
                area.setLeitosOcupados(area.getLeitosOcupados() + 1);
                if (atendimento != null) {
                    item.setAtendimentoId(atendimento.getId());
                    item.setPacienteNome(atendimento.getPaciente().getNomeExibicao());
                    item.setTipoAtendimento(atendimento.getTipoAtendimentoCodigo());
                    boolean podeConverter = "OBSERVACAO".equals(item.getContexto())
                            && item.getObservacaoId() != null
                            && !atendimentoIdsComInternacao.contains(atendimento.getId());
                    item.setPodeConverterParaInternacao(podeConverter);
                }
            }
            area.getLeitos().add(item);
        }
        List<LeitoMapaAreaDto> result = new ArrayList<>(areas.values());
        for (LeitoMapaAreaDto area : result) {
            if (area.getLeitosFixos() <= 0) {
                area.setTaxaNominalPercentual(null);
                continue;
            }
            double percentual = ((double) area.getLeitosOcupados() / area.getLeitosFixos()) * 100d;
            area.setTaxaNominalPercentual(BigDecimal.valueOf(percentual)
                    .setScale(2, RoundingMode.HALF_UP)
                    .doubleValue());
        }
        return result;
    }
}
