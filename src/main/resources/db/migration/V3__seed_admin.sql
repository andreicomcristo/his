UPDATE unidade
SET nome = 'UNIDADE DEFAULT',
    tipo_estabelecimento = 'HOSPITAL_GERAL',
    ativo = TRUE
WHERE cnes = 'DEFAULT';

INSERT INTO unidade (nome, tipo_estabelecimento, cnes, ativo)
SELECT 'UNIDADE DEFAULT', 'HOSPITAL_GERAL', 'DEFAULT', TRUE
WHERE NOT EXISTS (SELECT 1 FROM unidade WHERE cnes = 'DEFAULT');

INSERT INTO perfil (nome)
VALUES ('SUPER_ADMIN')
ON CONFLICT (nome) DO NOTHING;

INSERT INTO permissao (nome)
VALUES
    ('ADMIN_USUARIOS'),
    ('UNIDADE_GERENCIAR'),
    ('PERFIL_GERENCIAR'),
    ('PERMISSAO_VISUALIZAR')
ON CONFLICT (nome) DO NOTHING;

INSERT INTO perfil_permissao (perfil_id, permissao_id)
SELECT p.id, pm.id
FROM perfil p
CROSS JOIN permissao pm
WHERE p.nome = 'SUPER_ADMIN'
ON CONFLICT (perfil_id, permissao_id) DO NOTHING;

INSERT INTO usuario (keycloak_id, username, email, ativo)
VALUES (
    '${bootstrap_admin_sub}',
    '${bootstrap_admin_username}',
    '${bootstrap_admin_email}',
    TRUE
)
ON CONFLICT (keycloak_id) DO UPDATE
SET username = COALESCE(NULLIF(usuario.username, ''), EXCLUDED.username),
    email = COALESCE(NULLIF(usuario.email, ''), EXCLUDED.email),
    ativo = TRUE;

INSERT INTO usuario_unidade_perfil (usuario_id, unidade_id, perfil_id, ativo)
SELECT u.id, un.id, p.id, TRUE
FROM usuario u
JOIN unidade un ON un.cnes = 'DEFAULT'
JOIN perfil p ON p.nome = 'SUPER_ADMIN'
WHERE u.keycloak_id = '${bootstrap_admin_sub}'
ON CONFLICT (usuario_id, unidade_id, perfil_id) DO UPDATE
SET ativo = TRUE;
