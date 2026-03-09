ALTER TABLE atendimento
    ADD COLUMN episodio_id BIGINT;

ALTER TABLE atendimento
    ADD CONSTRAINT fk_atendimento_episodio
        FOREIGN KEY (episodio_id) REFERENCES episodio (id);

CREATE INDEX idx_atendimento_episodio
    ON atendimento (episodio_id);

UPDATE atendimento a
SET episodio_id = e.id
FROM episodio e
WHERE e.atendimento_id = a.id
  AND a.episodio_id IS NULL;

ALTER TABLE atendimento
    DROP CONSTRAINT ck_atendimento_status;

ALTER TABLE atendimento
    ADD CONSTRAINT ck_atendimento_status
        CHECK (status IN ('AGUARDANDO', 'EM_TRIAGEM', 'AGUARDANDO_RECEPCAO', 'AGUARDANDO_MEDICO', 'EM_ATENDIMENTO', 'FINALIZADO', 'EVADIU', 'TRANSFERIDO'));

ALTER TABLE atendimento_evento
    DROP CONSTRAINT ck_atendimento_evento_tipo;

ALTER TABLE atendimento_evento
    ADD CONSTRAINT ck_atendimento_evento_tipo
        CHECK (tipo IN ('ATENDIMENTO_CRIADO', 'TRIAGEM_FINALIZADA', 'EPISODIO_ABERTO', 'ENTRADA_REGISTRADA', 'FINALIZADO', 'TRANSFERENCIA_SOLICITADA', 'TRANSFERENCIA_SAIDA', 'TRANSFERENCIA_RECEBIDA', 'TRANSFERENCIA_CANCELADA'));

CREATE TABLE status_transferencia_externa (
    id BIGSERIAL PRIMARY KEY,
    codigo VARCHAR(40) NOT NULL UNIQUE,
    nome VARCHAR(120) NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE
);

INSERT INTO status_transferencia_externa (codigo, nome, ativo)
VALUES ('SOLICITADA', 'Solicitada', TRUE),
       ('EM_TRANSPORTE', 'Em transporte', TRUE),
       ('RECEBIDA', 'Recebida', TRUE),
       ('CANCELADA', 'Cancelada', TRUE);

CREATE TABLE transferencia_externa (
    id BIGSERIAL PRIMARY KEY,
    episodio_id BIGINT NOT NULL,
    atendimento_origem_id BIGINT NOT NULL,
    atendimento_destino_id BIGINT,
    unidade_origem_id BIGINT NOT NULL,
    unidade_destino_id BIGINT NOT NULL,
    status_id BIGINT NOT NULL,
    motivo VARCHAR(255) NOT NULL,
    observacao TEXT,
    data_solicitacao TIMESTAMP NOT NULL,
    data_saida TIMESTAMP,
    data_chegada TIMESTAMP,
    usuario_solicitacao VARCHAR(120),
    usuario_saida VARCHAR(120),
    usuario_acolhimento VARCHAR(120),
    CONSTRAINT fk_transferencia_externa_episodio
        FOREIGN KEY (episodio_id) REFERENCES episodio (id),
    CONSTRAINT fk_transferencia_externa_atendimento_origem
        FOREIGN KEY (atendimento_origem_id) REFERENCES atendimento (id),
    CONSTRAINT fk_transferencia_externa_atendimento_destino
        FOREIGN KEY (atendimento_destino_id) REFERENCES atendimento (id),
    CONSTRAINT fk_transferencia_externa_unidade_origem
        FOREIGN KEY (unidade_origem_id) REFERENCES unidade (id),
    CONSTRAINT fk_transferencia_externa_unidade_destino
        FOREIGN KEY (unidade_destino_id) REFERENCES unidade (id),
    CONSTRAINT fk_transferencia_externa_status
        FOREIGN KEY (status_id) REFERENCES status_transferencia_externa (id),
    CONSTRAINT ck_transferencia_externa_unidades
        CHECK (unidade_origem_id <> unidade_destino_id)
);

CREATE INDEX idx_transferencia_externa_destino_status
    ON transferencia_externa (unidade_destino_id, status_id, data_solicitacao);

CREATE INDEX idx_transferencia_externa_origem_status
    ON transferencia_externa (unidade_origem_id, status_id, data_solicitacao);

CREATE INDEX idx_transferencia_externa_episodio
    ON transferencia_externa (episodio_id);

