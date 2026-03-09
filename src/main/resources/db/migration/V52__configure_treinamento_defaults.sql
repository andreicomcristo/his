-- Consolidar ambiente inicial em uma unica unidade de treinamento

-- Remove areas e capacidades da unidade HEDH, se existirem
DELETE FROM area_capacidade
WHERE area_id IN (
    SELECT a.id
    FROM area a
    JOIN unidade u ON u.id = a.unidade_id
    WHERE u.cnes IN ('HEDH', 'HGE')
);

DELETE FROM area
WHERE unidade_id IN (
    SELECT id
    FROM unidade
    WHERE cnes IN ('HEDH', 'HGE')
);

-- Remove vinculos operacionais antigos
DELETE FROM usuario_unidade_perfil
WHERE unidade_id IN (
    SELECT id
    FROM unidade
    WHERE cnes IN ('HEDH', 'HGE')
);

DELETE FROM unidade_regra_triagem
WHERE unidade_id IN (
    SELECT id
    FROM unidade
    WHERE cnes IN ('HEDH', 'HGE')
);

-- Desvincula procedencias especificas das unidades antigas antes de remover as unidades
UPDATE procedencia
SET unidade_id = NULL
WHERE unidade_id IN (
    SELECT id
    FROM unidade
    WHERE cnes IN ('HEDH', 'HGE')
);

DELETE FROM unidade_config_fluxo
WHERE unidade_id IN (
    SELECT id
    FROM unidade
    WHERE cnes IN ('HEDH', 'HGE')
);

-- Remove usuarios operacionais antigos
DELETE FROM usuario
WHERE username IN ('classificacao.hedh', 'recepcao.hge');

-- Remove unidades auxiliares antigas
DELETE FROM unidade
WHERE cnes IN ('HEDH', 'HGE');

-- Renomeia unidade default para treinamento
UPDATE unidade
SET nome = 'TREINAMENTO',
    tipo_estabelecimento = 'HOSPITAL_GERAL',
    ativo = TRUE
WHERE cnes = 'DEFAULT';

-- Configuracao de fluxo da unidade de treinamento
INSERT INTO unidade_config_fluxo (unidade_id, primeiro_passo, exige_ficha_para_medico, cria_episodio_automatico)
SELECT u.id, 'RECEPCAO', TRUE, FALSE
FROM unidade u
WHERE u.cnes = 'DEFAULT'
ON CONFLICT (unidade_id) DO UPDATE
SET primeiro_passo = EXCLUDED.primeiro_passo,
    exige_ficha_para_medico = EXCLUDED.exige_ficha_para_medico,
    cria_episodio_automatico = EXCLUDED.cria_episodio_automatico;

INSERT INTO unidade_regra_triagem (unidade_id, tipo_atendimento, triagem_obrigatoria)
SELECT u.id, v.tipo_atendimento, v.triagem_obrigatoria
FROM unidade u
JOIN (VALUES
    ('URGENCIA', TRUE),
    ('AMBULATORIAL', FALSE),
    ('INTERNACAO_DIRETA', FALSE),
    ('PROCEDIMENTO', FALSE)
) AS v(tipo_atendimento, triagem_obrigatoria) ON TRUE
WHERE u.cnes = 'DEFAULT'
ON CONFLICT (unidade_id, tipo_atendimento) DO UPDATE
SET triagem_obrigatoria = EXCLUDED.triagem_obrigatoria;

-- Usuarios iniciais do treinamento
INSERT INTO usuario (keycloak_id, username, email, ativo)
VALUES
    ('bootstrap-admin-sub', 'admin', 'admin@his.local', TRUE),
    ('pending:recepcao', 'recepcao', 'recepcao@his.local', TRUE),
    ('pending:classificacao', 'classificacao', 'classificacao@his.local', TRUE)
ON CONFLICT (username) DO UPDATE
SET email = EXCLUDED.email,
    ativo = TRUE;

-- Garante vinculo do admin como super admin na unidade treinamento
INSERT INTO usuario_unidade_perfil (usuario_id, unidade_id, perfil_id, ativo)
SELECT us.id, un.id, pf.id, TRUE
FROM usuario us
JOIN unidade un ON un.cnes = 'DEFAULT'
JOIN perfil pf ON pf.nome = 'SUPER_ADMIN'
WHERE us.username = 'admin'
ON CONFLICT (usuario_id, unidade_id, perfil_id) DO UPDATE
SET ativo = TRUE;

-- Vínculos operacionais da unidade treinamento
INSERT INTO usuario_unidade_perfil (usuario_id, unidade_id, perfil_id, ativo)
SELECT us.id, un.id, pf.id, TRUE
FROM usuario us
JOIN unidade un ON un.cnes = 'DEFAULT'
JOIN perfil pf ON pf.nome = 'RECEPCAO_USUARIO'
WHERE us.username = 'recepcao'
ON CONFLICT (usuario_id, unidade_id, perfil_id) DO UPDATE
SET ativo = TRUE;

INSERT INTO usuario_unidade_perfil (usuario_id, unidade_id, perfil_id, ativo)
SELECT us.id, un.id, pf.id, TRUE
FROM usuario us
JOIN unidade un ON un.cnes = 'DEFAULT'
JOIN perfil pf ON pf.nome = 'CLASSIFICACAO_USUARIO'
WHERE us.username = 'classificacao'
ON CONFLICT (usuario_id, unidade_id, perfil_id) DO UPDATE
SET ativo = TRUE;
