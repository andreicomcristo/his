package br.com.his.patient.dto;

import java.util.Arrays;
import java.util.List;

public enum PacienteCatalogoTipo {
    RACA_COR("raca-cor", "Raça/Cor", "raca_cor", true, false, false, false, false),
    ETNIA_INDIGENA("etnia-indigena", "Etnia indígena", "etnia_indigena", false, false, false, false, false),
    NACIONALIDADE("nacionalidade", "Nacionalidade", "nacionalidade", false, false, false, false, false),
    NATURALIDADE("naturalidade", "Naturalidade", "naturalidade", false, false, true, false, false),
    ESTADO_CIVIL("estado-civil", "Estado civil", "estado_civil", false, true, false, false, false),
    ESCOLARIDADE("escolaridade", "Escolaridade", "escolaridade", false, false, false, false, false),
    TIPO_SANGUINEO("tipo-sanguineo", "Tipo sanguíneo", "tipo_sanguineo", false, false, false, false, false),
    ORIENTACAO_SEXUAL("orientacao-sexual", "Orientação sexual", "orientacao_sexual", false, false, false, false, false),
    IDENTIDADE_GENERO("identidade-genero", "Identidade de gênero", "identidade_genero", false, false, false, false, false),
    DEFICIENCIA("deficiencia", "Deficiência", "deficiencia", false, false, false, false, false),
    PROFISSAO("profissao", "Profissão", "profissao", false, false, false, true, false),
    PROCEDENCIA("procedencia", "Procedência", "procedencia", false, false, false, false, true);

    private final String slug;
    private final String titulo;
    private final String tabela;
    private final boolean usaCodigo;
    private final boolean usaNomeEDescricao;
    private final boolean usaCodIbge;
    private final boolean usaCboCod;
    private final boolean usaTipoProcedencia;

    PacienteCatalogoTipo(String slug, String titulo, String tabela,
                         boolean usaCodigo,
                         boolean usaNomeEDescricao,
                         boolean usaCodIbge,
                         boolean usaCboCod,
                         boolean usaTipoProcedencia) {
        this.slug = slug;
        this.titulo = titulo;
        this.tabela = tabela;
        this.usaCodigo = usaCodigo;
        this.usaNomeEDescricao = usaNomeEDescricao;
        this.usaCodIbge = usaCodIbge;
        this.usaCboCod = usaCboCod;
        this.usaTipoProcedencia = usaTipoProcedencia;
    }

    public String getSlug() { return slug; }
    public String getTitulo() { return titulo; }
    public String getTabela() { return tabela; }
    public boolean isUsaCodigo() { return usaCodigo; }
    public boolean isUsaNomeEDescricao() { return usaNomeEDescricao; }
    public boolean isUsaCodIbge() { return usaCodIbge; }
    public boolean isUsaCboCod() { return usaCboCod; }
    public boolean isUsaTipoProcedencia() { return usaTipoProcedencia; }
    public boolean isUsaDescricaoSimples() { return !usaNomeEDescricao && !usaCodigo && !usaCodIbge && !usaCboCod && !usaTipoProcedencia; }

    public static PacienteCatalogoTipo fromSlug(String slug) {
        return Arrays.stream(values())
                .filter(value -> value.slug.equals(slug))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Catalogo de paciente invalido: " + slug));
    }

    public static List<PacienteCatalogoTipo> visiveis() {
        return List.of(values());
    }
}
