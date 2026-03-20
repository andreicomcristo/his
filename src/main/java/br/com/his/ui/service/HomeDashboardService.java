package br.com.his.ui.service;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.his.ui.dto.HomeDashboardData;

@Service
public class HomeDashboardService {

    private static final Locale LOCALE_PT_BR = new Locale("pt", "BR");

    private final JdbcTemplate jdbcTemplate;

    public HomeDashboardService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional(readOnly = true)
    public HomeDashboardData carregar(Long unidadeId, LocalDate referencia) {
        LocalDate ref = referencia == null ? LocalDate.now() : referencia;
        LocalDateTime inicioDia = ref.atStartOfDay();
        LocalDateTime fimDia = ref.plusDays(1).atStartOfDay();

        long chegadasHoje = countLong("""
                select count(a.id)
                from atendimento a
                where a.unidade_id = ?
                  and a.data_hora_chegada >= ?
                  and a.data_hora_chegada < ?
                """, unidadeId, inicioDia, fimDia);

        long atendimentosAbertos = countLong("""
                select count(a.id)
                from atendimento a
                join status_atendimento sa on sa.id = a.status_id
                where a.unidade_id = ?
                  and upper(sa.codigo) in ('AGUARDANDO', 'EM_TRIAGEM', 'AGUARDANDO_RECEPCAO', 'AGUARDANDO_TRIAGEM', 'AGUARDANDO_MEDICO', 'EM_ATENDIMENTO')
                """, unidadeId);

        long aguardandoClassificacao = countByStatus(unidadeId, "AGUARDANDO_TRIAGEM");
        long aguardandoMedico = countByStatus(unidadeId, "AGUARDANDO_MEDICO");

        long emObservacao = countLong("""
                select count(o.id)
                from observacao o
                join atendimento a on a.id = o.atendimento_id
                where a.unidade_id = ?
                  and o.data_hora_cancelamento is null
                  and o.data_hora_fim is null
                """, unidadeId);

        long emInternacao = countLong("""
                select count(i.id)
                from internacao i
                join atendimento a on a.id = i.atendimento_id
                where a.unidade_id = ?
                  and i.data_hora_cancelamento is null
                  and i.data_hora_fim_internacao is null
                """, unidadeId);

        long leitosTotais = countLong("""
                select count(l.id)
                from leito l
                where l.unidade_id = ?
                  and l.dt_cancelamento is null
                  and l.assistencial = true
                """, unidadeId);

        long leitosVirtuais = countLong("""
                select count(l.id)
                from leito l
                join natureza_operacional_leito nol on nol.id = l.natureza_operacional_id
                where l.unidade_id = ?
                  and l.dt_cancelamento is null
                  and l.assistencial = true
                  and nol.virtual_superlotacao = true
                """, unidadeId);

        long leitosOcupados = countLong("""
                select count(distinct l.id)
                from leito l
                join leito_ocupacao lo on lo.leito_id = l.id and lo.data_hora_saida is null
                left join internacao i on i.id = lo.internacao_id
                left join observacao o on o.id = lo.observacao_id
                where l.unidade_id = ?
                  and l.dt_cancelamento is null
                  and l.assistencial = true
                  and (i.id is null or i.data_hora_cancelamento is null)
                  and (o.id is null or o.data_hora_cancelamento is null)
                """, unidadeId);

        long leitosLivres = Math.max(leitosTotais - leitosOcupados, 0L);
        Double taxaOcupacaoAtual = leitosTotais > 0
                ? (leitosOcupados * 100.0) / leitosTotais
                : null;

        List<HomeDashboardData.HomeCard> operacaoCards = List.of(
                new HomeDashboardData.HomeCard(
                        "Atendimentos em aberto",
                        formatInt(atendimentosAbertos),
                        "Status operacionais ativos na unidade",
                        "pe-7s-timer",
                        "bg-midnight-bloom"),
                new HomeDashboardData.HomeCard(
                        "Pacientes em observacao",
                        formatInt(emObservacao),
                        "Pacientes atualmente em leito de observacao",
                        "pe-7s-note2",
                        "bg-tempting-azure"),
                new HomeDashboardData.HomeCard(
                        "Pacientes em internacao",
                        formatInt(emInternacao),
                        "Pacientes atualmente em internacao",
                        "pe-7s-way",
                        "bg-arielle-smile"),
                new HomeDashboardData.HomeCard(
                        "Leitos ocupados",
                        formatInt(leitosOcupados),
                        "Leitos assistenciais com ocupacao aberta",
                        "pe-7s-server",
                        "bg-mean-fruit"),
                new HomeDashboardData.HomeCard(
                        "Leitos vagos",
                        formatInt(leitosLivres),
                        "Leitos assistenciais ativos e sem ocupacao",
                        "pe-7s-keypad",
                        "bg-grow-early"),
                new HomeDashboardData.HomeCard(
                        "Taxa de ocupacao atual",
                        formatPercent(taxaOcupacaoAtual),
                        "Base: " + formatInt(leitosOcupados) + "/" + formatInt(leitosTotais) + " leitos",
                        "pe-7s-graph1",
                        "bg-sunny-morning"),
                new HomeDashboardData.HomeCard(
                        "Leitos virtuais ativos",
                        formatInt(leitosVirtuais),
                        "Leitos de superlotacao habilitados na unidade",
                        "pe-7s-plus",
                        "bg-night-fade"),
                new HomeDashboardData.HomeCard(
                        "Chegadas hoje",
                        formatInt(chegadasHoje),
                        "Atendimentos com hora de chegada no dia",
                        "pe-7s-clock",
                        "bg-heavy-rain"),
                new HomeDashboardData.HomeCard(
                        "Aguardando classificacao",
                        formatInt(aguardandoClassificacao),
                        "Fila de triagem pendente",
                        "pe-7s-eyedropper",
                        "bg-amy-crisp"),
                new HomeDashboardData.HomeCard(
                        "Aguardando medico",
                        formatInt(aguardandoMedico),
                        "Fila para atendimento medico",
                        "pe-7s-id",
                        "bg-focus"));

        return new HomeDashboardData(
                ref,
                operacaoCards,
                listarFilaAtencao(unidadeId),
                listarDesfechosHoje(unidadeId, inicioDia, fimDia));
    }

    private long countByStatus(Long unidadeId, String statusCodigo) {
        return countLong("""
                select count(a.id)
                from atendimento a
                join status_atendimento sa on sa.id = a.status_id
                where a.unidade_id = ?
                  and upper(sa.codigo) = upper(?)
                """, unidadeId, statusCodigo);
    }

    private long countLong(String sql, Object... args) {
        Long value = jdbcTemplate.queryForObject(sql, Long.class, args);
        return value == null ? 0L : value;
    }

    private List<HomeDashboardData.HomeFilaItem> listarFilaAtencao(Long unidadeId) {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList("""
                select a.id as atendimento_id,
                       p.nome as paciente_nome,
                       coalesce(sa.descricao, sa.codigo) as status_descricao,
                       a.data_hora_chegada as chegada_em,
                       floor(extract(epoch from (now() - a.data_hora_chegada)) / 60.0)::bigint as minutos_espera
                from atendimento a
                join paciente p on p.id = a.paciente_id
                join status_atendimento sa on sa.id = a.status_id
                where a.unidade_id = ?
                  and upper(sa.codigo) in ('AGUARDANDO', 'EM_TRIAGEM', 'AGUARDANDO_RECEPCAO', 'AGUARDANDO_TRIAGEM', 'AGUARDANDO_MEDICO', 'EM_ATENDIMENTO')
                order by a.data_hora_chegada asc
                limit 8
                """, unidadeId);

        List<HomeDashboardData.HomeFilaItem> items = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            items.add(new HomeDashboardData.HomeFilaItem(
                    ((Number) row.get("atendimento_id")).longValue(),
                    (String) row.get("paciente_nome"),
                    (String) row.get("status_descricao"),
                    toLocalDateTime(row.get("chegada_em")),
                    row.get("minutos_espera") == null ? 0L : ((Number) row.get("minutos_espera")).longValue()));
        }
        return items;
    }

    private List<HomeDashboardData.HomeDesfechoItem> listarDesfechosHoje(Long unidadeId,
                                                                          LocalDateTime inicio,
                                                                          LocalDateTime fimExclusivo) {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList("""
                select coalesce(md.descricao, 'SEM MOTIVO') as motivo,
                       count(d.id) as total
                from desfecho d
                join atendimento a on a.id = d.atendimento_id
                left join motivo_desfecho md on md.id = d.motivo_desfecho_id
                where a.unidade_id = ?
                  and d.data_hora >= ?
                  and d.data_hora < ?
                group by 1
                order by total desc, motivo asc
                limit 8
                """, unidadeId, inicio, fimExclusivo);

        List<HomeDashboardData.HomeDesfechoItem> items = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            items.add(new HomeDashboardData.HomeDesfechoItem(
                    (String) row.get("motivo"),
                    row.get("total") == null ? 0L : ((Number) row.get("total")).longValue()));
        }
        return items;
    }

    private LocalDateTime toLocalDateTime(Object value) {
        if (value instanceof LocalDateTime localDateTime) {
            return localDateTime;
        }
        return null;
    }

    private String formatInt(long value) {
        return NumberFormat.getIntegerInstance(LOCALE_PT_BR).format(value);
    }

    private String formatPercent(Double value) {
        if (value == null) {
            return "N/A";
        }
        NumberFormat nf = NumberFormat.getNumberInstance(LOCALE_PT_BR);
        nf.setMinimumFractionDigits(2);
        nf.setMaximumFractionDigits(2);
        return nf.format(value) + "%";
    }
}
