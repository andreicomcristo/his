-- Unidades de referencia do legado GestHosp
-- HEDH: Hospital Estadual de Emergencia
-- HGE:  Hospital Geral do Estado

UPDATE unidade
SET nome = 'HOSPITAL ESTADUAL DE EMERGENCIA - HEDH',
    tipo_estabelecimento = 'HOSPITAL_GERAL',
    ativo = TRUE
WHERE cnes = 'HEDH';

INSERT INTO unidade (nome, tipo_estabelecimento, cnes, ativo)
SELECT 'HOSPITAL ESTADUAL DE EMERGENCIA - HEDH', 'HOSPITAL_GERAL', 'HEDH', TRUE
WHERE NOT EXISTS (SELECT 1 FROM unidade WHERE cnes = 'HEDH');

UPDATE unidade
SET nome = 'HOSPITAL GERAL DO ESTADO - HGE',
    tipo_estabelecimento = 'HOSPITAL_GERAL',
    ativo = TRUE
WHERE cnes = 'HGE';

INSERT INTO unidade (nome, tipo_estabelecimento, cnes, ativo)
SELECT 'HOSPITAL GERAL DO ESTADO - HGE', 'HOSPITAL_GERAL', 'HGE', TRUE
WHERE NOT EXISTS (SELECT 1 FROM unidade WHERE cnes = 'HGE');

-- Configuracao de fluxo por unidade
-- HEDH: classificacao (triagem) primeiro, episodio depois
INSERT INTO unidade_config_fluxo (unidade_id, primeiro_passo, exige_ficha_para_medico, cria_episodio_automatico)
SELECT u.id, 'TRIAGEM', TRUE, FALSE
FROM unidade u
WHERE u.cnes = 'HEDH'
ON CONFLICT (unidade_id) DO UPDATE
SET primeiro_passo = EXCLUDED.primeiro_passo,
    exige_ficha_para_medico = EXCLUDED.exige_ficha_para_medico,
    cria_episodio_automatico = EXCLUDED.cria_episodio_automatico;

-- HGE: recepcao/ficha primeiro, episodio pode abrir sem triagem obrigatoria
INSERT INTO unidade_config_fluxo (unidade_id, primeiro_passo, exige_ficha_para_medico, cria_episodio_automatico)
SELECT u.id, 'RECEPCAO', TRUE, FALSE
FROM unidade u
WHERE u.cnes = 'HGE'
ON CONFLICT (unidade_id) DO UPDATE
SET primeiro_passo = EXCLUDED.primeiro_passo,
    exige_ficha_para_medico = EXCLUDED.exige_ficha_para_medico,
    cria_episodio_automatico = EXCLUDED.cria_episodio_automatico;

-- Regras de triagem por tipo de atendimento
-- HEDH: triagem obrigatoria em URGENCIA; opcional nos demais
INSERT INTO unidade_regra_triagem (unidade_id, tipo_atendimento, triagem_obrigatoria)
SELECT u.id, v.tipo_atendimento, v.triagem_obrigatoria
FROM unidade u
JOIN (VALUES
    ('URGENCIA', TRUE),
    ('AMBULATORIAL', FALSE),
    ('INTERNACAO_DIRETA', FALSE),
    ('PROCEDIMENTO', FALSE)
) AS v(tipo_atendimento, triagem_obrigatoria) ON TRUE
WHERE u.cnes = 'HEDH'
ON CONFLICT (unidade_id, tipo_atendimento) DO UPDATE
SET triagem_obrigatoria = EXCLUDED.triagem_obrigatoria;

-- HGE: triagem opcional para todos os tipos (permite episodio primeiro)
INSERT INTO unidade_regra_triagem (unidade_id, tipo_atendimento, triagem_obrigatoria)
SELECT u.id, v.tipo_atendimento, FALSE
FROM unidade u
JOIN (VALUES
    ('URGENCIA'),
    ('AMBULATORIAL'),
    ('INTERNACAO_DIRETA'),
    ('PROCEDIMENTO')
) AS v(tipo_atendimento) ON TRUE
WHERE u.cnes = 'HGE'
ON CONFLICT (unidade_id, tipo_atendimento) DO UPDATE
SET triagem_obrigatoria = EXCLUDED.triagem_obrigatoria;

-- Permissoes operacionais por papel
INSERT INTO permissao (nome)
VALUES
    ('TRIAGEM_EXECUTAR'),
    ('RECEPCAO_EXECUTAR'),
    ('EPISODIO_ABRIR'),
    ('ENTRADA_REGISTRAR')
ON CONFLICT (nome) DO NOTHING;

-- Perfis operacionais de simulacao
INSERT INTO perfil (nome)
VALUES
    ('CLASSIFICACAO_USUARIO'),
    ('RECEPCAO_USUARIO')
ON CONFLICT (nome) DO NOTHING;

-- Perfil de classificacao
INSERT INTO perfil_permissao (perfil_id, permissao_id)
SELECT p.id, pm.id
FROM perfil p
JOIN permissao pm ON pm.nome IN ('ATENDIMENTO_ACESSAR', 'PACIENTE_VISUALIZAR', 'TRIAGEM_EXECUTAR')
WHERE p.nome = 'CLASSIFICACAO_USUARIO'
ON CONFLICT (perfil_id, permissao_id) DO NOTHING;

-- Perfil de recepcao
INSERT INTO perfil_permissao (perfil_id, permissao_id)
SELECT p.id, pm.id
FROM perfil p
JOIN permissao pm ON pm.nome IN ('ATENDIMENTO_ACESSAR', 'PACIENTE_VISUALIZAR', 'PACIENTE_EDITAR', 'RECEPCAO_EXECUTAR', 'EPISODIO_ABRIR', 'ENTRADA_REGISTRAR')
WHERE p.nome = 'RECEPCAO_USUARIO'
ON CONFLICT (perfil_id, permissao_id) DO NOTHING;

-- Usuarios de simulacao no HIS (espelho; para login real no Keycloak, criar os mesmos usernames no provedor)
INSERT INTO usuario (keycloak_id, username, email, ativo)
VALUES
    ('pending:classificacao.hedh', 'classificacao.hedh', 'classificacao.hedh@his.local', TRUE),
    ('pending:recepcao.hge', 'recepcao.hge', 'recepcao.hge@his.local', TRUE)
ON CONFLICT (username) DO UPDATE
SET email = EXCLUDED.email,
    ativo = TRUE;

-- Vinculos por unidade/perfil
INSERT INTO usuario_unidade_perfil (usuario_id, unidade_id, perfil_id, ativo)
SELECT us.id, un.id, pf.id, TRUE
FROM usuario us
JOIN unidade un ON un.cnes = 'HEDH'
JOIN perfil pf ON pf.nome = 'CLASSIFICACAO_USUARIO'
WHERE us.username = 'classificacao.hedh'
ON CONFLICT (usuario_id, unidade_id, perfil_id) DO UPDATE
SET ativo = TRUE;

INSERT INTO usuario_unidade_perfil (usuario_id, unidade_id, perfil_id, ativo)
SELECT us.id, un.id, pf.id, TRUE
FROM usuario us
JOIN unidade un ON un.cnes = 'HGE'
JOIN perfil pf ON pf.nome = 'RECEPCAO_USUARIO'
WHERE us.username = 'recepcao.hge'
ON CONFLICT (usuario_id, unidade_id, perfil_id) DO UPDATE
SET ativo = TRUE;
