# Guia Lean - Indicadores da Aba SAIDAS

## Escopo
Registro das definicoes acordadas para os indicadores da aba `SAIDAS`.

## Status atual no HIS (07/03/2026)
- Entregue em dominio:
  - modelo de `desfecho` com `tipo_desfecho`, `motivo_desfecho` e `data_hora`;
  - `ORIENTADO_REDE`, `EVASAO`, `ABANDONO`, `TRANSFERENCIA` e `OBITO` disponiveis para uso em indicadores.
- Em backlog:
  - painel/API Lean especificos para `SAI-*`;
  - separacao explicita entre `data_hora_decisao` e `data_hora_saida_real`.

## SAI-01 - Tempo medio entre decisao medica pela alta e saida do paciente
- Conceito:
  - mede o tempo operacional apos a decisao clinica de alta, ate a saida real do paciente.
- Formula base:
  - `SAI-01 = media( data_hora_saida_real - data_hora_decisao_alta )`.
- Fonte de dados recomendada no HIS:
  - campo futuro `desfecho.data_hora_decisao`;
  - campo futuro `desfecho.data_hora_saida_real`.
- Observacao:
  - hoje existe apenas `desfecho.data_hora`, portanto o `SAI-01` ainda nao e calculavel com precisao Lean.
- Regras de calculo:
  1. considerar apenas registros com os dois timestamps preenchidos;
  2. calcular em minutos;
  3. permitir estratificacao por tipo de desfecho (`ATENDIMENTO`, `OBSERVACAO`, `INTERNACAO`) quando necessario.

## SAI-02 - Tempo medio de substituicao de leitos
- Conceito:
  - tempo medio que o leito permanece sem paciente entre a saida de um ocupante e a entrada do proximo ocupante.
- Formula base:
  - por leito: `intervalo_substituicao = entrada_proxima_ocupacao - saida_ocupacao_anterior`;
  - indicador: `SAI-02 = media(intervalos_substituicao_validos)`.
- Fonte de dados recomendada no HIS:
  - tabela de ocupacao/movimentacao de leito (ex.: `leito_ocupacao`) com:
    - `leito_id`, `data_hora_entrada`, `data_hora_saida`.
- Regras de calculo:
  1. parear ocupacoes consecutivas do mesmo `leito_id`;
  2. considerar apenas intervalos validos (saida anterior < entrada seguinte);
  3. excluir periodos de bloqueio/manutencao do leito (quando houver esse status);
  4. calcular em minutos.

## Requisitos minimos no HIS para SAI-01 e SAI-02
- Modelo de desfecho com separacao explicita entre:
  - `data_hora_decisao_alta`;
  - `data_hora_saida_real`.
- Historico de ocupacao de leito com entrada e saida por intervalo.

## SAI-03 - Media de altas medicas por dia - geral
- Conceito:
  - media diaria de altas medicas no periodo.
- Formula base:
  - `SAI-03 = total_altas_medicas_no_periodo / total_dias_do_periodo`.
- Regra no HIS:
  - contar registros de `desfecho` com `motivo_desfecho = ALTA_MEDICA`;
  - data de corte recomendada: `desfecho.data_hora` (ou `data_hora_saida_real` quando existir este campo separado).
- Status atual:
  - base pronta para calculo via `desfecho`, mas sem endpoint/painel Lean dedicado.

## SAI-04 - Media de altas medicas por dia - clinica medica
- Conceito:
  - media diaria de altas medicas de internacoes com perfil clinico.
- Formula base:
  - `SAI-04 = total_altas_medicas_clinicas_no_periodo / total_dias_do_periodo`.
- Regra no HIS:
  - `motivo_desfecho = ALTA_MEDICA`;
  - internacao associada com `perfil_internacao = CLINICO`.

## SAI-05 - Media de altas medicas por dia - clinica cirurgica
- Conceito:
  - media diaria de altas medicas de internacoes com perfil cirurgico.
- Formula base:
  - `SAI-05 = total_altas_medicas_cirurgicas_no_periodo / total_dias_do_periodo`.
- Regra no HIS:
  - `motivo_desfecho = ALTA_MEDICA`;
  - internacao associada com `perfil_internacao = CIRURGICO`.

## SAI-06 - Media de altas medicas por dia - UTI geral
- Conceito:
  - media diaria de altas medicas de pacientes em contexto de UTI geral.
- Formula base:
  - `SAI-06 = total_altas_medicas_UTI_geral_no_periodo / total_dias_do_periodo`.
- Regra no HIS (recomendada):
  - `motivo_desfecho = ALTA_MEDICA`;
  - saida classificada como UTI geral por snapshot no momento da alta (ex.: tipo do leito de saida = `UTI_GERAL`), para evitar ambiguidade historica.

## SAI-07 - Media de pacientes transferidos por dia
- Conceito:
  - media diaria de pacientes com desfecho de transferencia.
- Formula base:
  - `SAI-07 = total_transferencias_no_periodo / total_dias_do_periodo`.
- Regra no HIS:
  - contar registros de `desfecho` com `motivo_desfecho = TRANSFERENCIA`.
- Status atual:
  - base pronta para calculo via `desfecho`, mas sem endpoint/painel Lean dedicado.

## SAI-08 - Media de obitos
- Conceito:
  - media de obitos no periodo.
- Divergencia na planilha:
  - aba `SAIDAS`: "por mes";
  - aba `LISTAS`: "por semana".
- Diretriz de implementacao no HIS:
  - manter o calculo-base por contagem de `motivo_desfecho = OBITO`;
  - parametrizar granularidade do relatorio (`DIARIA`, `SEMANAL`, `MENSAL`) para suportar os dois formatos sem retrabalho.
- Status atual:
  - base pronta para calculo via `desfecho`, mas sem endpoint/painel Lean dedicado.

## Requisitos adicionais no HIS para SAI-03..SAI-08
- Estruturar desfecho com:
  - `motivo_desfecho` (ALTA_MEDICA, OBITO, TRANSFERENCIA, EVASAO, etc.).
- Para estratificacoes (clinico/cirurgico/UTI):
  - vinculo com `internacao` e seu perfil;
  - snapshot de contexto de saida (especialmente para UTI geral).
