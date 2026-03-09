# Guia Lean - Aba FILAS

## Escopo
A aba `FILAS` na planilha Lean nao traz codigos de KPI (como `POR-01`, `TRI-02` etc.). Ela funciona como base de coleta operacional por horario.

## Status atual no HIS (07/03/2026)
- Ainda nao implementado:
  - nao existe tabela de snapshot historico (`fila_snapshot`);
  - nao existe painel Lean dedicado para fila por hora.
- Ja existe base para futura composicao:
  - `status_atendimento` padronizado (`AGUARDANDO_RECEPCAO`, `AGUARDANDO_TRIAGEM`, `AGUARDANDO_MEDICO`, etc.);
  - historico em `atendimento_periodo` para alguns estagios do fluxo.

## O que a aba FILAS mede
Em cada horario de coleta, registra quantos pacientes estao aguardando em pontos criticos do fluxo:
- quantidade aguardando na recepcao;
- quantidade aguardando na triagem;
- quantidade aguardando atendimento medico (todos os consultorios);
- quantidade aguardando reavaliacao medica;
- quantidade aguardando internacao no pronto socorro (boarding).

## Interpretacao
- Nao e um indicador unico.
- E uma "foto" (snapshot) do tamanho das filas naquele momento.
- A partir dessas fotos, derivam-se indicadores de operacao:
  - media de fila por hora/faixa;
  - pico de fila;
  - p90/p95 de fila;
  - comparacao DDC inicial vs DDC final.

## Proposta de implementacao no HIS
Substituir coleta manual por snapshot automatico periodico (ex.: a cada 1h) por unidade.

### Modelo sugerido
Tabela `fila_snapshot`:
- `id`
- `unidade_id`
- `data_hora_referencia`
- `qtd_aguardando_recepcao`
- `qtd_aguardando_triagem`
- `qtd_aguardando_medico`
- `qtd_aguardando_reavaliacao`
- `qtd_boarding_ps`
- `tipo_coleta` (`INICIAL`/`FINAL`, opcional, para manter logica Lean de comparacao)

## Regras de calculo recomendadas
- Padronizar timezone por unidade.
- Evitar dupla contagem por paciente em filas simultaneas.
- Definir claramente regras de status que compoem cada fila:
  - recepcao: sem entrada/cadastro concluido;
  - triagem: sem classificacao concluida;
  - medico: triado aguardando primeiro atendimento medico;
  - reavaliacao: aguardando retorno medico;
  - boarding: decisao de internacao sem leito definitivo.
