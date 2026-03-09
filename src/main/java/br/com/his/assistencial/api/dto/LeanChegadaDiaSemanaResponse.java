package br.com.his.assistencial.api.dto;

public record LeanChegadaDiaSemanaResponse(
        Integer ordem,
        String diaSemana,
        Double mediaChegadas) {
}
