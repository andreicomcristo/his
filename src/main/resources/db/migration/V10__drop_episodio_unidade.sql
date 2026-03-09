DROP INDEX IF EXISTS idx_episodio_unidade_status;

ALTER TABLE episodio
    DROP CONSTRAINT IF EXISTS fk_episodio_unidade;

ALTER TABLE episodio
    DROP COLUMN IF EXISTS unidade_id;

