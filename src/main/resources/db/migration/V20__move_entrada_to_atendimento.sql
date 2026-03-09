ALTER TABLE entrada
    ADD COLUMN atendimento_id BIGINT;

DO $$
DECLARE
    atendimento_row RECORD;
    novo_episodio_id BIGINT;
BEGIN
    FOR atendimento_row IN
        SELECT a.id,
               a.paciente_id,
               a.status,
               a.data_criacao
        FROM atendimento a
        WHERE a.episodio_id IS NULL
    LOOP
        INSERT INTO episodio (paciente_id, status, data_abertura, data_fechamento)
        VALUES (
            atendimento_row.paciente_id,
            CASE
                WHEN atendimento_row.status = 'FINALIZADO' THEN 'ALTA'
                WHEN atendimento_row.status = 'EVADIU' THEN 'CANCELADO'
                ELSE 'ABERTO'
            END,
            atendimento_row.data_criacao,
            CASE
                WHEN atendimento_row.status IN ('FINALIZADO', 'EVADIU') THEN atendimento_row.data_criacao
                ELSE NULL
            END
        )
        RETURNING id INTO novo_episodio_id;

        UPDATE atendimento
        SET episodio_id = novo_episodio_id
        WHERE id = atendimento_row.id;
    END LOOP;
END $$;

UPDATE entrada e
SET atendimento_id = (
    SELECT a.id
    FROM atendimento a
    WHERE a.episodio_id = e.episodio_id
    ORDER BY a.data_criacao ASC, a.id ASC
    LIMIT 1
)
WHERE e.atendimento_id IS NULL;

ALTER TABLE entrada
    ALTER COLUMN atendimento_id SET NOT NULL;

ALTER TABLE entrada
    ADD CONSTRAINT fk_entrada_atendimento
        FOREIGN KEY (atendimento_id) REFERENCES atendimento (id);

ALTER TABLE entrada
    ADD CONSTRAINT uq_entrada_atendimento UNIQUE (atendimento_id);

ALTER TABLE entrada
    DROP CONSTRAINT IF EXISTS fk_entrada_episodio;

ALTER TABLE entrada
    DROP CONSTRAINT IF EXISTS uq_entrada_episodio;

DROP INDEX IF EXISTS idx_entrada_episodio;

ALTER TABLE entrada
    DROP COLUMN episodio_id;

ALTER TABLE atendimento
    ALTER COLUMN episodio_id SET NOT NULL;
