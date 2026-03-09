ALTER TABLE atendimento
    ADD COLUMN IF NOT EXISTS status_id BIGINT;

UPDATE atendimento a
SET status_id = sa.id
FROM status_atendimento sa
WHERE a.status_id IS NULL
  AND UPPER(sa.codigo) = UPPER(a.status);

ALTER TABLE atendimento
    DROP CONSTRAINT IF EXISTS ck_atendimento_status;

DROP INDEX IF EXISTS idx_atendimento_status;

ALTER TABLE atendimento
    ALTER COLUMN status_id SET NOT NULL;

ALTER TABLE atendimento
    ADD CONSTRAINT fk_atendimento_status
    FOREIGN KEY (status_id) REFERENCES status_atendimento (id);

CREATE INDEX IF NOT EXISTS idx_atendimento_status_id
    ON atendimento (status_id);

ALTER TABLE atendimento
    DROP COLUMN IF EXISTS status;
