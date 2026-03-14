ALTER TABLE unidade_federativa
    RENAME COLUMN nome TO descricao;

ALTER TABLE cidade
    RENAME TO municipio;

ALTER TABLE municipio
    RENAME CONSTRAINT uq_cidade_nome_uf TO uq_municipio_nome_uf;

ALTER TABLE paciente
    RENAME COLUMN cidade_id TO municipio_id;

ALTER TABLE unidade
    RENAME COLUMN cidade_id TO municipio_id;

ALTER TABLE bairro
    RENAME COLUMN cidade_id TO municipio_id;

ALTER TABLE entrada
    RENAME COLUMN procedencia_cidade_id TO procedencia_municipio_id;

ALTER TABLE paciente
    RENAME CONSTRAINT fk_paciente_cidade TO fk_paciente_municipio;

ALTER TABLE unidade
    RENAME CONSTRAINT fk_unidade_cidade TO fk_unidade_municipio;

ALTER TABLE bairro
    RENAME CONSTRAINT fk_bairro_cidade TO fk_bairro_municipio;

ALTER TABLE entrada
    RENAME CONSTRAINT fk_entrada_procedencia_cidade TO fk_entrada_procedencia_municipio;

ALTER INDEX IF EXISTS idx_cidade_unidade_federativa
    RENAME TO idx_municipio_unidade_federativa;

ALTER INDEX IF EXISTS idx_cidade_nome
    RENAME TO idx_municipio_nome;

ALTER INDEX IF EXISTS idx_paciente_cidade
    RENAME TO idx_paciente_municipio;

ALTER INDEX IF EXISTS idx_unidade_cidade_id
    RENAME TO idx_unidade_municipio_id;

ALTER INDEX IF EXISTS idx_bairro_cidade
    RENAME TO idx_bairro_municipio;

ALTER INDEX IF EXISTS idx_entrada_procedencia_cidade_id
    RENAME TO idx_entrada_procedencia_municipio_id;
