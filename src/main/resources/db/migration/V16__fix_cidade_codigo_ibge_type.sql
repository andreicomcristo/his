ALTER TABLE cidade
    ALTER COLUMN codigo_ibge TYPE VARCHAR(5)
    USING NULLIF(BTRIM(codigo_ibge), '');
