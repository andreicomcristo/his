ALTER TABLE atendimento_periodo
    DROP CONSTRAINT IF EXISTS ck_atendimento_periodo_tipo;

ALTER TABLE atendimento_periodo
    ADD CONSTRAINT ck_atendimento_periodo_tipo
        CHECK (tipo IN (
            'CHEGADA',
            'RECEPCAO',
            'AGUARDANDO_TRIAGEM',
            'TRIAGEM',
            'AGUARDANDO_RECEPCAO',
            'AGUARDANDO_MEDICO',
            'CONSULTORIO',
            'OBSERVACAO',
            'SAIDA_TEMPORARIA'
        ));
