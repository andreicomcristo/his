-- Mantem apenas os tipos CIDADE e BAIRRO.
-- Limpa procedencia para manter catalogo padrao baseado em CIDADE.

DO $$
DECLARE
    v_bairro_id BIGINT;
    v_cidade_id BIGINT;
BEGIN
    SELECT t.id
      INTO v_bairro_id
      FROM (
            SELECT id,
                   UPPER(TRANSLATE(COALESCE(descricao, ''),
                                   'ÁÀÂÃÄÉÈÊËÍÌÎÏÓÒÔÕÖÚÙÛÜÇáàâãäéèêëíìîïóòôõöúùûüç',
                                   'AAAAAEEEEIIIIOOOOOUUUUCaaaaaeeeeiiiiooooouuuuc')) AS norm
              FROM tipo_procedencia
           ) t
     WHERE t.norm = 'BAIRRO'
     ORDER BY t.id
     LIMIT 1;

    IF v_bairro_id IS NULL THEN
        INSERT INTO tipo_procedencia (descricao) VALUES ('BAIRRO') RETURNING id INTO v_bairro_id;
    END IF;

    SELECT t.id
      INTO v_cidade_id
      FROM (
            SELECT id,
                   UPPER(TRANSLATE(COALESCE(descricao, ''),
                                   'ÁÀÂÃÄÉÈÊËÍÌÎÏÓÒÔÕÖÚÙÛÜÇáàâãäéèêëíìîïóòôõöúùûüç',
                                   'AAAAAEEEEIIIIOOOOOUUUUCaaaaaeeeeiiiiooooouuuuc')) AS norm
              FROM tipo_procedencia
           ) t
     WHERE t.norm IN ('CIDADE', 'MUNICIPIO')
     ORDER BY CASE WHEN t.norm = 'CIDADE' THEN 0 ELSE 1 END, t.id
     LIMIT 1;

    IF v_cidade_id IS NULL THEN
        INSERT INTO tipo_procedencia (descricao) VALUES ('CIDADE') RETURNING id INTO v_cidade_id;
    END IF;

    UPDATE tipo_procedencia SET descricao = 'BAIRRO' WHERE id = v_bairro_id;
    UPDATE tipo_procedencia SET descricao = 'CIDADE' WHERE id = v_cidade_id;

    -- Consolida tipos antigos equivalentes de CIDADE/MUNICIPIO no id canonico.
    UPDATE procedencia p
       SET tipo_procedencia_id = v_cidade_id
     WHERE p.tipo_procedencia_id IN (
         SELECT id
           FROM (
                 SELECT id,
                        UPPER(TRANSLATE(COALESCE(descricao, ''),
                                        'ÁÀÂÃÄÉÈÊËÍÌÎÏÓÒÔÕÖÚÙÛÜÇáàâãäéèêëíìîïóòôõöúùûüç',
                                        'AAAAAEEEEIIIIOOOOOUUUUCaaaaaeeeeiiiiooooouuuuc')) AS norm
                   FROM tipo_procedencia
                ) t
          WHERE t.norm IN ('CIDADE', 'MUNICIPIO')
            AND t.id <> v_cidade_id
     );

    -- Referencias antigas para tipos diferentes de CIDADE sao limpas.
    UPDATE entrada e
       SET procedencia_id = NULL
     WHERE e.procedencia_id IN (
         SELECT p.id
           FROM procedencia p
          WHERE p.tipo_procedencia_id IS NULL
             OR p.tipo_procedencia_id <> v_cidade_id
     );

    UPDATE paciente pa
       SET procedencia_id = NULL
     WHERE pa.procedencia_id IN (
         SELECT p.id
           FROM procedencia p
          WHERE p.tipo_procedencia_id IS NULL
             OR p.tipo_procedencia_id <> v_cidade_id
     );

    -- Mantem apenas procedencias de CIDADE.
    DELETE FROM procedencia p
     WHERE p.tipo_procedencia_id IS NULL
        OR p.tipo_procedencia_id <> v_cidade_id;

    -- Garante consistencia dos registros de CIDADE.
    UPDATE procedencia p
       SET descricao = m.nome,
           bairro_id = NULL,
           ativo = TRUE
      FROM municipio m
     WHERE p.tipo_procedencia_id = v_cidade_id
       AND p.municipio_id = m.id;

    DELETE FROM procedencia p
     WHERE p.tipo_procedencia_id = v_cidade_id
       AND p.municipio_id IS NULL;

    -- Completa catalogo padrao com todos os municipios.
    INSERT INTO procedencia (descricao, tipo_procedencia_id, bairro_id, municipio_id, unidade_id, ativo)
    SELECT m.nome, v_cidade_id, NULL::BIGINT, m.id, NULL::BIGINT, TRUE
      FROM municipio m
 LEFT JOIN procedencia p ON p.tipo_procedencia_id = v_cidade_id
                        AND p.municipio_id = m.id
                        AND p.unidade_id IS NULL
     WHERE p.id IS NULL;

    -- Ajusta constraint para aceitar somente BAIRRO e CIDADE.
    EXECUTE 'ALTER TABLE procedencia DROP CONSTRAINT IF EXISTS ck_procedencia_tipo_campos';
    EXECUTE format(
        'ALTER TABLE procedencia ADD CONSTRAINT ck_procedencia_tipo_campos CHECK (
            (tipo_procedencia_id = %s AND bairro_id IS NOT NULL AND municipio_id IS NULL)
            OR (tipo_procedencia_id = %s AND municipio_id IS NOT NULL AND bairro_id IS NULL)
        )',
        v_bairro_id, v_cidade_id
    );

    -- Remove tipos fora do padrao (mantem apenas BAIRRO e CIDADE).
    DELETE FROM tipo_procedencia
     WHERE id NOT IN (v_bairro_id, v_cidade_id);
END $$;

SELECT setval(
    pg_get_serial_sequence('tipo_procedencia', 'id'),
    COALESCE((SELECT MAX(id) FROM tipo_procedencia), 1),
    true
);

SELECT setval(
    pg_get_serial_sequence('procedencia', 'id'),
    COALESCE((SELECT MAX(id) FROM procedencia), 1),
    true
);
