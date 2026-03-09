# Guia Lean - Indicadores da Aba CONSULTA

## Escopo
Registro das definicoes acordadas para os indicadores da aba `CONSULTA`.

## Status atual no HIS (07/03/2026)
- Entregue:
  - painel web: `/ui/indicadores/lean/consulta`;
  - API: `/api/assistencial/indicadores/lean/consulta`;
  - indicadores `PSG-01`, `PSG-02` e `PSG-03` com media, mediana e p90.
- Em backlog:
  - `PSG-04` (tempo porta-medico), pois depende do modulo de atendimento medico;
  - familia `INT-*` ainda sem painel dedicado, apesar de ja existir estrutura parcial de internacao.

## Indicadores principais da aba
- `PSG-01` LOS sem internacao
- `PSG-02` LOS com internacao
- `PSG-03` Tempo de boarding
- `PSG-04` Tempo porta-medico

## PSG-01 - LOS sem internacao
- Conceito:
  - tempo medio (minutos) entre a chegada no pronto socorro e o desfecho no proprio PS, sem internacao.
- Formula base:
  - `PSG-01 = media( desfecho.data_hora - atendimento.data_hora_chegada )` para casos sem internacao.
- No HIS (implementado):
  - fonte: `desfecho` + `tipo_desfecho = ATENDIMENTO`;
  - regra atual: exclui `motivo_desfecho` em `EVASAO` e `ABANDONO`;
  - saida: media, mediana, p90 e total de casos.

## PSG-02 - LOS com internacao
- Conceito:
  - tempo medio (minutos) entre a chegada no pronto socorro e a chegada real no leito definitivo de internacao/UTI.
- Formula base:
  - `PSG-02 = media( data_hora_chegada_leito_definitivo - atendimento.data_hora_chegada )`.
- No HIS (implementado):
  - fonte: `internacao` + `leito_ocupacao` + `leito_ocupacao_tipo`;
  - regra atual:
    - usa a primeira `leito_ocupacao.data_hora_entrada` com `tipo_ocupacao = DEFINITIVA`;
    - so considera leitos com `permite_destino_definitivo = true`;
  - saida: media, mediana, p90 e total de casos.
- Relacao com outros indicadores:
  - o intervalo entre decisao medica de internacao e chegada ao leito definitivo compoe o `PSG-03` (boarding).

## PSG-03 - Tempo de boarding (minutos)
- Conceito:
  - tempo medio entre a decisao medica de internacao e a chegada do paciente ao leito definitivo de internacao/UTI.
- Formula base:
  - `PSG-03 = media( data_hora_chegada_leito_definitivo - data_hora_decisao_internacao )`.
- No HIS (implementado):
  - fonte principal: `internacao.data_hora_decisao_internacao` ate primeira ocupacao `DEFINITIVA`;
  - fallback atual: quando nao existe leito definitivo, usa `desfecho.data_hora`;
  - saida: media, mediana, p90, total de casos e total com fallback.

## PSG-04 - Tempo porta-medico (minutos)
- Conceito:
  - tempo medio entre a chegada do paciente ao pronto socorro e o inicio do primeiro atendimento medico.
- Formula base:
  - `PSG-04 = media( data_hora_inicio_primeiro_atendimento_medico - atendimento.data_hora_chegada )`.
- No HIS (status atual):
  - ainda nao implementado como indicador.
  - falta registro padronizado do inicio do primeiro atendimento medico no fluxo clinico.
- Diretriz de implementacao:
  1. no modulo medico, registrar `data_hora_inicio_atendimento_medico` (ou periodo `ATENDIMENTO_MEDICO` com inicio/fim);
  2. usar o primeiro atendimento medico do caso como referencia;
  3. calcular por unidade e periodo com timezone da unidade.

## Status da familia INT-* no HIS
- Estrutura parcial ja disponivel:
  - `internacao.origem_demanda_id`;
  - `internacao.perfil_internacao_id`;
  - `internacao.data_hora_decisao_internacao`, `data_hora_inicio_internacao` e `data_hora_fim_internacao`.
- Ainda pendente:
  - endpoints/painel Lean para `INT-01` ate `INT-05`;
  - modulo de solicitacao medica para UTI (`INT-03`/`INT-04`).

## INT-01 - Media de pacientes que internam por dia (origem PS)
- Conceito:
  - demanda diaria media de internacoes originadas do fluxo assistencial do pronto socorro.
- Ajuste de regra acordado:
  - considerar que pode haver paciente regulado e ainda assim ele entrar no fluxo do PS da unidade de destino;
  - portanto, nao usar apenas "demanda espontanea"; usar classificacao de origem/tipo assistencial.
- Formula base:
  - `INT-01 = total_internacoes_origem_PS_no_periodo / total_dias_do_periodo`.
- Diretriz de implementacao no HIS:
  1. na internacao (ou atendimento), registrar campo explicito de origem da demanda de internacao, ex.:
     - `origem_demanda_internacao` (`PS`, `ELETIVA`, `REGULACAO_DIRETA`, etc.);
  2. vincular internacao ao `atendimento_id` quando houver fluxo do PS;
  3. usar `data_hora_inicio_internacao` como data de corte para contagem;
  4. contabilizar no `INT-01` apenas os casos classificados como origem `PS`.
- Observacao:
  - internações diretas/eletivas devem ficar em indicador separado (`INT-02`).

## INT-02 - Media de pacientes eletivos que internam por dia
- Conceito:
  - demanda diaria media de internacoes eletivas (nao originadas do fluxo do pronto socorro).
- Formula base:
  - `INT-02 = total_internacoes_eletivas_no_periodo / total_dias_do_periodo`.
- Diretriz de implementacao no HIS:
  1. reutilizar a classificacao de origem da demanda de internacao:
     - `origem_demanda_internacao` (`PS`, `ELETIVA`, `REGULACAO_DIRETA`, etc.);
  2. contabilizar no `INT-02` apenas registros com `origem_demanda_internacao = ELETIVA`;
  3. usar `data_hora_inicio_internacao` como data de corte;
  4. manter segregacao com `INT-01` para evitar dupla contagem.
- Observacao:
  - nao inferir apenas por `tipo_atendimento`; priorizar campo explicito de origem para auditoria e consistencia.

## INT-03 - Media de solicitacoes de UTI geral por dia (origem PS)
- Conceito:
  - demanda diaria media de solicitacoes medicas de leito de `UTI_GERAL` para pacientes com origem no pronto socorro.
- Formula base:
  - `INT-03 = total_solicitacoes_UTI_GERAL_origem_PS_no_periodo / total_dias_do_periodo`.
- Diretriz de implementacao no HIS:
  1. registrar no modulo medico a tabela `solicitacao_internacao` com, no minimo:
     - `atendimento_id` (ou `episodio_id`);
     - `data_hora_solicitacao`;
     - `tipo_destino` (ex.: `UTI_GERAL`);
     - `origem_demanda` (ex.: `PS`, `ELETIVA`, `REGULACAO_DIRETA`);
     - `status` (ex.: `ABERTA`, `ATENDIDA`, `CANCELADA`).
  2. contar no indicador apenas solicitacoes com:
     - `tipo_destino = UTI_GERAL`;
     - `origem_demanda = PS`;
     - status valido para demanda real (recomendado excluir `CANCELADA`).
  3. para rastreabilidade clinica, quando a internacao for efetivada:
     - copiar a data da solicitacao aprovada para `internacao.data_hora_decisao_internacao`;
     - salvar `internacao.solicitacao_internacao_id` (FK) para vinculo auditavel.
  4. evitar inflar o indicador por retrabalho:
     - recomendacao inicial: considerar somente a primeira solicitacao valida por atendimento no periodo.
- Observacao:
  - `INT-03` mede demanda por UTI (solicitacao), nao ocupacao efetiva de leito; por isso a fonte primaria deve ser `solicitacao_internacao`.

## INT-04 - Media de solicitacoes de UTI geral por dia (incluindo pacientes eletivos)
- Conceito:
  - demanda diaria media total de solicitacoes medicas de leito de `UTI_GERAL`, independentemente da origem da demanda.
- Formula base:
  - `INT-04 = total_solicitacoes_UTI_GERAL_validas_no_periodo / total_dias_do_periodo`.
- Diretriz de implementacao no HIS:
  1. reutilizar a mesma fonte do `INT-03`:
     - tabela `solicitacao_internacao` com `tipo_destino`, `origem_demanda`, `status`, `data_hora_solicitacao`.
  2. filtrar para:
     - `tipo_destino = UTI_GERAL`;
     - incluir todas as origens (`PS`, `ELETIVA`, `REGULACAO_DIRETA`, etc.);
     - excluir status nao validos para demanda efetiva (recomendado excluir `CANCELADA`).
  3. manter a mesma regra de deduplicacao adotada no `INT-03` (ex.: primeira solicitacao valida por atendimento/caso no periodo), para consistencia entre indicadores.
- Observacao:
  - por definicao, `INT-04` tende a ser maior ou igual ao `INT-03`, pois inclui solicitacoes eletivas e demais origens alem do PS.
