package br.com.his.admin.service;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.his.admin.dto.PacienteCatalogoForm;
import br.com.his.admin.dto.PacienteCatalogoItem;
import br.com.his.admin.dto.PacienteCatalogoTipo;
import br.com.his.paciente.dto.PacienteLookupOption;

@Service
public class PacienteCatalogoAdminService {

    private final JdbcTemplate jdbcTemplate;

    public PacienteCatalogoAdminService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional(readOnly = true)
    public List<PacienteCatalogoItem> listar(PacienteCatalogoTipo tipo, String filtro) {
        String like = normalizeLike(filtro);
        if (like == null) {
            return listarSemFiltro(tipo);
        }
        return listarComFiltro(tipo, like);
    }

    private List<PacienteCatalogoItem> listarSemFiltro(PacienteCatalogoTipo tipo) {
        return switch (tipo) {
            case RACA_COR -> jdbcTemplate.query(
                    "select id, descricao, codigo from raca_cor order by descricao",
                    (rs, rowNum) -> {
                        PacienteCatalogoItem item = new PacienteCatalogoItem();
                        item.setId(rs.getLong("id"));
                        item.setPrincipal(rs.getString("descricao"));
                        item.setExtra(rs.getString("codigo"));
                        return item;
                    });
            case ESTADO_CIVIL -> jdbcTemplate.query(
                    "select id, nome, descricao from estado_civil order by nome",
                    (rs, rowNum) -> {
                        PacienteCatalogoItem item = new PacienteCatalogoItem();
                        item.setId(rs.getLong("id"));
                        item.setPrincipal(rs.getString("nome"));
                        item.setSecundario(rs.getString("descricao"));
                        return item;
                    });
            case NATURALIDADE -> jdbcTemplate.query(
                    "select id, descricao, cod_ibge from naturalidade order by descricao",
                    (rs, rowNum) -> {
                        PacienteCatalogoItem item = new PacienteCatalogoItem();
                        item.setId(rs.getLong("id"));
                        item.setPrincipal(rs.getString("descricao"));
                        item.setExtra(rs.getString("cod_ibge"));
                        return item;
                    });
            case PROFISSAO -> jdbcTemplate.query(
                    "select id, nome, cbo_cod from profissao order by nome",
                    (rs, rowNum) -> {
                        PacienteCatalogoItem item = new PacienteCatalogoItem();
                        item.setId(rs.getLong("id"));
                        item.setPrincipal(rs.getString("nome"));
                        item.setExtra(rs.getString("cbo_cod"));
                        return item;
                    });
            case PROCEDENCIA -> jdbcTemplate.query(
                    "select p.id, p.descricao, tp.descricao as tipo, u.nome as unidade from procedencia p left join tipo_procedencia tp on tp.id = p.tipo_procedencia_id left join unidade u on u.id = p.unidade_id order by p.descricao",
                    (rs, rowNum) -> {
                        PacienteCatalogoItem item = new PacienteCatalogoItem();
                        item.setId(rs.getLong("id"));
                        item.setPrincipal(rs.getString("descricao"));
                        item.setRelacionamento(rs.getString("tipo"));
                        item.setRelacionamentoSecundario(rs.getString("unidade"));
                        return item;
                    });
            default -> jdbcTemplate.query(
                    "select id, descricao from " + tipo.getTabela() + " order by descricao",
                    (rs, rowNum) -> {
                        PacienteCatalogoItem item = new PacienteCatalogoItem();
                        item.setId(rs.getLong("id"));
                        item.setPrincipal(rs.getString("descricao"));
                        return item;
                    });
        };
    }

    private List<PacienteCatalogoItem> listarComFiltro(PacienteCatalogoTipo tipo, String like) {
        return switch (tipo) {
            case RACA_COR -> jdbcTemplate.query(
                    "select id, descricao, codigo from raca_cor where upper(descricao) like ? or upper(coalesce(codigo,'')) like ? order by descricao",
                    (rs, rowNum) -> {
                        PacienteCatalogoItem item = new PacienteCatalogoItem();
                        item.setId(rs.getLong("id"));
                        item.setPrincipal(rs.getString("descricao"));
                        item.setExtra(rs.getString("codigo"));
                        return item;
                    }, like, like);
            case ESTADO_CIVIL -> jdbcTemplate.query(
                    "select id, nome, descricao from estado_civil where upper(nome) like ? or upper(coalesce(descricao,'')) like ? order by nome",
                    (rs, rowNum) -> {
                        PacienteCatalogoItem item = new PacienteCatalogoItem();
                        item.setId(rs.getLong("id"));
                        item.setPrincipal(rs.getString("nome"));
                        item.setSecundario(rs.getString("descricao"));
                        return item;
                    }, like, like);
            case NATURALIDADE -> jdbcTemplate.query(
                    "select id, descricao, cod_ibge from naturalidade where upper(descricao) like ? or upper(coalesce(cod_ibge,'')) like ? order by descricao",
                    (rs, rowNum) -> {
                        PacienteCatalogoItem item = new PacienteCatalogoItem();
                        item.setId(rs.getLong("id"));
                        item.setPrincipal(rs.getString("descricao"));
                        item.setExtra(rs.getString("cod_ibge"));
                        return item;
                    }, like, like);
            case PROFISSAO -> jdbcTemplate.query(
                    "select id, nome, cbo_cod from profissao where upper(nome) like ? or upper(coalesce(cbo_cod,'')) like ? order by nome",
                    (rs, rowNum) -> {
                        PacienteCatalogoItem item = new PacienteCatalogoItem();
                        item.setId(rs.getLong("id"));
                        item.setPrincipal(rs.getString("nome"));
                        item.setExtra(rs.getString("cbo_cod"));
                        return item;
                    }, like, like);
            case PROCEDENCIA -> jdbcTemplate.query(
                    "select p.id, p.descricao, tp.descricao as tipo, u.nome as unidade from procedencia p left join tipo_procedencia tp on tp.id = p.tipo_procedencia_id left join unidade u on u.id = p.unidade_id where upper(p.descricao) like ? or upper(coalesce(tp.descricao,'')) like ? or upper(coalesce(u.nome,'')) like ? order by p.descricao",
                    (rs, rowNum) -> {
                        PacienteCatalogoItem item = new PacienteCatalogoItem();
                        item.setId(rs.getLong("id"));
                        item.setPrincipal(rs.getString("descricao"));
                        item.setRelacionamento(rs.getString("tipo"));
                        item.setRelacionamentoSecundario(rs.getString("unidade"));
                        return item;
                    }, like, like, like);
            default -> jdbcTemplate.query(
                    "select id, descricao from " + tipo.getTabela() + " where upper(descricao) like ? order by descricao",
                    (rs, rowNum) -> {
                        PacienteCatalogoItem item = new PacienteCatalogoItem();
                        item.setId(rs.getLong("id"));
                        item.setPrincipal(rs.getString("descricao"));
                        return item;
                    }, like);
        };
    }

    @Transactional(readOnly = true)
    public PacienteCatalogoForm buscarFormulario(PacienteCatalogoTipo tipo, Long id) {
        return switch (tipo) {
            case RACA_COR -> jdbcTemplate.queryForObject(
                    "select descricao, codigo from raca_cor where id = ?",
                    (rs, rowNum) -> {
                        PacienteCatalogoForm form = new PacienteCatalogoForm();
                        form.setDescricao(rs.getString("descricao"));
                        form.setCodigo(rs.getString("codigo"));
                        return form;
                    }, id);
            case ESTADO_CIVIL -> jdbcTemplate.queryForObject(
                    "select nome, descricao from estado_civil where id = ?",
                    (rs, rowNum) -> {
                        PacienteCatalogoForm form = new PacienteCatalogoForm();
                        form.setNome(rs.getString("nome"));
                        form.setDescricaoComplementar(rs.getString("descricao"));
                        return form;
                    }, id);
            case NATURALIDADE -> jdbcTemplate.queryForObject(
                    "select descricao, cod_ibge from naturalidade where id = ?",
                    (rs, rowNum) -> {
                        PacienteCatalogoForm form = new PacienteCatalogoForm();
                        form.setDescricao(rs.getString("descricao"));
                        form.setCodIbge(rs.getString("cod_ibge"));
                        return form;
                    }, id);
            case PROFISSAO -> jdbcTemplate.queryForObject(
                    "select nome, cbo_cod from profissao where id = ?",
                    (rs, rowNum) -> {
                        PacienteCatalogoForm form = new PacienteCatalogoForm();
                        form.setNome(rs.getString("nome"));
                        form.setCboCod(rs.getString("cbo_cod"));
                        return form;
                    }, id);
            case PROCEDENCIA -> jdbcTemplate.queryForObject(
                    "select descricao, tipo_procedencia_id, unidade_id from procedencia where id = ?",
                    (rs, rowNum) -> {
                        PacienteCatalogoForm form = new PacienteCatalogoForm();
                        form.setDescricao(rs.getString("descricao"));
                        long tipoId = rs.getLong("tipo_procedencia_id");
                        form.setTipoProcedenciaId(rs.wasNull() ? null : tipoId);
                        long unidadeId = rs.getLong("unidade_id");
                        form.setUnidadeId(rs.wasNull() ? null : unidadeId);
                        return form;
                    }, id);
            default -> jdbcTemplate.queryForObject(
                    "select descricao from " + tipo.getTabela() + " where id = ?",
                    (rs, rowNum) -> {
                        PacienteCatalogoForm form = new PacienteCatalogoForm();
                        form.setDescricao(rs.getString("descricao"));
                        return form;
                    }, id);
        };
    }

    @Transactional
    public void criar(PacienteCatalogoTipo tipo, PacienteCatalogoForm form) {
        validar(tipo, form);
        try {
            switch (tipo) {
                case RACA_COR -> jdbcTemplate.update("insert into raca_cor (descricao, codigo) values (?, ?)",
                        upper(form.getDescricao()), normalize(form.getCodigo()));
                case ESTADO_CIVIL -> jdbcTemplate.update("insert into estado_civil (nome, descricao) values (?, ?)",
                        upper(form.getNome()), normalize(form.getDescricaoComplementar()));
                case NATURALIDADE -> jdbcTemplate.update("insert into naturalidade (descricao, cod_ibge) values (?, ?)",
                        upper(form.getDescricao()), normalize(form.getCodIbge()));
                case PROFISSAO -> jdbcTemplate.update("insert into profissao (nome, cbo_cod) values (?, ?)",
                        upper(form.getNome()), normalize(form.getCboCod()));
                case PROCEDENCIA -> jdbcTemplate.update("insert into procedencia (descricao, tipo_procedencia_id, unidade_id) values (?, ?, ?)",
                        upper(form.getDescricao()), form.getTipoProcedenciaId(), form.getUnidadeId());
                default -> jdbcTemplate.update("insert into " + tipo.getTabela() + " (descricao) values (?)",
                        upper(form.getDescricao()));
            }
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("Nao foi possivel salvar o registro");
        }
    }

    @Transactional
    public void atualizar(PacienteCatalogoTipo tipo, Long id, PacienteCatalogoForm form) {
        validar(tipo, form);
        try {
            switch (tipo) {
                case RACA_COR -> jdbcTemplate.update("update raca_cor set descricao = ?, codigo = ? where id = ?",
                        upper(form.getDescricao()), normalize(form.getCodigo()), id);
                case ESTADO_CIVIL -> jdbcTemplate.update("update estado_civil set nome = ?, descricao = ? where id = ?",
                        upper(form.getNome()), normalize(form.getDescricaoComplementar()), id);
                case NATURALIDADE -> jdbcTemplate.update("update naturalidade set descricao = ?, cod_ibge = ? where id = ?",
                        upper(form.getDescricao()), normalize(form.getCodIbge()), id);
                case PROFISSAO -> jdbcTemplate.update("update profissao set nome = ?, cbo_cod = ? where id = ?",
                        upper(form.getNome()), normalize(form.getCboCod()), id);
                case PROCEDENCIA -> jdbcTemplate.update("update procedencia set descricao = ?, tipo_procedencia_id = ?, unidade_id = ? where id = ?",
                        upper(form.getDescricao()), form.getTipoProcedenciaId(), form.getUnidadeId(), id);
                default -> jdbcTemplate.update("update " + tipo.getTabela() + " set descricao = ? where id = ?",
                        upper(form.getDescricao()), id);
            }
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("Nao foi possivel atualizar o registro");
        }
    }

    @Transactional
    public void excluir(PacienteCatalogoTipo tipo, Long id) {
        try {
            jdbcTemplate.update("delete from " + tipo.getTabela() + " where id = ?", id);
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("Registro em uso e nao pode ser excluido");
        }
    }

    @Transactional(readOnly = true)
    public List<PacienteLookupOption> listarTiposProcedencia() {
        return jdbcTemplate.query("select id, descricao from tipo_procedencia order by descricao",
                (rs, rowNum) -> new PacienteLookupOption(rs.getLong("id"), rs.getString("descricao")));
    }

    private void validar(PacienteCatalogoTipo tipo, PacienteCatalogoForm form) {
        switch (tipo) {
            case ESTADO_CIVIL -> {
                if (isBlank(form.getNome())) {
                    throw new IllegalArgumentException("Nome e obrigatorio");
                }
            }
            case PROFISSAO -> {
                if (isBlank(form.getNome())) {
                    throw new IllegalArgumentException("Nome e obrigatorio");
                }
            }
            case PROCEDENCIA -> {
                if (isBlank(form.getDescricao())) {
                    throw new IllegalArgumentException("Descricao e obrigatoria");
                }
                if (form.getTipoProcedenciaId() == null) {
                    throw new IllegalArgumentException("Tipo de procedencia e obrigatorio");
                }
                if (form.getUnidadeId() != null) {
                    Integer count = jdbcTemplate.queryForObject("select count(1) from unidade where id = ?", Integer.class, form.getUnidadeId());
                    if (count == null || count == 0) {
                        throw new IllegalArgumentException("Unidade invalida");
                    }
                }
            }
            default -> {
                if (isBlank(form.getDescricao())) {
                    throw new IllegalArgumentException("Descricao e obrigatoria");
                }
            }
        }
    }

    private static String normalizeLike(String value) {
        if (isBlank(value)) {
            return null;
        }
        return "%" + value.trim().toUpperCase() + "%";
    }

    private static String normalize(String value) {
        if (isBlank(value)) {
            return null;
        }
        return value.trim();
    }

    private static String upper(String value) {
        String normalized = normalize(value);
        return normalized == null ? null : normalized.toUpperCase();
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
