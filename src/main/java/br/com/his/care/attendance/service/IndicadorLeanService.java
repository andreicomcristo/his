package br.com.his.care.attendance.service;

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
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.his.care.attendance.api.dto.LeanChegadaDiaSemanaResponse;
import br.com.his.care.attendance.api.dto.LeanChegadaHoraResponse;
import br.com.his.care.attendance.api.dto.LeanConsultaIndicadorResponse;
import br.com.his.care.attendance.api.dto.LeanConsultaResponse;
import br.com.his.care.attendance.api.dto.LeanIndicadorValorResponse;
import br.com.his.care.attendance.api.dto.LeanPortaTriagemResponse;
import br.com.his.care.attendance.api.dto.LeanTempoTriagemResponse;
import br.com.his.care.attendance.api.dto.LeanTriagemCorDistribuicaoResponse;
import br.com.his.care.attendance.api.dto.TaxaOcupacaoLeanIndicadorResponse;
import br.com.his.care.attendance.api.dto.TaxaOcupacaoLeanResponse;
import br.com.his.care.attendance.api.dto.TaxaOcupacaoTipoLeitoResponse;

@Service
public class IndicadorLeanService {

    private final JdbcTemplate jdbcTemplate;

    public IndicadorLeanService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional(readOnly = true)
    public TaxaOcupacaoLeanResponse calcularTaxasOcupacao(Long unidadeId, LocalDate dataInicio, LocalDate dataFim) {
        if (unidadeId == null) {
            throw new IllegalArgumentException("Unidade e obrigatoria");
        }
        if (dataInicio == null || dataFim == null) {
            throw new IllegalArgumentException("Data inicio e data fim sao obrigatorias");
        }
        if (dataFim.isBefore(dataInicio)) {
            throw new IllegalArgumentException("Data fim nao pode ser anterior a data inicio");
        }

        LocalDateTime inicio = dataInicio.atStartOfDay();
        LocalDateTime fimExclusivo = dataFim.plusDays(1).atStartOfDay();
        long minutosPeriodo = ChronoUnit.MINUTES.between(inicio, fimExclusivo);
        if (minutosPeriodo <= 0) {
            throw new IllegalArgumentException("Periodo invalido para calculo");
        }

        List<TaxaOcupacaoLeanIndicadorResponse> indicadores = new ArrayList<>();
        for (TaxaOcupacaoCategoria categoria : TaxaOcupacaoCategoria.values()) {
            CalculoOcupacao calculo = calcularOcupacao(
                    unidadeId,
                    inicio,
                    fimExclusivo,
                    minutosPeriodo,
                    categoria.perfilLeitoCodigo,
                    categoria.tipoLeitoCodigo);
            indicadores.add(new TaxaOcupacaoLeanIndicadorResponse(
                    categoria.codigo,
                    categoria.descricao,
                    calculo.leitosFixosElegiveis(),
                    calculo.leitosTotaisElegiveis(),
                    calculo.minutosOcupados(),
                    calculo.minutosDisponiveisNominal(),
                    calculo.minutosDisponiveisOperacional(),
                    calculo.taxaNominalPercentual(),
                    calculo.taxaOperacionalPercentual()));
        }

        List<TaxaOcupacaoTipoLeitoResponse> indicadoresPorTipoLeito = new ArrayList<>();
        for (String tipoLeito : listarTiposLeitoAtivos()) {
            CalculoOcupacao calculo = calcularOcupacao(
                    unidadeId,
                    inicio,
                    fimExclusivo,
                    minutosPeriodo,
                    null,
                    tipoLeito);
            indicadoresPorTipoLeito.add(new TaxaOcupacaoTipoLeitoResponse(
                    tipoLeito,
                    calculo.leitosFixosElegiveis(),
                    calculo.leitosTotaisElegiveis(),
                    calculo.minutosOcupados(),
                    calculo.minutosDisponiveisNominal(),
                    calculo.minutosDisponiveisOperacional(),
                    calculo.taxaNominalPercentual(),
                    calculo.taxaOperacionalPercentual()));
        }

        return new TaxaOcupacaoLeanResponse(
                unidadeId,
                dataInicio,
                dataFim,
                indicadores,
                indicadoresPorTipoLeito);
    }

    @Transactional(readOnly = true)
    public LeanPortaTriagemResponse calcularPortaTriagem(Long unidadeId, LocalDate dataInicio, LocalDate dataFim) {
        validarParametros(unidadeId, dataInicio, dataFim);

        LocalDateTime inicio = dataInicio.atStartOfDay();
        LocalDateTime fimExclusivo = dataFim.plusDays(1).atStartOfDay();
        long diasPeriodo = ChronoUnit.DAYS.between(dataInicio, dataFim.plusDays(1));
        if (diasPeriodo <= 0) {
            throw new IllegalArgumentException("Periodo invalido para calculo");
        }

        long totalAtendimentosPeriodo = contarAtendimentosChegada(unidadeId, inicio, fimExclusivo);
        int anoReferencia = dataFim.getYear();
        long por01TotalAno = contarAtendimentosChegada(
                unidadeId,
                LocalDate.of(anoReferencia, 1, 1).atStartOfDay(),
                LocalDate.of(anoReferencia + 1, 1, 1).atStartOfDay());
        long por02TotalDia = contarAtendimentosChegada(
                unidadeId,
                dataFim.atStartOfDay(),
                dataFim.plusDays(1).atStartOfDay());

        long totalComEntradaPerfil = contarAtendimentosComEntradaPorPerfil(unidadeId, inicio, fimExclusivo, null);
        long totalHorizontal = contarAtendimentosComEntradaPorPerfil(unidadeId, inicio, fimExclusivo, "HORIZONTAL");
        long totalVertical = contarAtendimentosComEntradaPorPerfil(unidadeId, inicio, fimExclusivo, "VERTICAL");
        Double por03Horizontal = percentual(totalHorizontal, totalComEntradaPerfil);
        Double por04Vertical = percentual(totalVertical, totalComEntradaPerfil);

        List<LeanChegadaHoraResponse> chegadasPorHora = calcularChegadasPorHora(unidadeId, inicio, fimExclusivo, diasPeriodo);
        Double por05MediaHora = round2(totalAtendimentosPeriodo / (double) (diasPeriodo * 24));

        List<LeanChegadaDiaSemanaResponse> chegadasPorDiaSemana = calcularChegadasPorDiaSemana(unidadeId, dataInicio, dataFim, inicio, fimExclusivo);
        Double por06MediaDiaSemana = round2(totalAtendimentosPeriodo / (double) diasPeriodo);

        long totalOrientadoRede = contarAtendimentosPorMotivoDesfecho(unidadeId, inicio, fimExclusivo, "ORIENTADO_REDE");
        Double por07OrientadoRede = percentual(totalOrientadoRede, totalAtendimentosPeriodo);

        long totalEvasao = contarAtendimentosPorStatusOuMotivo(unidadeId, inicio, fimExclusivo, "EVADIU", "EVASAO");
        Double por08Evasao = percentual(totalEvasao, totalAtendimentosPeriodo);

        boolean abandonoConfigurado = existeStatusOuMotivo("ABANDONO");
        Double por09Abandono = null;
        String por09Observacao = "Nao configurado (status/motivo ABANDONO inexistente)";
        if (abandonoConfigurado) {
            long totalAbandono = contarAtendimentosPorStatusOuMotivo(unidadeId, inicio, fimExclusivo, "ABANDONO", "ABANDONO");
            por09Abandono = percentual(totalAbandono, totalAtendimentosPeriodo);
            por09Observacao = null;
        }

        List<LeanIndicadorValorResponse> indicadoresPorta = new ArrayList<>();
        indicadoresPorta.add(new LeanIndicadorValorResponse(
                "POR-01",
                "Total de pacientes atendidos no pronto socorro por ano",
                (double) por01TotalAno,
                "atendimentos",
                "Ano de referencia: " + anoReferencia));
        indicadoresPorta.add(new LeanIndicadorValorResponse(
                "POR-02",
                "Total de pacientes atendidos no pronto socorro por dia",
                (double) por02TotalDia,
                "atendimentos",
                "Data de referencia: " + dataFim));
        indicadoresPorta.add(new LeanIndicadorValorResponse(
                "POR-03",
                "Percentual de pacientes horizontais por dia (ambulancia)",
                por03Horizontal,
                "%",
                "Base: atendimentos com entrada e forma de chegada"));
        indicadoresPorta.add(new LeanIndicadorValorResponse(
                "POR-04",
                "Percentual de pacientes verticais por dia",
                por04Vertical,
                "%",
                "Base: atendimentos com entrada e forma de chegada"));
        indicadoresPorta.add(new LeanIndicadorValorResponse(
                "POR-05",
                "Media de chegada de pacientes por hora",
                por05MediaHora,
                "pacientes/hora",
                "Periodo filtrado"));
        indicadoresPorta.add(new LeanIndicadorValorResponse(
                "POR-06",
                "Media de chegada de pacientes por dia da semana",
                por06MediaDiaSemana,
                "pacientes/dia",
                "Periodo filtrado"));
        indicadoresPorta.add(new LeanIndicadorValorResponse(
                "POR-07",
                "Percentual de pacientes orientados para rede de saude",
                por07OrientadoRede,
                "%",
                null));
        indicadoresPorta.add(new LeanIndicadorValorResponse(
                "POR-08",
                "Taxa de evasao",
                por08Evasao,
                "%",
                null));
        indicadoresPorta.add(new LeanIndicadorValorResponse(
                "POR-09",
                "Taxa de abandono",
                por09Abandono,
                "%",
                por09Observacao));

        long totalClassificacoes = contarClassificacoes(unidadeId, inicio, fimExclusivo);
        List<LeanTriagemCorDistribuicaoResponse> distribuicaoTriagem = listarDistribuicaoTriagem(unidadeId, inicio, fimExclusivo, totalClassificacoes);
        long totalRiscoMaior = contarClassificacoesRiscoMaior(unidadeId, inicio, fimExclusivo);
        Double tri03RiscoMaior = percentual(totalRiscoMaior, totalClassificacoes);
        TempoTriagemMetricas tempoTriagem = buscarTempoTriagem(unidadeId, inicio, fimExclusivo);
        long tri06SalasClassificacao = contarSalasClassificacao(unidadeId);

        List<LeanIndicadorValorResponse> indicadoresTriagem = new ArrayList<>();
        indicadoresTriagem.add(new LeanIndicadorValorResponse(
                "TRI-02",
                "Distribuicao dos pacientes pelo sistema de triagem",
                null,
                null,
                "Ver detalhamento por classificacao de cor"));
        indicadoresTriagem.add(new LeanIndicadorValorResponse(
                "TRI-03",
                "Percentual de risco maior",
                tri03RiscoMaior,
                "%",
                null));
        indicadoresTriagem.add(new LeanIndicadorValorResponse(
                "TRI-04",
                "Tempo medio de atendimento da triagem",
                tempoTriagem.mediaMinutos(),
                "min",
                "Mediana: " + formatNullable(tempoTriagem.medianaMinutos()) + " | P90: " + formatNullable(tempoTriagem.p90Minutos())));
        indicadoresTriagem.add(new LeanIndicadorValorResponse(
                "TRI-05",
                "Media de enfermeiros na triagem por hora",
                null,
                null,
                "Nao implementado no HIS (sem modulo de escala)"));
        indicadoresTriagem.add(new LeanIndicadorValorResponse(
                "TRI-06",
                "Quantidade de salas de classificacao de risco",
                (double) tri06SalasClassificacao,
                "salas",
                "Baseado na capacidade SALA_CLASSIFICACAO"));

        return new LeanPortaTriagemResponse(
                unidadeId,
                dataInicio,
                dataFim,
                indicadoresPorta,
                indicadoresTriagem,
                chegadasPorHora,
                chegadasPorDiaSemana,
                distribuicaoTriagem,
                new LeanTempoTriagemResponse(
                        tempoTriagem.mediaMinutos(),
                        tempoTriagem.medianaMinutos(),
                        tempoTriagem.p90Minutos(),
                        tempoTriagem.totalTriagensComTempo()));
    }

    @Transactional(readOnly = true)
    public LeanConsultaResponse calcularConsulta(Long unidadeId, LocalDate dataInicio, LocalDate dataFim) {
        validarParametros(unidadeId, dataInicio, dataFim);

        LocalDateTime inicio = dataInicio.atStartOfDay();
        LocalDateTime fimExclusivo = dataFim.plusDays(1).atStartOfDay();

        TempoMinutosMetricas psg01 = buscarTempoPsg01(unidadeId, inicio, fimExclusivo);
        TempoMinutosMetricas psg02 = buscarTempoPsg02(unidadeId, inicio, fimExclusivo);
        TempoMinutosMetricas psg03 = buscarTempoPsg03(unidadeId, inicio, fimExclusivo);

        List<LeanConsultaIndicadorResponse> indicadores = new ArrayList<>();
        indicadores.add(new LeanConsultaIndicadorResponse(
                "PSG-01",
                "LOS sem internacao",
                psg01.mediaMinutos(),
                psg01.medianaMinutos(),
                psg01.p90Minutos(),
                psg01.totalCasos(),
                null,
                "Base: desfecho tipo ATENDIMENTO (exclui EVASAO/ABANDONO)."));
        indicadores.add(new LeanConsultaIndicadorResponse(
                "PSG-02",
                "LOS com internacao",
                psg02.mediaMinutos(),
                psg02.medianaMinutos(),
                psg02.p90Minutos(),
                psg02.totalCasos(),
                null,
                "Base: chegada no primeiro leito DEFINITIVO de internacao."));
        indicadores.add(new LeanConsultaIndicadorResponse(
                "PSG-03",
                "Tempo de boarding",
                psg03.mediaMinutos(),
                psg03.medianaMinutos(),
                psg03.p90Minutos(),
                psg03.totalCasos(),
                psg03.totalFallbackDesfecho(),
                "Base: decisao de internacao ate leito DEFINITIVO; fallback para desfecho quando nao houve leito definitivo."));

        return new LeanConsultaResponse(unidadeId, dataInicio, dataFim, indicadores);
    }

    @Transactional(readOnly = true)
    public LeanRecepcaoOperadoresResponse calcularRecepcaoOperadores(Long unidadeId,
                                                                     LocalDate dataInicio,
                                                                     LocalDate dataFim) {
        validarParametros(unidadeId, dataInicio, dataFim);

        LocalDateTime inicio = dataInicio.atStartOfDay();
        LocalDateTime fimExclusivo = dataFim.plusDays(1).atStartOfDay();
        OperadoresRecepcaoResultado resultadoOperadores = listarRecepcaoOperadores(
                unidadeId,
                inicio,
                fimExclusivo);
        List<LeanRecepcaoOperadorItemResponse> operadores = resultadoOperadores.operadores();
        long totalOperadores = operadores.size();
        long totalAtendimentos = operadores.stream()
                .mapToLong(item -> item.quantidadeAtendimentos() == null ? 0L : item.quantidadeAtendimentos())
                .sum();

        Double mediaAtendimentosPorOperador = totalOperadores > 0
                ? round2(totalAtendimentos / (double) totalOperadores)
                : 0d;
        Double mediaTempoRecepcaoMinutos = resultadoOperadores.totalRegistrosComTempo() > 0
                ? round2(resultadoOperadores.somaMinutos() / resultadoOperadores.totalRegistrosComTempo())
                : null;

        return new LeanRecepcaoOperadoresResponse(
                unidadeId,
                dataInicio,
                dataFim,
                totalOperadores,
                totalAtendimentos,
                mediaAtendimentosPorOperador,
                mediaTempoRecepcaoMinutos,
                operadores);
    }

    @Transactional(readOnly = true)
    public LeanClassificacaoOperadoresResponse calcularClassificacaoOperadores(Long unidadeId,
                                                                               LocalDate dataInicio,
                                                                               LocalDate dataFim) {
        validarParametros(unidadeId, dataInicio, dataFim);

        LocalDateTime inicio = dataInicio.atStartOfDay();
        LocalDateTime fimExclusivo = dataFim.plusDays(1).atStartOfDay();
        OperadoresClassificacaoResultado resultadoOperadores = listarClassificacaoOperadores(
                unidadeId,
                inicio,
                fimExclusivo);
        List<LeanClassificacaoOperadorItemResponse> operadores = resultadoOperadores.operadores();
        long totalOperadores = operadores.size();
        long totalClassificacoes = operadores.stream()
                .mapToLong(item -> item.quantidadeClassificacoes() == null ? 0L : item.quantidadeClassificacoes())
                .sum();

        Double mediaClassificacoesPorOperador = totalOperadores > 0
                ? round2(totalClassificacoes / (double) totalOperadores)
                : 0d;
        Double mediaTempoClassificacaoMinutos = resultadoOperadores.totalRegistrosComTempo() > 0
                ? round2(resultadoOperadores.somaMinutos() / resultadoOperadores.totalRegistrosComTempo())
                : null;

        return new LeanClassificacaoOperadoresResponse(
                unidadeId,
                dataInicio,
                dataFim,
                totalOperadores,
                totalClassificacoes,
                mediaClassificacoesPorOperador,
                mediaTempoClassificacaoMinutos,
                operadores);
    }

    @Transactional(readOnly = true)
    public List<LeanRecepcaoOperadorAtendimentoItemResponse> listarDetalhesRecepcaoPorOperador(Long unidadeId,
                                                                                                 LocalDate dataInicio,
                                                                                                 LocalDate dataFim,
                                                                                                 Long operadorUsuarioId) {
        validarParametros(unidadeId, dataInicio, dataFim);
        if (operadorUsuarioId == null) {
            throw new IllegalArgumentException("Operador e obrigatorio");
        }

        LocalDateTime inicio = dataInicio.atStartOfDay();
        LocalDateTime fimExclusivo = dataFim.plusDays(1).atStartOfDay();

        List<Map<String, Object>> rows = jdbcTemplate.queryForList("""
                select
                    a.id as atendimento_id,
                    coalesce(nullif(trim(pm.nome), ''), nullif(trim(p.nome), ''), 'NAO INFORMADO') as paciente_nome,
                    ap.inicio_em as inicio_em,
                    ap.fim_em as fim_em,
                    case
                        when ap.fim_em is not null and ap.fim_em > ap.inicio_em
                            then extract(epoch from (ap.fim_em - ap.inicio_em)) / 60.0
                        else null
                    end as tempo_minutos
                from atendimento_periodo ap
                join atendimento a on a.id = ap.atendimento_id
                join paciente p on p.id = a.paciente_id
                left join paciente pm on pm.id = p.merged_into_id
                where a.unidade_id = ?
                  and ap.tipo = 'RECEPCAO'
                  and ap.usuario_inicio_id = ?
                  and ap.inicio_em >= ?
                  and ap.inicio_em < ?
                order by ap.inicio_em desc, a.id desc
                """, unidadeId, operadorUsuarioId, inicio, fimExclusivo);

        List<LeanRecepcaoOperadorAtendimentoItemResponse> itens = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            Long atendimentoId = row.get("atendimento_id") == null ? null : ((Number) row.get("atendimento_id")).longValue();
            String pacienteNome = row.get("paciente_nome") == null ? "NAO INFORMADO" : row.get("paciente_nome").toString();
            LocalDateTime inicioRecepcao = toLocalDateTime(row.get("inicio_em"));
            LocalDateTime fimRecepcao = toLocalDateTime(row.get("fim_em"));
            Double tempoMinutos = row.get("tempo_minutos") == null ? null : round2(((Number) row.get("tempo_minutos")).doubleValue());
            itens.add(new LeanRecepcaoOperadorAtendimentoItemResponse(
                    atendimentoId,
                    pacienteNome,
                    inicioRecepcao,
                    fimRecepcao,
                    tempoMinutos));
        }
        return itens;
    }

    @Transactional(readOnly = true)
    public List<LeanClassificacaoOperadorAtendimentoItemResponse> listarDetalhesClassificacaoPorOperador(Long unidadeId,
                                                                                                           LocalDate dataInicio,
                                                                                                           LocalDate dataFim,
                                                                                                           Long operadorUsuarioId) {
        validarParametros(unidadeId, dataInicio, dataFim);
        if (operadorUsuarioId == null) {
            throw new IllegalArgumentException("Operador e obrigatorio");
        }

        LocalDateTime inicio = dataInicio.atStartOfDay();
        LocalDateTime fimExclusivo = dataFim.plusDays(1).atStartOfDay();

        List<Map<String, Object>> rows = jdbcTemplate.queryForList("""
                select
                    a.id as atendimento_id,
                    coalesce(nullif(trim(pm.nome), ''), nullif(trim(p.nome), ''), 'NAO INFORMADO') as paciente_nome,
                    ap.inicio_em as inicio_em,
                    ap.fim_em as fim_em,
                    case
                        when ap.fim_em is not null and ap.fim_em > ap.inicio_em
                            then extract(epoch from (ap.fim_em - ap.inicio_em)) / 60.0
                        else null
                    end as tempo_minutos
                from atendimento_periodo ap
                join atendimento a on a.id = ap.atendimento_id
                join paciente p on p.id = a.paciente_id
                left join paciente pm on pm.id = p.merged_into_id
                where a.unidade_id = ?
                  and ap.tipo = 'TRIAGEM'
                  and ap.usuario_inicio_id = ?
                  and ap.inicio_em >= ?
                  and ap.inicio_em < ?
                order by ap.inicio_em desc, a.id desc
                """, unidadeId, operadorUsuarioId, inicio, fimExclusivo);

        List<LeanClassificacaoOperadorAtendimentoItemResponse> itens = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            Long atendimentoId = row.get("atendimento_id") == null ? null : ((Number) row.get("atendimento_id")).longValue();
            String pacienteNome = row.get("paciente_nome") == null ? "NAO INFORMADO" : row.get("paciente_nome").toString();
            LocalDateTime inicioClassificacao = toLocalDateTime(row.get("inicio_em"));
            LocalDateTime fimClassificacao = toLocalDateTime(row.get("fim_em"));
            Double tempoMinutos = row.get("tempo_minutos") == null ? null : round2(((Number) row.get("tempo_minutos")).doubleValue());
            itens.add(new LeanClassificacaoOperadorAtendimentoItemResponse(
                    atendimentoId,
                    pacienteNome,
                    inicioClassificacao,
                    fimClassificacao,
                    tempoMinutos));
        }
        return itens;
    }

    private void validarParametros(Long unidadeId, LocalDate dataInicio, LocalDate dataFim) {
        if (unidadeId == null) {
            throw new IllegalArgumentException("Unidade e obrigatoria");
        }
        if (dataInicio == null || dataFim == null) {
            throw new IllegalArgumentException("Data inicio e data fim sao obrigatorias");
        }
        if (dataFim.isBefore(dataInicio)) {
            throw new IllegalArgumentException("Data fim nao pode ser anterior a data inicio");
        }
    }

    private OperadoresRecepcaoResultado listarRecepcaoOperadores(Long unidadeId,
                                                                 LocalDateTime inicio,
                                                                 LocalDateTime fimExclusivo) {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList("""
                with operadores as (
                    select distinct
                        u.id as usuario_id,
                        coalesce(nullif(trim(c.nome), ''), nullif(trim(u.username), ''), 'SEM OPERADOR') as operador_nome,
                        u.username as username
                    from colaborador_unidade_atuacao cua
                    join colaborador_unidade_vinculo cuv on cuv.id = cua.colaborador_unidade_vinculo_id
                    join usuario_colaborador uc on uc.colaborador_id = cuv.colaborador_id
                    join usuario u on u.id = uc.usuario_id
                    join perfil_permissao pp on pp.perfil_id = cua.perfil_id
                    join permissao p on p.id = pp.permissao_id
                    left join colaborador c on c.id = cuv.colaborador_id
                    where cuv.unidade_id = ?
                      and u.ativo = true
                      and uc.ativo = true
                      and cuv.ativo = true
                      and cua.ativo = true
                      and upper(p.nome) = 'RECEPCAO_EXECUTAR'
                ),
                quantidade_por_operador as (
                    select
                        ap.usuario_inicio_id as usuario_id,
                        count(*) as quantidade,
                        count(*) filter (where ap.fim_em is not null and ap.fim_em > ap.inicio_em) as quantidade_com_tempo,
                        coalesce(sum(
                            case
                                when ap.fim_em is not null and ap.fim_em > ap.inicio_em
                                    then extract(epoch from (ap.fim_em - ap.inicio_em)) / 60.0
                                else 0
                            end
                        ), 0) as soma_minutos
                    from atendimento_periodo ap
                    join atendimento a on a.id = ap.atendimento_id
                    where a.unidade_id = ?
                      and ap.tipo = 'RECEPCAO'
                      and ap.inicio_em >= ?
                      and ap.inicio_em < ?
                      and ap.usuario_inicio_id is not null
                    group by ap.usuario_inicio_id
                )
                select
                    o.usuario_id,
                    o.operador_nome,
                    o.username,
                    coalesce(q.quantidade, 0) as quantidade,
                    coalesce(q.quantidade_com_tempo, 0) as quantidade_com_tempo,
                    coalesce(q.soma_minutos, 0) as soma_minutos
                from operadores o
                left join quantidade_por_operador q on q.usuario_id = o.usuario_id
                order by quantidade desc, o.operador_nome
                """, unidadeId, unidadeId, inicio, fimExclusivo);

        List<LeanRecepcaoOperadorItemResponse> itens = new ArrayList<>();
        double somaMinutos = 0d;
        long totalRegistrosComTempo = 0L;
        for (Map<String, Object> row : rows) {
            Long usuarioId = row.get("usuario_id") == null ? null : ((Number) row.get("usuario_id")).longValue();
            long quantidade = row.get("quantidade") == null ? 0L : ((Number) row.get("quantidade")).longValue();
            long quantidadeComTempo = row.get("quantidade_com_tempo") == null
                    ? 0L
                    : ((Number) row.get("quantidade_com_tempo")).longValue();
            double somaMinutosOperador = row.get("soma_minutos") == null
                    ? 0d
                    : ((Number) row.get("soma_minutos")).doubleValue();
            String operador = row.get("operador_nome") == null ? "SEM OPERADOR" : row.get("operador_nome").toString();
            String username = row.get("username") == null ? "-" : row.get("username").toString();
            Double mediaTempoRecepcaoMinutos = quantidadeComTempo > 0
                    ? round2(somaMinutosOperador / quantidadeComTempo)
                    : null;
            itens.add(new LeanRecepcaoOperadorItemResponse(
                    usuarioId,
                    operador,
                    username,
                    quantidade,
                    mediaTempoRecepcaoMinutos));
            somaMinutos += somaMinutosOperador;
            totalRegistrosComTempo += quantidadeComTempo;
        }
        return new OperadoresRecepcaoResultado(itens, somaMinutos, totalRegistrosComTempo);
    }

    private OperadoresClassificacaoResultado listarClassificacaoOperadores(Long unidadeId,
                                                                           LocalDateTime inicio,
                                                                           LocalDateTime fimExclusivo) {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList("""
                with operadores as (
                    select distinct
                        u.id as usuario_id,
                        coalesce(nullif(trim(c.nome), ''), nullif(trim(u.username), ''), 'SEM OPERADOR') as operador_nome,
                        u.username as username
                    from colaborador_unidade_atuacao cua
                    join colaborador_unidade_vinculo cuv on cuv.id = cua.colaborador_unidade_vinculo_id
                    join usuario_colaborador uc on uc.colaborador_id = cuv.colaborador_id
                    join usuario u on u.id = uc.usuario_id
                    join perfil_permissao pp on pp.perfil_id = cua.perfil_id
                    join permissao p on p.id = pp.permissao_id
                    left join colaborador c on c.id = cuv.colaborador_id
                    where cuv.unidade_id = ?
                      and u.ativo = true
                      and uc.ativo = true
                      and cuv.ativo = true
                      and cua.ativo = true
                      and upper(p.nome) = 'TRIAGEM_EXECUTAR'
                ),
                quantidade_por_operador as (
                    select
                        ap.usuario_inicio_id as usuario_id,
                        count(*) as quantidade,
                        count(*) filter (where ap.fim_em is not null and ap.fim_em > ap.inicio_em) as quantidade_com_tempo,
                        coalesce(sum(
                            case
                                when ap.fim_em is not null and ap.fim_em > ap.inicio_em
                                    then extract(epoch from (ap.fim_em - ap.inicio_em)) / 60.0
                                else 0
                            end
                        ), 0) as soma_minutos
                    from atendimento_periodo ap
                    join atendimento a on a.id = ap.atendimento_id
                    where a.unidade_id = ?
                      and ap.tipo = 'TRIAGEM'
                      and ap.inicio_em >= ?
                      and ap.inicio_em < ?
                      and ap.usuario_inicio_id is not null
                    group by ap.usuario_inicio_id
                )
                select
                    o.usuario_id,
                    o.operador_nome,
                    o.username,
                    coalesce(q.quantidade, 0) as quantidade,
                    coalesce(q.quantidade_com_tempo, 0) as quantidade_com_tempo,
                    coalesce(q.soma_minutos, 0) as soma_minutos
                from operadores o
                left join quantidade_por_operador q on q.usuario_id = o.usuario_id
                order by quantidade desc, o.operador_nome
                """, unidadeId, unidadeId, inicio, fimExclusivo);

        List<LeanClassificacaoOperadorItemResponse> itens = new ArrayList<>();
        double somaMinutos = 0d;
        long totalRegistrosComTempo = 0L;
        for (Map<String, Object> row : rows) {
            Long usuarioId = row.get("usuario_id") == null ? null : ((Number) row.get("usuario_id")).longValue();
            long quantidade = row.get("quantidade") == null ? 0L : ((Number) row.get("quantidade")).longValue();
            long quantidadeComTempo = row.get("quantidade_com_tempo") == null
                    ? 0L
                    : ((Number) row.get("quantidade_com_tempo")).longValue();
            double somaMinutosOperador = row.get("soma_minutos") == null
                    ? 0d
                    : ((Number) row.get("soma_minutos")).doubleValue();
            String operador = row.get("operador_nome") == null ? "SEM OPERADOR" : row.get("operador_nome").toString();
            String username = row.get("username") == null ? "-" : row.get("username").toString();
            Double mediaTempoClassificacaoMinutos = quantidadeComTempo > 0
                    ? round2(somaMinutosOperador / quantidadeComTempo)
                    : null;
            itens.add(new LeanClassificacaoOperadorItemResponse(
                    usuarioId,
                    operador,
                    username,
                    quantidade,
                    mediaTempoClassificacaoMinutos));
            somaMinutos += somaMinutosOperador;
            totalRegistrosComTempo += quantidadeComTempo;
        }
        return new OperadoresClassificacaoResultado(itens, somaMinutos, totalRegistrosComTempo);
    }

    private long contarAtendimentosChegada(Long unidadeId, LocalDateTime inicio, LocalDateTime fimExclusivo) {
        Long total = jdbcTemplate.queryForObject("""
                select count(a.id)
                from atendimento a
                where a.unidade_id = ?
                  and a.data_hora_chegada >= ?
                  and a.data_hora_chegada < ?
                """, Long.class, unidadeId, inicio, fimExclusivo);
        return total == null ? 0L : total;
    }

    private long contarAtendimentosComEntradaPorPerfil(Long unidadeId,
                                                       LocalDateTime inicio,
                                                       LocalDateTime fimExclusivo,
                                                       String perfilChegada) {
        StringBuilder sql = new StringBuilder("""
                select count(a.id)
                from atendimento a
                join entrada e on e.atendimento_id = a.id
                join forma_chegada fc on fc.id = e.forma_chegada_id
                where a.unidade_id = ?
                  and a.data_hora_chegada >= ?
                  and a.data_hora_chegada < ?
                """);
        List<Object> params = new ArrayList<>();
        params.add(unidadeId);
        params.add(inicio);
        params.add(fimExclusivo);

        if (perfilChegada != null) {
            sql.append(" and upper(fc.perfil_chegada) = ?");
            params.add(perfilChegada);
        }

        Long total = jdbcTemplate.queryForObject(sql.toString(), Long.class, params.toArray());
        return total == null ? 0L : total;
    }

    private List<LeanChegadaHoraResponse> calcularChegadasPorHora(Long unidadeId,
                                                                  LocalDateTime inicio,
                                                                  LocalDateTime fimExclusivo,
                                                                  long diasPeriodo) {
        Map<Integer, Long> totaisPorHora = new HashMap<>();
        List<Map<String, Object>> rows = jdbcTemplate.queryForList("""
                select extract(hour from a.data_hora_chegada)::int as hora, count(a.id) as total
                from atendimento a
                where a.unidade_id = ?
                  and a.data_hora_chegada >= ?
                  and a.data_hora_chegada < ?
                group by 1
                """, unidadeId, inicio, fimExclusivo);
        for (Map<String, Object> row : rows) {
            int hora = ((Number) row.get("hora")).intValue();
            long total = ((Number) row.get("total")).longValue();
            totaisPorHora.put(hora, total);
        }

        List<LeanChegadaHoraResponse> saida = new ArrayList<>();
        for (int hora = 0; hora <= 23; hora++) {
            long totalHora = totaisPorHora.getOrDefault(hora, 0L);
            double media = totalHora / (double) diasPeriodo;
            saida.add(new LeanChegadaHoraResponse(hora, round2(media)));
        }
        return saida;
    }

    private List<LeanChegadaDiaSemanaResponse> calcularChegadasPorDiaSemana(Long unidadeId,
                                                                             LocalDate dataInicio,
                                                                             LocalDate dataFim,
                                                                             LocalDateTime inicio,
                                                                             LocalDateTime fimExclusivo) {
        Map<Integer, Long> totaisPorDiaSemana = new HashMap<>();
        List<Map<String, Object>> rows = jdbcTemplate.queryForList("""
                select extract(dow from a.data_hora_chegada)::int as dia, count(a.id) as total
                from atendimento a
                where a.unidade_id = ?
                  and a.data_hora_chegada >= ?
                  and a.data_hora_chegada < ?
                group by 1
                """, unidadeId, inicio, fimExclusivo);
        for (Map<String, Object> row : rows) {
            int dia = ((Number) row.get("dia")).intValue();
            long total = ((Number) row.get("total")).longValue();
            totaisPorDiaSemana.put(dia, total);
        }

        Map<Integer, Long> ocorrenciasPorDia = new HashMap<>();
        LocalDate cursor = dataInicio;
        while (!cursor.isAfter(dataFim)) {
            int postgresDow = toPostgresDow(cursor.getDayOfWeek());
            ocorrenciasPorDia.put(postgresDow, ocorrenciasPorDia.getOrDefault(postgresDow, 0L) + 1L);
            cursor = cursor.plusDays(1);
        }

        List<LeanChegadaDiaSemanaResponse> saida = new ArrayList<>();
        for (int dow = 0; dow <= 6; dow++) {
            long totalDia = totaisPorDiaSemana.getOrDefault(dow, 0L);
            long ocorrencias = ocorrenciasPorDia.getOrDefault(dow, 0L);
            Double media = ocorrencias > 0 ? round2(totalDia / (double) ocorrencias) : null;
            saida.add(new LeanChegadaDiaSemanaResponse(dow, nomeDiaSemana(dow), media));
        }
        return saida;
    }

    private int toPostgresDow(DayOfWeek dayOfWeek) {
        return dayOfWeek == DayOfWeek.SUNDAY ? 0 : dayOfWeek.getValue();
    }

    private String nomeDiaSemana(int postgresDow) {
        return switch (postgresDow) {
            case 0 -> "DOMINGO";
            case 1 -> "SEGUNDA";
            case 2 -> "TERCA";
            case 3 -> "QUARTA";
            case 4 -> "QUINTA";
            case 5 -> "SEXTA";
            case 6 -> "SABADO";
            default -> "N/A";
        };
    }

    private long contarAtendimentosPorMotivoDesfecho(Long unidadeId,
                                                     LocalDateTime inicio,
                                                     LocalDateTime fimExclusivo,
                                                     String motivoDescricao) {
        Long total = jdbcTemplate.queryForObject("""
                select count(distinct a.id)
                from atendimento a
                where a.unidade_id = ?
                  and a.data_hora_chegada >= ?
                  and a.data_hora_chegada < ?
                  and exists (
                    select 1
                    from desfecho d
                    join motivo_desfecho md on md.id = d.motivo_desfecho_id
                    where d.atendimento_id = a.id
                      and upper(md.descricao) = upper(?)
                  )
                """, Long.class, unidadeId, inicio, fimExclusivo, motivoDescricao);
        return total == null ? 0L : total;
    }

    private long contarAtendimentosPorStatusOuMotivo(Long unidadeId,
                                                     LocalDateTime inicio,
                                                     LocalDateTime fimExclusivo,
                                                     String statusCodigo,
                                                     String motivoDescricao) {
        Long total = jdbcTemplate.queryForObject("""
                select count(distinct a.id)
                from atendimento a
                left join status_atendimento sa on sa.id = a.status_id
                where a.unidade_id = ?
                  and a.data_hora_chegada >= ?
                  and a.data_hora_chegada < ?
                  and (
                    upper(coalesce(sa.codigo, '')) = upper(?)
                    or exists (
                        select 1
                        from desfecho d
                        join motivo_desfecho md on md.id = d.motivo_desfecho_id
                        where d.atendimento_id = a.id
                          and upper(md.descricao) = upper(?)
                    )
                  )
                """, Long.class, unidadeId, inicio, fimExclusivo, statusCodigo, motivoDescricao);
        return total == null ? 0L : total;
    }

    private boolean existeStatusOuMotivo(String valor) {
        Boolean existe = jdbcTemplate.queryForObject("""
                select exists (
                    select 1
                    from status_atendimento s
                    where upper(s.codigo) = upper(?)
                ) or exists (
                    select 1
                    from motivo_desfecho m
                    where upper(m.descricao) = upper(?)
                )
                """, Boolean.class, valor, valor);
        return Boolean.TRUE.equals(existe);
    }

    private long contarClassificacoes(Long unidadeId, LocalDateTime inicio, LocalDateTime fimExclusivo) {
        Long total = jdbcTemplate.queryForObject("""
                select count(cr.id)
                from classificacao_risco cr
                join atendimento a on a.id = cr.atendimento_id
                where a.unidade_id = ?
                  and a.data_hora_chegada >= ?
                  and a.data_hora_chegada < ?
                """, Long.class, unidadeId, inicio, fimExclusivo);
        return total == null ? 0L : total;
    }

    private long contarClassificacoesRiscoMaior(Long unidadeId, LocalDateTime inicio, LocalDateTime fimExclusivo) {
        Long total = jdbcTemplate.queryForObject("""
                select count(cr.id)
                from classificacao_risco cr
                join atendimento a on a.id = cr.atendimento_id
                join classificacao_cor cc on cc.id = cr.classificacao_cor_id
                where a.unidade_id = ?
                  and a.data_hora_chegada >= ?
                  and a.data_hora_chegada < ?
                  and cc.risco_maior = true
                """, Long.class, unidadeId, inicio, fimExclusivo);
        return total == null ? 0L : total;
    }

    private List<LeanTriagemCorDistribuicaoResponse> listarDistribuicaoTriagem(Long unidadeId,
                                                                                LocalDateTime inicio,
                                                                                LocalDateTime fimExclusivo,
                                                                                long totalClassificacoes) {
        List<LeanTriagemCorDistribuicaoResponse> itens = new ArrayList<>();
        List<Map<String, Object>> rows = jdbcTemplate.queryForList("""
                select upper(coalesce(cc.descricao, 'NAO INFORMADO')) as cor, count(cr.id) as total
                from classificacao_risco cr
                join atendimento a on a.id = cr.atendimento_id
                left join classificacao_cor cc on cc.id = cr.classificacao_cor_id
                where a.unidade_id = ?
                  and a.data_hora_chegada >= ?
                  and a.data_hora_chegada < ?
                group by 1
                order by total desc, cor
                """, unidadeId, inicio, fimExclusivo);
        for (Map<String, Object> row : rows) {
            long quantidade = ((Number) row.get("total")).longValue();
            itens.add(new LeanTriagemCorDistribuicaoResponse(
                    String.valueOf(row.get("cor")),
                    quantidade,
                    percentual(quantidade, totalClassificacoes)));
        }
        return itens;
    }

    private TempoTriagemMetricas buscarTempoTriagem(Long unidadeId, LocalDateTime inicio, LocalDateTime fimExclusivo) {
        return jdbcTemplate.queryForObject("""
                select
                    avg(t.minutos) as media_minutos,
                    percentile_cont(0.5) within group (order by t.minutos) as mediana_minutos,
                    percentile_cont(0.9) within group (order by t.minutos) as p90_minutos,
                    count(*) as total_triagens
                from (
                    select extract(epoch from (ap.fim_em - ap.inicio_em)) / 60.0 as minutos
                    from atendimento_periodo ap
                    join atendimento a on a.id = ap.atendimento_id
                    where a.unidade_id = ?
                      and ap.tipo = 'TRIAGEM'
                      and ap.inicio_em >= ?
                      and ap.inicio_em < ?
                      and ap.fim_em is not null
                      and ap.fim_em > ap.inicio_em
                ) t
                """, (rs, rowNum) -> new TempoTriagemMetricas(
                rs.getObject("media_minutos") == null ? null : round2(rs.getDouble("media_minutos")),
                rs.getObject("mediana_minutos") == null ? null : round2(rs.getDouble("mediana_minutos")),
                rs.getObject("p90_minutos") == null ? null : round2(rs.getDouble("p90_minutos")),
                rs.getLong("total_triagens")
        ), unidadeId, inicio, fimExclusivo);
    }

    private TempoMinutosMetricas buscarTempoPsg01(Long unidadeId, LocalDateTime inicio, LocalDateTime fimExclusivo) {
        return jdbcTemplate.queryForObject("""
                select
                    avg(t.minutos) as media_minutos,
                    percentile_cont(0.5) within group (order by t.minutos) as mediana_minutos,
                    percentile_cont(0.9) within group (order by t.minutos) as p90_minutos,
                    count(*) as total_casos,
                    sum(case when t.usou_fallback then 1 else 0 end) as total_fallback
                from (
                    select
                        extract(epoch from (d.data_hora - a.data_hora_chegada)) / 60.0 as minutos,
                        false as usou_fallback
                    from desfecho d
                    join atendimento a on a.id = d.atendimento_id
                    join tipo_desfecho td on td.id = d.tipo_desfecho_id
                    left join motivo_desfecho md on md.id = d.motivo_desfecho_id
                    where a.unidade_id = ?
                      and d.data_hora >= ?
                      and d.data_hora < ?
                      and upper(td.descricao) = 'ATENDIMENTO'
                      and upper(coalesce(md.descricao, '')) not in ('EVASAO', 'ABANDONO')
                      and d.data_hora > a.data_hora_chegada
                ) t
                """, (rs, rowNum) -> new TempoMinutosMetricas(
                rs.getObject("media_minutos") == null ? null : round2(rs.getDouble("media_minutos")),
                rs.getObject("mediana_minutos") == null ? null : round2(rs.getDouble("mediana_minutos")),
                rs.getObject("p90_minutos") == null ? null : round2(rs.getDouble("p90_minutos")),
                rs.getLong("total_casos"),
                rs.getObject("total_fallback") == null ? 0L : rs.getLong("total_fallback")
        ), unidadeId, inicio, fimExclusivo);
    }

    private TempoMinutosMetricas buscarTempoPsg02(Long unidadeId, LocalDateTime inicio, LocalDateTime fimExclusivo) {
        return jdbcTemplate.queryForObject("""
                with primeira_definitiva as (
                    select
                        i.atendimento_id,
                        min(lo.data_hora_entrada) as chegada_definitiva
                    from internacao i
                    join leito_ocupacao lo on lo.internacao_id = i.id
                    join leito_ocupacao_tipo lot on lot.id = lo.tipo_ocupacao_id
                    join leito l on l.id = lo.leito_id
                    where i.data_hora_cancelamento is null
                      and upper(lot.codigo) = 'DEFINITIVA'
                      and l.permite_destino_definitivo = true
                    group by i.atendimento_id
                )
                select
                    avg(t.minutos) as media_minutos,
                    percentile_cont(0.5) within group (order by t.minutos) as mediana_minutos,
                    percentile_cont(0.9) within group (order by t.minutos) as p90_minutos,
                    count(*) as total_casos,
                    sum(case when t.usou_fallback then 1 else 0 end) as total_fallback
                from (
                    select
                        extract(epoch from (pd.chegada_definitiva - a.data_hora_chegada)) / 60.0 as minutos,
                        false as usou_fallback
                    from primeira_definitiva pd
                    join atendimento a on a.id = pd.atendimento_id
                    where a.unidade_id = ?
                      and pd.chegada_definitiva >= ?
                      and pd.chegada_definitiva < ?
                      and pd.chegada_definitiva > a.data_hora_chegada
                ) t
                """, (rs, rowNum) -> new TempoMinutosMetricas(
                rs.getObject("media_minutos") == null ? null : round2(rs.getDouble("media_minutos")),
                rs.getObject("mediana_minutos") == null ? null : round2(rs.getDouble("mediana_minutos")),
                rs.getObject("p90_minutos") == null ? null : round2(rs.getDouble("p90_minutos")),
                rs.getLong("total_casos"),
                rs.getObject("total_fallback") == null ? 0L : rs.getLong("total_fallback")
        ), unidadeId, inicio, fimExclusivo);
    }

    private TempoMinutosMetricas buscarTempoPsg03(Long unidadeId, LocalDateTime inicio, LocalDateTime fimExclusivo) {
        return jdbcTemplate.queryForObject("""
                with primeira_definitiva as (
                    select
                        i.atendimento_id,
                        min(lo.data_hora_entrada) as chegada_definitiva
                    from internacao i
                    join leito_ocupacao lo on lo.internacao_id = i.id
                    join leito_ocupacao_tipo lot on lot.id = lo.tipo_ocupacao_id
                    join leito l on l.id = lo.leito_id
                    where i.data_hora_cancelamento is null
                      and upper(lot.codigo) = 'DEFINITIVA'
                      and l.permite_destino_definitivo = true
                    group by i.atendimento_id
                )
                select
                    avg(t.minutos) as media_minutos,
                    percentile_cont(0.5) within group (order by t.minutos) as mediana_minutos,
                    percentile_cont(0.9) within group (order by t.minutos) as p90_minutos,
                    count(*) as total_casos,
                    sum(case when t.usou_fallback then 1 else 0 end) as total_fallback
                from (
                    select
                        extract(epoch from (
                            coalesce(pd.chegada_definitiva, d.data_hora) - i.data_hora_decisao_internacao
                        )) / 60.0 as minutos,
                        (pd.chegada_definitiva is null and d.data_hora is not null) as usou_fallback
                    from internacao i
                    join atendimento a on a.id = i.atendimento_id
                    left join primeira_definitiva pd on pd.atendimento_id = i.atendimento_id
                    left join desfecho d on d.atendimento_id = i.atendimento_id
                    where a.unidade_id = ?
                      and i.data_hora_cancelamento is null
                      and i.data_hora_decisao_internacao is not null
                      and coalesce(pd.chegada_definitiva, d.data_hora) >= ?
                      and coalesce(pd.chegada_definitiva, d.data_hora) < ?
                      and coalesce(pd.chegada_definitiva, d.data_hora) > i.data_hora_decisao_internacao
                ) t
                """, (rs, rowNum) -> new TempoMinutosMetricas(
                rs.getObject("media_minutos") == null ? null : round2(rs.getDouble("media_minutos")),
                rs.getObject("mediana_minutos") == null ? null : round2(rs.getDouble("mediana_minutos")),
                rs.getObject("p90_minutos") == null ? null : round2(rs.getDouble("p90_minutos")),
                rs.getLong("total_casos"),
                rs.getObject("total_fallback") == null ? 0L : rs.getLong("total_fallback")
        ), unidadeId, inicio, fimExclusivo);
    }

    private long contarSalasClassificacao(Long unidadeId) {
        Long total = jdbcTemplate.queryForObject("""
                select count(distinct a.id)
                from area a
                join area_capacidade ac on ac.area_id = a.id
                join capacidade_area ca on ca.id = ac.capacidade_area_id
                where a.unidade_id = ?
                  and a.dt_cancelamento is null
                  and ca.ativo = true
                  and upper(ca.nome) = 'SALA_CLASSIFICACAO'
                """, Long.class, unidadeId);
        return total == null ? 0L : total;
    }

    private Double percentual(long numerador, long denominador) {
        if (denominador <= 0) {
            return null;
        }
        return round2((numerador * 100d) / denominador);
    }

    private Double round2(double valor) {
        return BigDecimal.valueOf(valor).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    private String formatNullable(Double valor) {
        return valor == null ? "N/A" : round2(valor).toString();
    }

    private LocalDateTime toLocalDateTime(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof LocalDateTime localDateTime) {
            return localDateTime;
        }
        if (value instanceof Timestamp timestamp) {
            return timestamp.toLocalDateTime();
        }
        throw new IllegalArgumentException("Tipo de data/hora inesperado: " + value.getClass().getName());
    }

    private CalculoOcupacao calcularOcupacao(Long unidadeId,
                                             LocalDateTime inicio,
                                             LocalDateTime fimExclusivo,
                                             long minutosPeriodo,
                                             String perfilLeitoCodigo,
                                             String tipoLeitoCodigo) {
        long leitosFixosElegiveis = contarLeitosElegiveis(
                unidadeId,
                perfilLeitoCodigo,
                tipoLeitoCodigo,
                false);
        long leitosTotaisElegiveis = contarLeitosElegiveis(
                unidadeId,
                perfilLeitoCodigo,
                tipoLeitoCodigo,
                true);
        double minutosOcupados = somarMinutosOcupados(
                unidadeId,
                inicio,
                fimExclusivo,
                perfilLeitoCodigo,
                tipoLeitoCodigo);
        long minutosDisponiveisNominal = leitosFixosElegiveis * minutosPeriodo;
        long minutosDisponiveisOperacional = leitosTotaisElegiveis * minutosPeriodo;
        Double taxaNominalPercentual = minutosDisponiveisNominal > 0
                ? BigDecimal.valueOf((minutosOcupados / minutosDisponiveisNominal) * 100d)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue()
                : null;
        Double taxaOperacionalPercentual = minutosDisponiveisOperacional > 0
                ? BigDecimal.valueOf((minutosOcupados / minutosDisponiveisOperacional) * 100d)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue()
                : null;
        return new CalculoOcupacao(
                leitosFixosElegiveis,
                leitosTotaisElegiveis,
                BigDecimal.valueOf(minutosOcupados).setScale(2, RoundingMode.HALF_UP).doubleValue(),
                minutosDisponiveisNominal,
                minutosDisponiveisOperacional,
                taxaNominalPercentual,
                taxaOperacionalPercentual);
    }

    private long contarLeitosElegiveis(Long unidadeId,
                                       String perfilLeitoCodigo,
                                       String tipoLeitoCodigo,
                                       boolean incluirVirtualSuperlotacao) {
        StringBuilder sql = new StringBuilder("""
                select count(l.id)
                from leito l
                join tipo_leito tl on tl.id = l.tipo_leito_id
                left join perfil_leito pl on pl.id = l.perfil_leito_id
                join natureza_operacional_leito nol on nol.id = l.natureza_operacional_id
                where l.unidade_id = ?
                  and l.ativo = true
                  and l.assistencial = true
                """);
        List<Object> params = new ArrayList<>();
        params.add(unidadeId);

        sql.append(incluirVirtualSuperlotacao
                ? " and nol.considera_taxa_operacional = true"
                : " and nol.considera_taxa_nominal = true");
        if (perfilLeitoCodigo != null) {
            sql.append(" and upper(coalesce(pl.descricao, '')) = ?");
            params.add(perfilLeitoCodigo);
        }
        if (tipoLeitoCodigo != null) {
            sql.append(" and upper(tl.descricao) = ?");
            params.add(tipoLeitoCodigo);
        }

        Long count = jdbcTemplate.queryForObject(sql.toString(), Long.class, params.toArray());
        return count == null ? 0L : count;
    }

    private double somarMinutosOcupados(Long unidadeId,
                                        LocalDateTime inicio,
                                        LocalDateTime fimExclusivo,
                                        String perfilLeitoCodigo,
                                        String tipoLeitoCodigo) {
        StringBuilder sql = new StringBuilder("""
                select coalesce(sum(
                    extract(epoch from (
                        least(coalesce(lo.data_hora_saida, ?), ?)
                        - greatest(lo.data_hora_entrada, ?)
                    )) / 60.0
                ), 0)
                from leito_ocupacao lo
                join leito l on l.id = lo.leito_id
                join tipo_leito tl on tl.id = l.tipo_leito_id
                left join perfil_leito pl on pl.id = l.perfil_leito_id
                left join internacao i on i.id = lo.internacao_id
                left join observacao o on o.id = lo.observacao_id
                where l.unidade_id = ?
                  and l.ativo = true
                  and l.assistencial = true
                  and lo.data_hora_entrada < ?
                  and coalesce(lo.data_hora_saida, ?) > ?
                  and (i.id is null or i.data_hora_cancelamento is null)
                  and (o.id is null or o.data_hora_cancelamento is null)
                """);

        List<Object> params = new ArrayList<>();
        params.add(fimExclusivo);
        params.add(fimExclusivo);
        params.add(inicio);
        params.add(unidadeId);
        params.add(fimExclusivo);
        params.add(fimExclusivo);
        params.add(inicio);

        if (perfilLeitoCodigo != null) {
            sql.append(" and upper(coalesce(pl.descricao, '')) = ?");
            params.add(perfilLeitoCodigo);
        }
        if (tipoLeitoCodigo != null) {
            sql.append(" and upper(tl.descricao) = ?");
            params.add(tipoLeitoCodigo);
        }

        Double minutes = jdbcTemplate.queryForObject(sql.toString(), Double.class, params.toArray());
        return minutes == null ? 0d : minutes;
    }

    private List<String> listarTiposLeitoAtivos() {
        return jdbcTemplate.queryForList("""
                select upper(descricao)
                from tipo_leito
                where ativo = true
                order by upper(descricao)
                """, String.class);
    }

    private record TempoTriagemMetricas(Double mediaMinutos,
                                        Double medianaMinutos,
                                        Double p90Minutos,
                                        Long totalTriagensComTempo) {
    }

    private record TempoMinutosMetricas(Double mediaMinutos,
                                        Double medianaMinutos,
                                        Double p90Minutos,
                                        Long totalCasos,
                                        Long totalFallbackDesfecho) {
    }

    private record CalculoOcupacao(long leitosFixosElegiveis,
                                   long leitosTotaisElegiveis,
                                   double minutosOcupados,
                                   long minutosDisponiveisNominal,
                                   long minutosDisponiveisOperacional,
                                   Double taxaNominalPercentual,
                                   Double taxaOperacionalPercentual) {
    }

    private record OperadoresRecepcaoResultado(List<LeanRecepcaoOperadorItemResponse> operadores,
                                               double somaMinutos,
                                               long totalRegistrosComTempo) {
    }

    private record OperadoresClassificacaoResultado(List<LeanClassificacaoOperadorItemResponse> operadores,
                                                    double somaMinutos,
                                                    long totalRegistrosComTempo) {
    }

    private enum TaxaOcupacaoCategoria {
        TO_01("TO-01", "Taxa de ocupacao do hospital", null, null),
        TO_02("TO-02", "Taxa de ocupacao dos leitos clinicos", "CLINICO", null),
        TO_03("TO-03", "Taxa de ocupacao dos leitos cirurgicos", "CIRURGICO", null),
        TO_04("TO-04", "Taxa de ocupacao dos leitos de UTI geral", null, "UTI_GERAL"),
        TO_05("TO-05", "Taxa de ocupacao dos leitos de semi-intensiva", null, "SEMI_INTENSIVA");

        private final String codigo;
        private final String descricao;
        private final String perfilLeitoCodigo;
        private final String tipoLeitoCodigo;

        TaxaOcupacaoCategoria(String codigo, String descricao, String perfilLeitoCodigo, String tipoLeitoCodigo) {
            this.codigo = codigo;
            this.descricao = descricao;
            this.perfilLeitoCodigo = perfilLeitoCodigo;
            this.tipoLeitoCodigo = tipoLeitoCodigo;
        }
    }
}


