ALTER TABLE unidade_federativa
    ALTER COLUMN codigo_ibge TYPE VARCHAR(2)
    USING NULLIF(BTRIM(codigo_ibge), '');
