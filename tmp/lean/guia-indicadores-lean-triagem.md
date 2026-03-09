# Guia Lean - Indicadores da Aba TRIAGEM

## Escopo
Registro das definicoes acordadas para os indicadores da aba `TRIAGEM` na planilha Lean.

## Status atual no HIS (07/03/2026)
- Entregue:
  - painel web: `/ui/indicadores/lean/porta-triagem`;
  - API: `/api/assistencial/indicadores/lean/porta-triagem`;
  - indicadores `TRI-02`, `TRI-03`, `TRI-04` e `TRI-06`.
- Parcial:
  - `TRI-05` segue sem implementacao por falta de modulo de escala de enfermagem.

## Observacao da planilha
- Na aba `KPIs DDC`, os indicadores `TRI-*` estao classificados como `COMPLEMENTAR`.
- Priorizamos inicialmente os indicadores `TRI-02`, `TRI-03` e `TRI-04`.

## TRI-02 - Distribuicao dos pacientes pelo sistema de triagem
- Conceito:
  - proporcao de pacientes por categoria/cor de classificacao de risco.
- No HIS (implementado):
  - base em `classificacao_risco.classificacao_cor_id`.
  - distribuicao consolidada por cor no periodo filtrado.
- Recomendacoes:
  1. consolidar relatorio por unidade e periodo;
  2. garantir trilha historica quando houver ajuste de classificacao.

## TRI-03 - Percentual de risco maior
- Conceito:
  - percentual de pacientes classificados em risco alto (definicao local das cores consideradas criticas).
- No HIS (implementado):
  - calculado por `classificacao_cor.risco_maior = true`.
- Recomendacoes:
  1. parametrizar no cadastro de cor quais entram em `risco maior`;
  2. evitar regra fixa hardcoded.

## TRI-04 - Tempo medio de atendimento da triagem
- Conceito:
  - tempo de execucao da triagem (entrada na triagem ate saida da triagem), sem incluir espera anterior.
- No HIS (implementado):
  - calculado por `atendimento_periodo` (`tipo = TRIAGEM`) com `inicio_em` e `fim_em`.
  - painel exibe media, mediana e p90.
- Recomendacoes:
  1. definir `atendimento_periodo` como fonte oficial do KPI;
  2. expor media, mediana e p90 para analise operacional.

## TRI-05 - Media de enfermeiros na triagem por hora
- Conceito:
  - capacidade humana media disponivel na triagem por hora.
- No HIS (status atual):
  - nao atendido nativamente (sem modulo de escala/alocacao horaria da equipe).
- Decisao atual:
  - nao implementar neste momento inicial.

## TRI-06 - Quantidade de salas de classificacao de risco
- Conceito:
  - capacidade fisica de salas de classificacao de risco disponiveis na unidade.
- No HIS (implementado):
  - capacidade `SALA_CLASSIFICACAO` criada e vinculada a areas ativas de classificacao.
  - indicador conta quantidade de areas com essa capacidade por unidade.
