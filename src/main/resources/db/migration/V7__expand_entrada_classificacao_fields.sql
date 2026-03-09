ALTER TABLE classificacao_risco
    ADD COLUMN discriminador VARCHAR(2000),
    ADD COLUMN medicacoes_uso_continuo VARCHAR(500),
    ADD COLUMN frequencia_respiratoria INTEGER,
    ADD COLUMN saturacao_o2_com_terapia_o2 INTEGER,
    ADD COLUMN saturacao_o2_aa INTEGER,
    ADD COLUMN glicemia_capilar NUMERIC(6,2),
    ADD COLUMN peso_kg NUMERIC(6,2),
    ADD COLUMN altura_cm NUMERIC(6,2),
    ADD COLUMN hgt INTEGER,
    ADD COLUMN perfusao_capilar_periferica_seg INTEGER,
    ADD COLUMN preenchimento_capilar_central_seg INTEGER,
    ADD COLUMN observacao_fast VARCHAR(500),
    ADD COLUMN observacao TEXT;

ALTER TABLE entrada
    ADD COLUMN data_hora_entrada TIMESTAMP,
    ADD COLUMN forma_chegada VARCHAR(120),
    ADD COLUMN motivo_entrada VARCHAR(150),
    ADD COLUMN observacoes VARCHAR(500),
    ADD COLUMN informacao_ad_chegada VARCHAR(500),
    ADD COLUMN procedencia_observacao VARCHAR(200),
    ADD COLUMN telefone_acompanhante VARCHAR(30),
    ADD COLUMN situacao_ocupacional VARCHAR(120),
    ADD COLUMN profissao_observacao VARCHAR(150),
    ADD COLUMN tempo_servico VARCHAR(120);

UPDATE entrada
SET data_hora_entrada = COALESCE(data_hora_entrada, atualizado_em)
WHERE data_hora_entrada IS NULL;

ALTER TABLE entrada
    ALTER COLUMN data_hora_entrada SET NOT NULL;
