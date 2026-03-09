ALTER TABLE atendimento
    DROP CONSTRAINT IF EXISTS ck_atendimento_status;

ALTER TABLE atendimento
    ADD CONSTRAINT ck_atendimento_status
        CHECK (status IN (
            'AGUARDANDO',
            'EM_TRIAGEM',
            'AGUARDANDO_RECEPCAO',
            'AGUARDANDO_TRIAGEM',
            'AGUARDANDO_MEDICO',
            'EM_ATENDIMENTO',
            'FINALIZADO',
            'EVADIU',
            'TRANSFERIDO'
        ));

ALTER TABLE atendimento_periodo
    DROP CONSTRAINT IF EXISTS ck_atendimento_periodo_tipo;

ALTER TABLE atendimento_periodo
    ADD CONSTRAINT ck_atendimento_periodo_tipo
        CHECK (tipo IN (
            'CHEGADA',
            'RECEPCAO',
            'AGUARDANDO_TRIAGEM',
            'TRIAGEM',
            'AGUARDANDO_MEDICO',
            'CONSULTORIO',
            'OBSERVACAO',
            'SAIDA_TEMPORARIA'
        ));
