ALTER TABLE unidade_federativa
    ALTER COLUMN sigla TYPE VARCHAR(2)
    USING NULLIF(BTRIM(sigla), '');
