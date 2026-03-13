package br.com.his.care.scheduling.dto;

import java.util.List;

public class AgendaPacienteVinculoResultado {

    private final int totalSolicitado;
    private final int totalCriado;
    private final List<String> avisos;

    public AgendaPacienteVinculoResultado(int totalSolicitado, int totalCriado, List<String> avisos) {
        this.totalSolicitado = totalSolicitado;
        this.totalCriado = totalCriado;
        this.avisos = avisos == null ? List.of() : List.copyOf(avisos);
    }

    public int getTotalSolicitado() {
        return totalSolicitado;
    }

    public int getTotalCriado() {
        return totalCriado;
    }

    public int getTotalNaoCriado() {
        return Math.max(totalSolicitado - totalCriado, 0);
    }

    public List<String> getAvisos() {
        return avisos;
    }

    public String resumoAvisos(int limite) {
        if (avisos.isEmpty()) {
            return "";
        }
        int max = Math.max(1, limite);
        StringBuilder sb = new StringBuilder();
        int qtd = Math.min(max, avisos.size());
        for (int i = 0; i < qtd; i++) {
            if (i > 0) {
                sb.append(" | ");
            }
            sb.append(avisos.get(i));
        }
        if (avisos.size() > max) {
            sb.append(" | ...");
        }
        return sb.toString();
    }
}

