# Guia Lean - Indicadores da Aba INTERNACAO

## Escopo
Registro das definicoes acordadas para os indicadores estruturais de leitos (`LEI-01` a `LEI-06`).

## Status atual no HIS (07/03/2026)
- Entregue:
  - painel de taxa de ocupacao (`TO-01` ate `TO-05`) em `/ui/indicadores/lean/taxa-ocupacao`;
  - API: `/api/assistencial/indicadores/lean/taxa-ocupacao`;
  - mapa de leitos, internacao, observacao e movimentacao por `leito_ocupacao`;
  - natureza operacional do leito (`FIXO_CNES`, `FIXO_NAO_CNES`, `VIRTUAL_SUPERLOTACAO`) com taxa nominal x operacional.
- Em backlog (sem painel Lean dedicado ainda):
  - `LEI-01` a `LEI-06`;
  - `TMP-01` a `TMP-05`;
  - `FU-01` a `FU-05`.

## Premissas operacionais para o HIS
- Esses indicadores representam capacidade instalada (estrutura), nao producao assistencial.
- A contagem deve considerar leitos ativos e cadastrados para uso assistencial.
- Base recomendada: tabela `leito` (quando o mapa de leitos estiver implantado), com classificacoes por tipo e perfil.

## LEI-01 - Total de leitos do hospital (geral)
- Conceito:
  - total de leitos assistenciais ativos do hospital/unidade.
- Formula base:
  - `LEI-01 = count(leito_ativo_assistencial)`.
- Regra no HIS:
  - contar leitos com `ativo = true` e `assistencial = true`.

## LEI-02 - Total de leitos de internacao disponiveis para o PS (enfermaria)
- Conceito:
  - total de leitos de enfermaria que podem receber pacientes vindos do pronto socorro.
- Formula base:
  - `LEI-02 = count(leitos_enfermaria_habilitados_para_PS)`.
- Regra no HIS:
  - filtrar leitos com:
    - `tipo_leito = ENFERMARIA`;
    - `recebe_ps = true`;
    - `ativo = true`.

## LEI-03 - Total de leitos clinicos (geral)
- Conceito:
  - total de leitos classificados como perfil clinico.
- Formula base:
  - `LEI-03 = count(leitos_perfil_clinico_ativos)`.
- Regra no HIS:
  - filtrar `perfil_leito = CLINICO` e `ativo = true`.

## LEI-04 - Total de leitos cirurgicos (geral)
- Conceito:
  - total de leitos classificados como perfil cirurgico.
- Formula base:
  - `LEI-04 = count(leitos_perfil_cirurgico_ativos)`.
- Regra no HIS:
  - filtrar `perfil_leito = CIRURGICO` e `ativo = true`.

## LEI-05 - Total de leitos de UTI (geral)
- Conceito:
  - total de leitos de UTI ativos do hospital/unidade.
- Formula base:
  - `LEI-05 = count(leitos_UTI_ativos)`.
- Regra no HIS:
  - filtrar `tipo_leito = UTI` e `ativo = true`.

## LEI-06 - Total de leitos de RPA (geral)
- Conceito:
  - total de leitos de RPA (recuperacao pos-anestesica) ativos.
- Formula base:
  - `LEI-06 = count(leitos_RPA_ativos)`.
- Regra no HIS:
  - filtrar `tipo_leito = RPA` e `ativo = true`.

## Observacoes de modelagem recomendadas
- Criar tabelas de dominio para evitar enum fixo:
  - `tipo_leito` (ENFERMARIA, UTI, RPA, etc.);
  - `perfil_leito` (CLINICO, CIRURGICO, etc.).
- Decisao funcional acordada:
  - o cadastro de `leito` deve armazenar explicitamente:
    - `tipo_leito_id`;
    - `perfil_leito_id` (quando aplicavel).
  - os indicadores `LEI-*` devem ser calculados a partir da tabela de `leito`, nao apenas da `area`.
- Definir em `leito` se recebe PS:
  - campo `recebe_ps` (boolean) ou relacao com tipo de origem permitida.
- Manter regra de classificacao unica por leito para evitar dupla contagem em `LEI-03` e `LEI-04`.

## Indicadores TMP (tempo medio de permanencia)

### Cohort padrao (regra comum)
- Para todos os indicadores `TMP-*`, considerar internacoes encerradas no periodo:
  - incluir somente registros com `internacao.data_hora_fim_internacao` preenchida;
  - usar o periodo de competencia pela data de encerramento (recomendado).
- Formula base de permanencia hospitalar:
  - `permanencia = data_hora_fim_internacao - data_hora_inicio_internacao`.

## TMP-01 - Tempo medio de permanencia do paciente internado (hospitalar)
- Conceito:
  - media do tempo de permanencia das internacoes encerradas no periodo.
- Formula base:
  - `TMP-01 = media(permanencia_hospitalar)` sem filtro adicional.

## TMP-02 - Tempo medio de permanencia do paciente internado advindo do PS
- Conceito:
  - media do tempo de permanencia hospitalar apenas para internacoes de origem PS.
- Formula base:
  - `TMP-02 = media(permanencia_hospitalar)` com `origem_demanda_internacao = PS`.

## TMP-03 - Tempo medio de permanencia do paciente internado clinico
- Conceito:
  - media do tempo de permanencia hospitalar para internacoes de perfil clinico.
- Formula base:
  - `TMP-03 = media(permanencia_hospitalar)` com `perfil_internacao = CLINICO`.

## TMP-04 - Tempo medio de permanencia do paciente internado cirurgico
- Conceito:
  - media do tempo de permanencia hospitalar para internacoes de perfil cirurgico.
- Formula base:
  - `TMP-04 = media(permanencia_hospitalar)` com `perfil_internacao = CIRURGICO`.

## TMP-05 - Tempo medio de permanencia do paciente internado - UTI geral
- Conceito:
  - media do tempo de permanencia em leito de `UTI_GERAL` (nao o tempo hospitalar total).
- Formula base:
  - para cada internacao, somar periodos em ocupacoes de leito `tipo_leito = UTI_GERAL`;
  - `TMP-05 = media(tempo_total_em_UTI_GERAL_por_internacao)`.
- Regra de implementacao no HIS:
  - usar tabela de movimentacao/ocupacao de leito (ex.: `leito_ocupacao`) com:
    - `data_hora_entrada`, `data_hora_saida`, `leito_id`;
    - classificacao do leito para identificar `UTI_GERAL`.

## Requisitos minimos de dados no HIS para TMP-01..TMP-05
- `internacao.data_hora_inicio_internacao`
- `internacao.data_hora_fim_internacao`
- `internacao.origem_demanda_id`
- `internacao.perfil_internacao_id`
- tabela de ocupacao de leito com historico entrada/saida e tipo do leito.

## Observacao de nomenclatura tecnica no HIS
- No banco atual:
  - origem da internacao: `internacao.origem_demanda_id` (tabela `internacao_origem_demanda`);
  - perfil da internacao: `internacao.perfil_internacao_id` (tabela `internacao_perfil`);
  - movimentacao: `leito_ocupacao` + `leito_ocupacao_tipo` (`PROVISORIA`/`DEFINITIVA`).

## Indicadores FU (fator de utilizacao de leitos)

### Formula padrao (FU-01 a FU-05)
- Conceito:
  - taxa de ocupacao/utilizacao dos leitos elegiveis no periodo.
- Formula base:
  - `FU = (tempo_ocupado_total_dos_leitos_elegiveis / tempo_total_disponivel_dos_leitos_elegiveis) x 100`.
- Implementacao pratica:
  1. somar o tempo ocupado (minutos/horas) de cada leito elegivel no periodo;
  2. calcular tempo total disponivel:
     - `quantidade_de_leitos_elegiveis x duracao_do_periodo`;
  3. dividir ocupado/disponivel e multiplicar por 100.

## FU-01 - Fator de utilizacao dos leitos disponiveis para o PS
- Elegibilidade dos leitos:
  - `ativo = true`;
  - `assistencial = true`;
  - `recebe_ps = true`.

## FU-02 - Fator de utilizacao dos leitos do hospital
- Elegibilidade dos leitos:
  - todos os leitos assistenciais ativos da unidade/hospital.

## FU-03 - Fator de utilizacao dos leitos clinicos disponiveis para PS
- Elegibilidade dos leitos:
  - `ativo = true`;
  - `assistencial = true`;
  - `perfil_leito = CLINICO`;
  - `recebe_ps = true`.

## FU-04 - Fator de utilizacao dos leitos cirurgicos disponiveis para o PS
- Elegibilidade dos leitos:
  - `ativo = true`;
  - `assistencial = true`;
  - `perfil_leito = CIRURGICO`;
  - `recebe_ps = true`.

## FU-05 - Fator de utilizacao dos leitos de UTI geral
- Elegibilidade dos leitos:
  - `ativo = true`;
  - `assistencial = true`;
  - `tipo_leito = UTI_GERAL`.

## Requisitos minimos de dados no HIS para FU-01..FU-05
- cadastro de `leito` com:
  - `tipo_leito_id`;
  - `perfil_leito_id` (quando aplicavel);
  - `recebe_ps`;
  - `ativo` e classificacao assistencial.
- historico de ocupacao por leito:
  - `data_hora_entrada` e `data_hora_saida` por intervalo de ocupacao.
