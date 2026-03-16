-- Mantem somente BAIRRO, CIDADE e OUTROS em tipo_procedencia.
-- Mantem somente procedencias de BAIRRO e CIDADE no catalogo procedencia.

DO $$
DECLARE
    v_bairro_id BIGINT;
    v_cidade_id BIGINT;
    v_outros_id BIGINT;
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

    SELECT t.id
      INTO v_outros_id
      FROM (
            SELECT id,
                   UPPER(TRANSLATE(COALESCE(descricao, ''),
                                   'ÁÀÂÃÄÉÈÊËÍÌÎÏÓÒÔÕÖÚÙÛÜÇáàâãäéèêëíìîïóòôõöúùûüç',
                                   'AAAAAEEEEIIIIOOOOOUUUUCaaaaaeeeeiiiiooooouuuuc')) AS norm
              FROM tipo_procedencia
           ) t
     WHERE t.norm LIKE 'OUTRO%'
     ORDER BY t.id
     LIMIT 1;

    IF v_outros_id IS NULL THEN
        INSERT INTO tipo_procedencia (descricao) VALUES ('OUTROS') RETURNING id INTO v_outros_id;
    END IF;

    UPDATE tipo_procedencia SET descricao = 'BAIRRO' WHERE id = v_bairro_id;
    UPDATE tipo_procedencia SET descricao = 'CIDADE' WHERE id = v_cidade_id;
    UPDATE tipo_procedencia SET descricao = 'OUTROS' WHERE id = v_outros_id;

    -- Se houver tipos equivalentes duplicados de CIDADE/MUNICIPIO, consolida para o tipo CIDADE canonical.
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

    -- Procedencias fora de BAIRRO/CIDADE deixam de ser usadas nas entradas;
    -- preserva descricao antiga em procedencia_observacao quando vazia.
    UPDATE entrada e
       SET procedencia_observacao = CASE
               WHEN COALESCE(TRIM(e.procedencia_observacao), '') = '' THEN p.descricao
               ELSE e.procedencia_observacao
           END,
           procedencia_id = NULL,
           tipo_procedencia_id = v_outros_id
      FROM procedencia p
     WHERE e.procedencia_id = p.id
       AND (p.tipo_procedencia_id IS NULL OR p.tipo_procedencia_id NOT IN (v_bairro_id, v_cidade_id));

    -- Entradas com tipos antigos passam a OUTROS.
    UPDATE entrada
       SET tipo_procedencia_id = v_outros_id
     WHERE tipo_procedencia_id IS NOT NULL
       AND tipo_procedencia_id NOT IN (v_bairro_id, v_cidade_id, v_outros_id);

    -- Paciente nao deve apontar para procedencias que serao removidas.
    UPDATE paciente pa
       SET procedencia_id = NULL
     WHERE pa.procedencia_id IN (
         SELECT p.id
           FROM procedencia p
          WHERE p.tipo_procedencia_id IS NULL
             OR p.tipo_procedencia_id NOT IN (v_bairro_id, v_cidade_id)
     );

    -- Limpa catalogo procedencia para manter somente CIDADE e BAIRRO.
    DELETE FROM procedencia
     WHERE tipo_procedencia_id IS NULL
        OR tipo_procedencia_id NOT IN (v_bairro_id, v_cidade_id);

    -- Remove tipos legados, mantendo somente BAIRRO/CIDADE/OUTROS.
    DELETE FROM tipo_procedencia
     WHERE id NOT IN (v_bairro_id, v_cidade_id, v_outros_id);
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
