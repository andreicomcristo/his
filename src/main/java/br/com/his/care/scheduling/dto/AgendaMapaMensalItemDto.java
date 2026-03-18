package br.com.his.care.scheduling.dto;

public record AgendaMapaMensalItemDto(Long agendaId,
                                      int dia,
                                      String horaInicio,
                                      String horaFim,
                                      String modoAgenda,
                                      int vagasTotais,
                                      boolean ativo) {
}
