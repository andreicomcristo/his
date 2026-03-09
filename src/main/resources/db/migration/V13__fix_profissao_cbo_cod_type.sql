ALTER TABLE profissao
    ALTER COLUMN cbo_cod TYPE VARCHAR(6)
    USING NULLIF(BTRIM(cbo_cod), '');
