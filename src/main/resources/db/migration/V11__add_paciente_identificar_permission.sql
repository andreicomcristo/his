INSERT INTO permissao (nome)
VALUES ('PACIENTE_IDENTIFICAR')
ON CONFLICT (nome) DO NOTHING;

INSERT INTO perfil_permissao (perfil_id, permissao_id)
SELECT p.id, pm.id
FROM perfil p
JOIN permissao pm ON pm.nome = 'PACIENTE_IDENTIFICAR'
WHERE p.nome IN ('SUPER_ADMIN', 'CLASSIFICACAO_USUARIO')
ON CONFLICT (perfil_id, permissao_id) DO NOTHING;
