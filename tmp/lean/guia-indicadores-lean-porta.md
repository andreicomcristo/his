# Guia Lean - Indicadores da Aba PORTA

## Escopo
Este documento registra as definicoes acordadas para implementacao dos indicadores Lean da aba `PORTA` no HIS.

## Status atual no HIS (07/03/2026)
- Entregue:
  - painel web: `/ui/indicadores/lean/porta-triagem`;
  - API: `/api/assistencial/indicadores/lean/porta-triagem`;
  - indicadores `POR-01` ate `POR-09`.
- Fonte principal:
  - `atendimento.data_hora_chegada` (filtro por unidade atual e periodo).
- Dependencias ja implantadas:
  - `forma_chegada.perfil_chegada` para `HORIZONTAL`/`VERTICAL`;
  - motivo de desfecho `ORIENTADO_REDE` (com `destino_rede` obrigatorio no cadastro de desfecho);
  - `ABANDONO` cadastrado em `status_atendimento` e `motivo_desfecho`.

## POR-01 - Total de pacientes atendidos no pronto socorro por ano
- Fonte no HIS: tabela `atendimento`.
- Regra: contar atendimentos da unidade no ano calendario.
- Data de referencia: `atendimento.data_hora_chegada`.
- Filtro principal: `atendimento.unidade_id`.

## POR-02 - Total de pacientes atendidos no pronto socorro por dia
- Fonte no HIS: tabela `atendimento`.
- Regra: contar atendimentos da unidade no dia.
- Data de referencia: `atendimento.data_hora_chegada`.
- Filtro principal: `atendimento.unidade_id`.

## POR-03 - Percentual de pacientes horizontais por dia (ambulancia)
- Conceito acordado:
  - `Horizontal` = paciente transportado/assistido (ex.: SAMU, transporte aereo, ambulancia), com maior chance de ocupacao de maca/leito.
- Estrutura no HIS:
  - Base de dados em `entrada.forma_chegada_id`.
  - `forma_chegada.perfil_chegada` com valores `HORIZONTAL`/`VERTICAL`.
- Calculo:
  - numerador: atendimentos (com entrada) do dia com `perfil_chegada = HORIZONTAL`.
  - denominador: total de atendimentos (com entrada) do dia.
  - resultado em `%`.

## POR-04 - Percentual de pacientes verticais por dia
- Conceito acordado:
  - `Vertical` = paciente deambulando (anda), sem transporte assistido.
- Estrutura no HIS:
  - mesma base do POR-03, via `entrada.forma_chegada_id` + `forma_chegada.perfil_chegada`.
- Calculo:
  - numerador: atendimentos (com entrada) do dia com `perfil_chegada = VERTICAL`.
  - denominador: total de atendimentos (com entrada) do dia.
  - resultado em `%`.

## Observacao tecnica
- Para evitar divergencia entre relatorios diarios, usar timezone da unidade (atualmente America/Maceio no ambiente de referencia).

## POR-05 - Media de chegada de pacientes por hora
- Fonte no HIS: tabela `atendimento`.
- Regra: calcular media horaria de chegadas no periodo analisado.
- Data de referencia: `atendimento.data_hora_chegada`.
- Filtro principal: `atendimento.unidade_id`.
- Formula base:
  - para cada hora `H` (00 a 23):
  - `media_hora_H = total_chegadas_na_hora_H_no_periodo / total_de_dias_do_periodo`.

## POR-05.1 a POR-05.24 - Detalhamento por hora (00:00 a 23:00)
- Cada item representa a media da hora correspondente:
  - `POR-05.1` -> 00:00-00:59
  - `POR-05.2` -> 01:00-01:59
  - ...
  - `POR-05.24` -> 23:00-23:59
- Nao usar contagem bruta de um unico dia quando o indicador estiver configurado para periodo mensal.

## POR-06 - Media de chegada de pacientes por dia da semana
- Fonte no HIS: tabela `atendimento`.
- Regra: consolidar medias por dia da semana no periodo analisado.
- Data de referencia: `atendimento.data_hora_chegada`.
- Filtro principal: `atendimento.unidade_id`.

## POR-06.1 a POR-06.7 - Detalhamento por dia da semana
- Cada item representa a media de chegadas no dia da semana correspondente:
  - `POR-06.1` domingo
  - `POR-06.2` segunda-feira
  - `POR-06.3` terca-feira
  - `POR-06.4` quarta-feira
  - `POR-06.5` quinta-feira
  - `POR-06.6` sexta-feira
  - `POR-06.7` sabado
- Formula base para cada dia da semana `D`:
  - `media_D = total_chegadas_no_dia_D_no_periodo / quantidade_de_ocorrencias_do_dia_D_no_periodo`.

## Parametro de periodo (recomendacao)
- Padrao operacional: mes corrente.
- Permitir analise por intervalo customizado (data inicial e data final), reaplicando as mesmas formulas.

## POR-07 - Percentual de pacientes orientados para rede de saude
- Conceito acordado:
  - pacientes atendidos no pronto socorro que foram direcionados para outro ponto da rede (APS, UPA, ambulatorio especializado, outro hospital de referencia), por nao serem perfil da unidade para continuidade no local.
- Formula:
  - `POR-07 = (total_pacientes_orientados_para_rede / total_atendimentos_no_periodo) * 100`.
- Fonte no HIS (implementado):
  - `desfecho` + `motivo_desfecho = ORIENTADO_REDE`.
  - `destino_rede_id` obrigatorio quando motivo = `ORIENTADO_REDE`.

## POR-08 - Taxa de evasao
- Conceito acordado:
  - paciente que sai da unidade apos atendimento medico, sem conclusao assistencial planejada.
- Formula:
  - `POR-08 = (total_evasoes_no_periodo / total_atendimentos_no_periodo) * 100`.
- Fonte no HIS:
  - `atendimento.status = EVADIU` (e/ou desfecho com motivo `EVASAO`, quando aplicado).
- Regra de consistencia recomendada:
  - padronizar uma unica regra de marcacao de evasao no fluxo para evitar dupla contagem.

## POR-09 - Taxa de abandono
- Conceito acordado:
  - paciente que sai da unidade antes do atendimento medico.
- Formula:
  - `POR-09 = (total_abandonos_no_periodo / total_atendimentos_no_periodo) * 100`.
- Fonte no HIS (implementado):
  - `status_atendimento = ABANDONO` e/ou `motivo_desfecho = ABANDONO`.
- Observacao tecnica:
  - o servico de indicador ja faz validacao defensiva: se `ABANDONO` nao existir no banco, retorna `N/A` com observacao.
