INSERT INTO permissao (nome)
VALUES ('ADMIN_SISTEMA')
ON CONFLICT (nome) DO NOTHING;

INSERT INTO perfil_permissao (perfil_id, permissao_id)
SELECT p.id, pm.id
FROM perfil p
JOIN permissao pm ON pm.nome = 'ADMIN_SISTEMA'
WHERE p.nome = 'SUPER_ADMIN'
ON CONFLICT (perfil_id, permissao_id) DO NOTHING;
