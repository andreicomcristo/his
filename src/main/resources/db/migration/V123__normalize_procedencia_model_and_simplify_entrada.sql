-- Normaliza procedencia como origem unica e simplifica entrada para usar apenas procedencia_id.

ALTER TABLE procedencia
    ADD COLUMN IF NOT EXISTS bairro_id BIGINT,
    ADD COLUMN IF NOT EXISTS municipio_id BIGINT,
    ADD COLUMN IF NOT EXISTS ativo BOOLEAN;

UPDATE procedencia
SET ativo = TRUE
WHERE ativo IS NULL;

ALTER TABLE procedencia
    ALTER COLUMN ativo SET NOT NULL,
    ALTER COLUMN ativo SET DEFAULT TRUE;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.table_constraints
        WHERE table_schema = 'public'
          AND table_name = 'procedencia'
          AND constraint_name = 'fk_procedencia_bairro'
    ) THEN
        ALTER TABLE procedencia
            ADD CONSTRAINT fk_procedencia_bairro
                FOREIGN KEY (bairro_id) REFERENCES bairro(id);
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.table_constraints
        WHERE table_schema = 'public'
          AND table_name = 'procedencia'
          AND constraint_name = 'fk_procedencia_municipio'
    ) THEN
        ALTER TABLE procedencia
            ADD CONSTRAINT fk_procedencia_municipio
                FOREIGN KEY (municipio_id) REFERENCES municipio(id);
    END IF;
END $$;

CREATE INDEX IF NOT EXISTS idx_procedencia_bairro_id ON procedencia (bairro_id);
CREATE INDEX IF NOT EXISTS idx_procedencia_municipio_id ON procedencia (municipio_id);
CREATE INDEX IF NOT EXISTS idx_procedencia_ativo ON procedencia (ativo);

DO $$
DECLARE
    v_bairro_tipo_id BIGINT;
    v_cidade_tipo_id BIGINT;
    v_outros_tipo_id BIGINT;
BEGIN
    SELECT id INTO v_bairro_tipo_id
    FROM tipo_procedencia
    WHERE UPPER(TRANSLATE(COALESCE(descricao, ''),
            'ÁÀÂÃÄÉÈÊËÍÌÎÏÓÒÔÕÖÚÙÛÜÇáàâãäéèêëíìîïóòôõöúùûüç',
            'AAAAAEEEEIIIIOOOOOUUUUCaaaaaeeeeiiiiooooouuuuc')) = 'BAIRRO'
    ORDER BY id
    LIMIT 1;

    SELECT id INTO v_cidade_tipo_id
    FROM tipo_procedencia
    WHERE UPPER(TRANSLATE(COALESCE(descricao, ''),
            'ÁÀÂÃÄÉÈÊËÍÌÎÏÓÒÔÕÖÚÙÛÜÇáàâãäéèêëíìîïóòôõöúùûüç',
            'AAAAAEEEEIIIIOOOOOUUUUCaaaaaeeeeiiiiooooouuuuc')) IN ('CIDADE', 'MUNICIPIO')
    ORDER BY id
    LIMIT 1;

    SELECT id INTO v_outros_tipo_id
    FROM tipo_procedencia
    WHERE UPPER(TRANSLATE(COALESCE(descricao, ''),
            'ÁÀÂÃÄÉÈÊËÍÌÎÏÓÒÔÕÖÚÙÛÜÇáàâãäéèêëíìîïóòôõöúùûüç',
            'AAAAAEEEEIIIIOOOOOUUUUCaaaaaeeeeiiiiooooouuuuc')) LIKE 'OUTRO%'
    ORDER BY id
    LIMIT 1;

    IF v_bairro_tipo_id IS NULL OR v_cidade_tipo_id IS NULL OR v_outros_tipo_id IS NULL THEN
        RAISE EXCEPTION 'Tipos de procedencia BAIRRO/CIDADE/OUTROS nao encontrados.';
    END IF;

    -- Mapeia procedencias de BAIRRO/CIDADE por descricao para os ids estruturados.
    UPDATE procedencia p
       SET bairro_id = b.id,
           municipio_id = NULL
      FROM bairro b
     WHERE p.tipo_procedencia_id = v_bairro_tipo_id
       AND p.bairro_id IS NULL
       AND UPPER(TRANSLATE(COALESCE(TRIM(p.descricao), ''),
               'ÁÀÂÃÄÉÈÊËÍÌÎÏÓÒÔÕÖÚÙÛÜÇáàâãäéèêëíìîïóòôõöúùûüç',
               'AAAAAEEEEIIIIOOOOOUUUUCaaaaaeeeeiiiiooooouuuuc'))
           = UPPER(TRANSLATE(COALESCE(TRIM(b.nome), ''),
               'ÁÀÂÃÄÉÈÊËÍÌÎÏÓÒÔÕÖÚÙÛÜÇáàâãäéèêëíìîïóòôõöúùûüç',
               'AAAAAEEEEIIIIOOOOOUUUUCaaaaaeeeeiiiiooooouuuuc'));

    UPDATE procedencia p
       SET municipio_id = m.id,
           bairro_id = NULL
      FROM municipio m
     WHERE p.tipo_procedencia_id = v_cidade_tipo_id
       AND p.municipio_id IS NULL
       AND UPPER(TRANSLATE(COALESCE(TRIM(p.descricao), ''),
               'ÁÀÂÃÄÉÈÊËÍÌÎÏÓÒÔÕÖÚÙÛÜÇáàâãäéèêëíìîïóòôõöúùûüç',
               'AAAAAEEEEIIIIOOOOOUUUUCaaaaaeeeeiiiiooooouuuuc'))
           = UPPER(TRANSLATE(COALESCE(TRIM(m.nome), ''),
               'ÁÀÂÃÄÉÈÊËÍÌÎÏÓÒÔÕÖÚÙÛÜÇáàâãäéèêëíìîïóòôõöúùûüç',
               'AAAAAEEEEIIIIOOOOOUUUUCaaaaaeeeeiiiiooooouuuuc'));

    -- Registros inconsistentes de BAIRRO/CIDADE passam para OUTROS.
    UPDATE procedencia
       SET tipo_procedencia_id = v_outros_tipo_id,
           bairro_id = NULL,
           municipio_id = NULL
     WHERE (tipo_procedencia_id = v_bairro_tipo_id AND bairro_id IS NULL)
        OR (tipo_procedencia_id = v_cidade_tipo_id AND municipio_id IS NULL);

    -- Qualquer tipo diferente de BAIRRO/CIDADE/OUTROS tambem vira OUTROS.
    UPDATE procedencia
       SET tipo_procedencia_id = v_outros_tipo_id,
           bairro_id = NULL,
           municipio_id = NULL
     WHERE tipo_procedencia_id IS NULL
        OR tipo_procedencia_id NOT IN (v_bairro_tipo_id, v_cidade_tipo_id, v_outros_tipo_id);

    UPDATE procedencia
       SET descricao = 'OUTROS'
     WHERE tipo_procedencia_id = v_outros_tipo_id
       AND COALESCE(TRIM(descricao), '') = '';

    -- Mantem descricao legivel para BAIRRO/CIDADE.
    UPDATE procedencia p
       SET descricao = b.nome
      FROM bairro b
     WHERE p.tipo_procedencia_id = v_bairro_tipo_id
       AND p.bairro_id = b.id;

    UPDATE procedencia p
       SET descricao = m.nome
      FROM municipio m
     WHERE p.tipo_procedencia_id = v_cidade_tipo_id
       AND p.municipio_id = m.id;

    -- Cria procedencias estruturadas para entradas antigas que ainda usam colunas legadas.
    INSERT INTO procedencia (descricao, tipo_procedencia_id, bairro_id, municipio_id, unidade_id, ativo)
    SELECT DISTINCT b.nome, v_bairro_tipo_id, e.procedencia_bairro_id, NULL::BIGINT, NULL::BIGINT, TRUE
      FROM entrada e
      JOIN bairro b ON b.id = e.procedencia_bairro_id
 LEFT JOIN procedencia p ON p.tipo_procedencia_id = v_bairro_tipo_id
                        AND p.bairro_id = e.procedencia_bairro_id
                        AND p.unidade_id IS NULL
     WHERE e.procedencia_bairro_id IS NOT NULL
       AND p.id IS NULL;

    INSERT INTO procedencia (descricao, tipo_procedencia_id, bairro_id, municipio_id, unidade_id, ativo)
    SELECT DISTINCT m.nome, v_cidade_tipo_id, NULL::BIGINT, e.procedencia_municipio_id, NULL::BIGINT, TRUE
      FROM entrada e
      JOIN municipio m ON m.id = e.procedencia_municipio_id
 LEFT JOIN procedencia p ON p.tipo_procedencia_id = v_cidade_tipo_id
                        AND p.municipio_id = e.procedencia_municipio_id
                        AND p.unidade_id IS NULL
     WHERE e.procedencia_municipio_id IS NOT NULL
       AND p.id IS NULL;

    INSERT INTO procedencia (descricao, tipo_procedencia_id, bairro_id, municipio_id, unidade_id, ativo)
    SELECT DISTINCT TRIM(e.procedencia_observacao), v_outros_tipo_id, NULL::BIGINT, NULL::BIGINT, NULL::BIGINT, TRUE
      FROM entrada e
 LEFT JOIN procedencia p ON p.tipo_procedencia_id = v_outros_tipo_id
                        AND p.unidade_id IS NULL
                        AND UPPER(TRIM(COALESCE(p.descricao, ''))) = UPPER(TRIM(COALESCE(e.procedencia_observacao, '')))
     WHERE e.procedencia_id IS NULL
       AND e.tipo_procedencia_id = v_outros_tipo_id
       AND COALESCE(TRIM(e.procedencia_observacao), '') <> ''
       AND p.id IS NULL;

    -- Converte colunas legadas da entrada para procedencia_id.
    UPDATE entrada e
       SET procedencia_id = p.id
      FROM procedencia p
     WHERE e.procedencia_id IS NULL
       AND e.procedencia_bairro_id IS NOT NULL
       AND p.tipo_procedencia_id = v_bairro_tipo_id
       AND p.bairro_id = e.procedencia_bairro_id
       AND p.unidade_id IS NULL;

    UPDATE entrada e
       SET procedencia_id = p.id
      FROM procedencia p
     WHERE e.procedencia_id IS NULL
       AND e.procedencia_municipio_id IS NOT NULL
       AND p.tipo_procedencia_id = v_cidade_tipo_id
       AND p.municipio_id = e.procedencia_municipio_id
       AND p.unidade_id IS NULL;

    UPDATE entrada e
       SET procedencia_id = p.id
      FROM procedencia p
     WHERE e.procedencia_id IS NULL
       AND e.tipo_procedencia_id = v_outros_tipo_id
       AND COALESCE(TRIM(e.procedencia_observacao), '') <> ''
       AND p.tipo_procedencia_id = v_outros_tipo_id
       AND p.unidade_id IS NULL
       AND UPPER(TRIM(COALESCE(p.descricao, ''))) = UPPER(TRIM(COALESCE(e.procedencia_observacao, '')));

    -- Regras de consistencia por tipo (constraint com ids canonicos).
    EXECUTE 'ALTER TABLE procedencia DROP CONSTRAINT IF EXISTS ck_procedencia_tipo_campos';
    EXECUTE format(
        'ALTER TABLE procedencia ADD CONSTRAINT ck_procedencia_tipo_campos CHECK (
            (tipo_procedencia_id = %s AND bairro_id IS NOT NULL AND municipio_id IS NULL)
            OR (tipo_procedencia_id = %s AND municipio_id IS NOT NULL AND bairro_id IS NULL)
            OR (tipo_procedencia_id = %s AND bairro_id IS NULL AND municipio_id IS NULL AND COALESCE(BTRIM(descricao), '''') <> '''')
        )',
        v_bairro_tipo_id, v_cidade_tipo_id, v_outros_tipo_id
    );
END $$;

ALTER TABLE entrada
    DROP CONSTRAINT IF EXISTS fk_entrada_tipo_procedencia,
    DROP CONSTRAINT IF EXISTS fk_entrada_procedencia_bairro,
    DROP CONSTRAINT IF EXISTS fk_entrada_procedencia_municipio;

DROP INDEX IF EXISTS idx_entrada_tipo_procedencia_id;
DROP INDEX IF EXISTS idx_entrada_procedencia_bairro_id;
DROP INDEX IF EXISTS idx_entrada_procedencia_municipio_id;

ALTER TABLE entrada
    DROP COLUMN IF EXISTS tipo_procedencia_id,
    DROP COLUMN IF EXISTS procedencia_bairro_id,
    DROP COLUMN IF EXISTS procedencia_municipio_id;
