package br.com.his.care.admission.support;

import br.com.his.care.admission.dto.EntradaForm;
import br.com.his.patient.model.lookup.TipoProcedencia;

import java.text.Normalizer;
import java.util.Locale;

public final class ProcedenciaEntradaRules {

    public enum TipoCampo {
        CATALOGO,
        BAIRRO,
        MUNICIPIO,
        OUTROS
    }

    private ProcedenciaEntradaRules() {
    }

    public static TipoCampo resolve(TipoProcedencia tipoProcedencia) {
        String normalizedDescricao = normalize(tipoProcedencia == null ? null : tipoProcedencia.getDescricao());
        if (normalizedDescricao.contains("BAIRRO")) {
            return TipoCampo.BAIRRO;
        }
        if (normalizedDescricao.contains("MUNICIPIO") || normalizedDescricao.contains("CIDADE")) {
            return TipoCampo.MUNICIPIO;
        }
        if (normalizedDescricao.contains("OUTRO")) {
            return TipoCampo.OUTROS;
        }
        return TipoCampo.CATALOGO;
    }

    public static void clearIrrelevantFields(EntradaForm form, TipoCampo tipoCampo) {
        if (form == null || tipoCampo == null) {
            return;
        }
        switch (tipoCampo) {
            case BAIRRO -> {
                form.setProcedenciaId(null);
                form.setProcedenciaMunicipioUfId(null);
                form.setProcedenciaMunicipioId(null);
            }
            case MUNICIPIO -> {
                form.setProcedenciaId(null);
                form.setProcedenciaBairroId(null);
            }
            case CATALOGO -> {
                form.setProcedenciaBairroId(null);
                form.setProcedenciaMunicipioUfId(null);
                form.setProcedenciaMunicipioId(null);
            }
            case OUTROS -> {
                form.setProcedenciaId(null);
                form.setProcedenciaBairroId(null);
                form.setProcedenciaMunicipioUfId(null);
                form.setProcedenciaMunicipioId(null);
            }
        }
    }

    private static String normalize(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        String ascii = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "");
        return ascii.trim().toUpperCase(Locale.ROOT);
    }
}
