ALTER TABLE episodio
    DROP CONSTRAINT IF EXISTS uq_episodio_atendimento;

ALTER TABLE episodio
    DROP CONSTRAINT IF EXISTS fk_episodio_atendimento;

ALTER TABLE episodio
    DROP COLUMN IF EXISTS atendimento_id;
