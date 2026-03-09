CREATE TABLE unidade_config_fluxo (
    unidade_id BIGINT PRIMARY KEY,
    primeiro_passo VARCHAR(20) NOT NULL,
    exige_ficha_para_medico BOOLEAN NOT NULL DEFAULT FALSE,
    cria_episodio_automatico BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_unidade_config_fluxo_unidade
        FOREIGN KEY (unidade_id) REFERENCES unidade (id),
    CONSTRAINT ck_unidade_config_fluxo_primeiro_passo
        CHECK (primeiro_passo IN ('TRIAGEM', 'RECEPCAO'))
);

CREATE TABLE unidade_regra_triagem (
    id BIGSERIAL PRIMARY KEY,
    unidade_id BIGINT NOT NULL,
    tipo_atendimento VARCHAR(40) NOT NULL,
    triagem_obrigatoria BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_unidade_regra_triagem_unidade
        FOREIGN KEY (unidade_id) REFERENCES unidade (id),
    CONSTRAINT uq_unidade_regra_triagem_unidade_tipo
        UNIQUE (unidade_id, tipo_atendimento)
);

CREATE TABLE atendimento (
    id BIGSERIAL PRIMARY KEY,
    paciente_id BIGINT NOT NULL,
    unidade_id BIGINT NOT NULL,
    tipo_atendimento VARCHAR(40) NOT NULL,
    status VARCHAR(40) NOT NULL,
    data_hora_chegada TIMESTAMP NOT NULL,
    usuario_criacao VARCHAR(120),
    data_criacao TIMESTAMP NOT NULL,
    CONSTRAINT fk_atendimento_paciente
        FOREIGN KEY (paciente_id) REFERENCES paciente (id),
    CONSTRAINT fk_atendimento_unidade
        FOREIGN KEY (unidade_id) REFERENCES unidade (id),
    CONSTRAINT ck_atendimento_tipo
        CHECK (tipo_atendimento IN ('URGENCIA', 'AMBULATORIAL', 'INTERNACAO_DIRETA', 'PROCEDIMENTO')),
    CONSTRAINT ck_atendimento_status
        CHECK (status IN ('AGUARDANDO', 'EM_TRIAGEM', 'AGUARDANDO_RECEPCAO', 'AGUARDANDO_MEDICO', 'EM_ATENDIMENTO', 'FINALIZADO', 'EVADIU'))
);

CREATE TABLE classificacao_risco (
    id BIGSERIAL PRIMARY KEY,
    atendimento_id BIGINT NOT NULL,
    nivel VARCHAR(20),
    pressao_arterial VARCHAR(20),
    temperatura NUMERIC(5,2),
    frequencia_cardiaca INTEGER,
    saturacao_o2 INTEGER,
    queixa_principal TEXT,
    data_inicio TIMESTAMP NOT NULL,
    data_fim TIMESTAMP,
    CONSTRAINT fk_classificacao_risco_atendimento
        FOREIGN KEY (atendimento_id) REFERENCES atendimento (id),
    CONSTRAINT ck_classificacao_risco_nivel
        CHECK (nivel IS NULL OR nivel IN ('VERMELHO', 'LARANJA', 'AMARELO', 'VERDE', 'AZUL'))
);

CREATE TABLE episodio (
    id BIGSERIAL PRIMARY KEY,
    atendimento_id BIGINT NOT NULL,
    paciente_id BIGINT NOT NULL,
    unidade_id BIGINT NOT NULL,
    status VARCHAR(30) NOT NULL,
    data_abertura TIMESTAMP NOT NULL,
    data_fechamento TIMESTAMP,
    CONSTRAINT uq_episodio_atendimento UNIQUE (atendimento_id),
    CONSTRAINT fk_episodio_atendimento
        FOREIGN KEY (atendimento_id) REFERENCES atendimento (id),
    CONSTRAINT fk_episodio_paciente
        FOREIGN KEY (paciente_id) REFERENCES paciente (id),
    CONSTRAINT fk_episodio_unidade
        FOREIGN KEY (unidade_id) REFERENCES unidade (id),
    CONSTRAINT ck_episodio_status
        CHECK (status IN ('ABERTO', 'OBSERVACAO', 'INTERNADO', 'ALTA', 'OBITO', 'CANCELADO'))
);

CREATE TABLE entrada (
    id BIGSERIAL PRIMARY KEY,
    episodio_id BIGINT NOT NULL,
    procedencia VARCHAR(120),
    acompanhante VARCHAR(150),
    convenio VARCHAR(120),
    guia VARCHAR(120),
    atualizado_em TIMESTAMP NOT NULL,
    atualizado_por VARCHAR(120),
    CONSTRAINT uq_entrada_episodio UNIQUE (episodio_id),
    CONSTRAINT fk_entrada_episodio
        FOREIGN KEY (episodio_id) REFERENCES episodio (id)
);

CREATE TABLE atendimento_periodo (
    id BIGSERIAL PRIMARY KEY,
    atendimento_id BIGINT NOT NULL,
    tipo VARCHAR(40) NOT NULL,
    inicio_em TIMESTAMP NOT NULL,
    fim_em TIMESTAMP,
    usuario_inicio VARCHAR(120),
    usuario_fim VARCHAR(120),
    metadata JSONB NOT NULL DEFAULT '{}'::jsonb,
    CONSTRAINT fk_atendimento_periodo_atendimento
        FOREIGN KEY (atendimento_id) REFERENCES atendimento (id),
    CONSTRAINT ck_atendimento_periodo_tipo
        CHECK (tipo IN ('CHEGADA', 'RECEPCAO', 'TRIAGEM', 'AGUARDANDO_MEDICO', 'CONSULTORIO', 'OBSERVACAO', 'SAIDA_TEMPORARIA'))
);

CREATE TABLE atendimento_evento (
    id BIGSERIAL PRIMARY KEY,
    atendimento_id BIGINT NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    data_hora TIMESTAMP NOT NULL,
    usuario VARCHAR(120),
    metadata JSONB NOT NULL DEFAULT '{}'::jsonb,
    CONSTRAINT fk_atendimento_evento_atendimento
        FOREIGN KEY (atendimento_id) REFERENCES atendimento (id),
    CONSTRAINT ck_atendimento_evento_tipo
        CHECK (tipo IN ('ATENDIMENTO_CRIADO', 'TRIAGEM_FINALIZADA', 'EPISODIO_ABERTO', 'ENTRADA_REGISTRADA', 'FINALIZADO'))
);

CREATE UNIQUE INDEX uq_classificacao_risco_aberta
    ON classificacao_risco (atendimento_id)
    WHERE data_fim IS NULL;

CREATE UNIQUE INDEX uq_atendimento_periodo_aberto
    ON atendimento_periodo (atendimento_id, tipo)
    WHERE fim_em IS NULL;

CREATE INDEX idx_atendimento_unidade_chegada
    ON atendimento (unidade_id, data_hora_chegada DESC);

CREATE INDEX idx_atendimento_status
    ON atendimento (status);

CREATE INDEX idx_classificacao_risco_atendimento_data
    ON classificacao_risco (atendimento_id, data_inicio);

CREATE INDEX idx_episodio_unidade_status
    ON episodio (unidade_id, status);

CREATE INDEX idx_atendimento_periodo_atendimento_inicio
    ON atendimento_periodo (atendimento_id, inicio_em);

CREATE INDEX idx_atendimento_periodo_inicio
    ON atendimento_periodo (inicio_em);

CREATE INDEX idx_atendimento_evento_atendimento_data
    ON atendimento_evento (atendimento_id, data_hora);
