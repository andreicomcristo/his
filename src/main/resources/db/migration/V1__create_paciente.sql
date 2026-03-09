CREATE TABLE paciente (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    nome_social VARCHAR(150),
    cpf VARCHAR(11),
    cns VARCHAR(20),
    rg VARCHAR(30),
    data_nascimento DATE,
    sexo VARCHAR(10),
    telefone VARCHAR(20),
    nome_mae VARCHAR(150),
    nome_pai VARCHAR(150),
    email VARCHAR(150),
    observacoes TEXT,
    cep VARCHAR(10),
    logradouro VARCHAR(200),
    numero VARCHAR(20),
    complemento VARCHAR(100),
    bairro VARCHAR(100),
    cidade VARCHAR(100),
    uf VARCHAR(2),
    temporario BOOLEAN NOT NULL DEFAULT FALSE,
    idade_aparente INTEGER,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    data_cancelamento TIMESTAMP,
    merged_into_id BIGINT,
    criado_em TIMESTAMP NOT NULL DEFAULT NOW(),
    atualizado_em TIMESTAMP,
    criado_por VARCHAR(100),
    atualizado_por VARCHAR(100),
    CONSTRAINT fk_paciente_merged_into
        FOREIGN KEY (merged_into_id) REFERENCES paciente (id),
    CONSTRAINT ck_paciente_idade_aparente
        CHECK (idade_aparente IS NULL OR (idade_aparente >= 0 AND idade_aparente <= 150))
);

CREATE UNIQUE INDEX uq_paciente_cpf_not_null
    ON paciente (cpf)
    WHERE cpf IS NOT NULL
      AND BTRIM(cpf) <> ''
      AND ativo = TRUE
      AND merged_into_id IS NULL;

CREATE INDEX idx_paciente_nome ON paciente (UPPER(nome));
CREATE INDEX idx_paciente_cns ON paciente (cns);

CREATE TABLE paciente_merge_log (
    id BIGSERIAL PRIMARY KEY,
    from_paciente_id BIGINT NOT NULL,
    to_paciente_id BIGINT NOT NULL,
    motivo TEXT,
    merged_em TIMESTAMP NOT NULL DEFAULT NOW(),
    merged_por VARCHAR(100) NOT NULL,
    CONSTRAINT fk_merge_log_from
        FOREIGN KEY (from_paciente_id) REFERENCES paciente (id),
    CONSTRAINT fk_merge_log_to
        FOREIGN KEY (to_paciente_id) REFERENCES paciente (id)
);

CREATE INDEX idx_paciente_merge_log_from ON paciente_merge_log (from_paciente_id);
CREATE INDEX idx_paciente_merge_log_to ON paciente_merge_log (to_paciente_id);
CREATE INDEX idx_paciente_merge_log_merged_em ON paciente_merge_log (merged_em);
