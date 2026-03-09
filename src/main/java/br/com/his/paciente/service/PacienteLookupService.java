package br.com.his.paciente.service;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import br.com.his.configuracao.repository.BairroRepository;
import br.com.his.configuracao.repository.CidadeRepository;
import br.com.his.paciente.dto.PacienteLookupOption;
import br.com.his.paciente.dto.ProcedenciaEntradaOption;
import br.com.his.paciente.dto.PacienteForm;
import br.com.his.paciente.model.lookup.Sexo;
import br.com.his.paciente.repository.SexoRepository;

@Service
public class PacienteLookupService {

    private static final String FILTER_SEM_NAO_INFORMADO = """
            where %s not ilike '%%NÃO INFORMAD%%'
              and %s not ilike '%%NAO INFORMAD%%'
              and %s not ilike 'PREFIRO NÃO INFORMAR%%'
              and %s not ilike 'PREFIRO NAO INFORMAR%%'
            """;

    private final JdbcTemplate jdbcTemplate;
    private final BairroRepository bairroRepository;
    private final CidadeRepository cidadeRepository;
    private final SexoRepository sexoRepository;

    public PacienteLookupService(JdbcTemplate jdbcTemplate,
                                BairroRepository bairroRepository,
                                CidadeRepository cidadeRepository,
                                SexoRepository sexoRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.bairroRepository = bairroRepository;
        this.cidadeRepository = cidadeRepository;
        this.sexoRepository = sexoRepository;
    }

    public List<Sexo> listarSexos() {
        return sexoRepository.findByAtivoTrueOrderByIdAsc();
    }

    public List<PacienteLookupOption> listarRacasCor() {
        return listar("select id, descricao from raca_cor %s order by id", "descricao");
    }

    public List<PacienteLookupOption> listarEtniasIndigenas() {
        return listar("select id, descricao from etnia_indigena %s order by descricao", "descricao");
    }

    public List<PacienteLookupOption> listarNacionalidades() {
        return listar("select id, descricao from nacionalidade %s order by descricao", "descricao");
    }

    public List<PacienteLookupOption> listarNaturalidades() {
        return listar("select id, descricao from naturalidade %s order by descricao", "descricao");
    }

    public List<PacienteLookupOption> listarEstadosCivis() {
        return listar("select id, nome as descricao from estado_civil %s order by nome", "nome");
    }

    public List<PacienteLookupOption> listarEscolaridades() {
        return listar("select id, descricao from escolaridade %s order by id", "descricao");
    }

    public List<PacienteLookupOption> listarTiposSanguineos() {
        return listar("select id, descricao from tipo_sanguineo %s order by id", "descricao");
    }

    public List<PacienteLookupOption> listarOrientacoesSexuais() {
        return listar("select id, descricao from orientacao_sexual %s order by id", "descricao");
    }

    public List<PacienteLookupOption> listarIdentidadesGenero() {
        return listar("select id, descricao from identidade_genero %s order by id", "descricao");
    }

    public List<PacienteLookupOption> listarDeficiencias() {
        return listar("select id, descricao from deficiencia %s order by id", "descricao");
    }

    public List<PacienteLookupOption> listarProfissoes() {
        return listar("select id, nome as descricao from profissao %s order by nome", "nome");
    }

    public List<PacienteLookupOption> listarProcedencias() {
        return listar("select id, descricao from procedencia %s and unidade_id is null order by descricao", "descricao");
    }

    public List<ProcedenciaEntradaOption> listarProcedenciasEntrada(Long unidadeId) {
        String sql = """
                select p.id, p.descricao, p.tipo_procedencia_id
                from procedencia p
                where (unidade_id is null or unidade_id = ?)
                  and p.descricao not ilike '%%NÃO INFORMAD%%'
                  and p.descricao not ilike '%%NAO INFORMAD%%'
                  and p.descricao not ilike 'PREFIRO NÃO INFORMAR%%'
                  and p.descricao not ilike 'PREFIRO NAO INFORMAR%%'
                order by p.descricao
                """;
        return jdbcTemplate.query(sql,
                (rs, rowNum) -> new ProcedenciaEntradaOption(
                        rs.getLong("id"),
                        rs.getString("descricao"),
                        rs.getLong("tipo_procedencia_id")),
                unidadeId);
    }

    public List<PacienteLookupOption> listarTiposProcedenciaEntrada() {
        return jdbcTemplate.query("""
                        select id, descricao
                        from tipo_procedencia
                        order by descricao
                        """,
                (rs, rowNum) -> new PacienteLookupOption(rs.getLong("id"), rs.getString("descricao")));
    }

    public List<PacienteLookupOption> listarBairrosPorCidade(Long cidadeId) {
        if (cidadeId == null) {
            return List.of();
        }
        return bairroRepository.findAtivosByCidadeIdOrderByNome(cidadeId)
                .stream()
                .map(bairro -> new PacienteLookupOption(bairro.getId(), bairro.getNome()))
                .toList();
    }

    public List<PacienteLookupOption> listarCidadesProcedenciaEntrada() {
        return cidadeRepository.findAllWithUnidadeFederativaOrderByNome()
                .stream()
                .map(cidade -> new PacienteLookupOption(
                        cidade.getId(),
                        cidade.getNome() + " - " + cidade.getUnidadeFederativa().getSigla(),
                        cidade.getUnidadeFederativa().getId()))
                .toList();
    }

    public void validarReferencias(PacienteForm form) {
        validar(form.getRacaCorId(), "select count(1) from raca_cor where id = ?", "Raça ou Cor invalida");
        validar(form.getEtniaIndigenaId(), "select count(1) from etnia_indigena where id = ?", "Etnia indigena invalida");
        validar(form.getNacionalidadeId(), "select count(1) from nacionalidade where id = ?", "Nacionalidade invalida");
        validar(form.getNaturalidadeId(), "select count(1) from naturalidade where id = ?", "Naturalidade invalida");
        validar(form.getEstadoCivilId(), "select count(1) from estado_civil where id = ?", "Estado civil invalido");
        validar(form.getEscolaridadeId(), "select count(1) from escolaridade where id = ?", "Escolaridade invalida");
        validar(form.getTipoSanguineoId(), "select count(1) from tipo_sanguineo where id = ?", "Tipo sanguineo invalido");
        validar(form.getOrientacaoSexualId(), "select count(1) from orientacao_sexual where id = ?", "Orientacao sexual invalida");
        validar(form.getIdentidadeGeneroId(), "select count(1) from identidade_genero where id = ?", "Identidade de genero invalida");
        validar(form.getDeficienciaId(), "select count(1) from deficiencia where id = ?", "Deficiencia invalida");
        validar(form.getProfissaoId(), "select count(1) from profissao where id = ?", "Profissao invalida");
        validar(form.getProcedenciaId(), "select count(1) from procedencia where id = ?", "Procedencia invalida");
        validarCodigo(form.getSexo(), "select count(1) from sexo where upper(codigo) = upper(?) and ativo = true", "Sexo invalido");

        if (Long.valueOf(7L).equals(form.getRacaCorId()) && form.getEtniaIndigenaId() == null) {
            throw new IllegalArgumentException("A Etnia Indigena e obrigatoria quando a Raça/Cor e indigena");
        }
    }

    private List<PacienteLookupOption> listar(String baseSql, String column) {
        String filter = FILTER_SEM_NAO_INFORMADO.formatted(column, column, column, column);
        return jdbcTemplate.query(baseSql.formatted(filter),
                (rs, rowNum) -> new PacienteLookupOption(rs.getLong("id"), rs.getString("descricao")));
    }

    private void validar(Long id, String sql, String mensagem) {
        if (id == null) {
            return;
        }
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        if (count == null || count == 0) {
            throw new IllegalArgumentException(mensagem);
        }
    }

    private void validarCodigo(String value, String sql, String mensagem) {
        if (value == null || value.isBlank()) {
            return;
        }
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, value.trim());
        if (count == null || count == 0) {
            throw new IllegalArgumentException(mensagem);
        }
    }
}
