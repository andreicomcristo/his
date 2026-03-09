create table if not exists classificacao_reavaliacao (
    id bigserial primary key,
    classificacao_risco_id bigint not null,
    classificacao_cor_id bigint not null,
    classificacao_sinais_vitais_id bigint,
    classificacao_oxigenacao_id bigint,
    classificacao_glicemia_id bigint,
    classificacao_antropometria_id bigint,
    classificacao_perfusao_id bigint,
    regua_dor_id bigint not null,
    glasgow_abertura_ocular_id bigint,
    glasgow_resposta_verbal_id bigint,
    glasgow_resposta_motora_id bigint,
    glasgow_resposta_pupilar_id bigint,
    glasgow_total integer,
    discriminador varchar(2000),
    observacao text,
    data_hora timestamp not null default now(),
    usuario varchar(120),
    constraint fk_classificacao_reavaliacao_classificacao_risco
        foreign key (classificacao_risco_id) references classificacao_risco (id) on delete cascade,
    constraint fk_classificacao_reavaliacao_classificacao_cor
        foreign key (classificacao_cor_id) references classificacao_cor (id),
    constraint fk_classificacao_reavaliacao_sinais_vitais
        foreign key (classificacao_sinais_vitais_id) references classificacao_sinais_vitais (id),
    constraint fk_classificacao_reavaliacao_oxigenacao
        foreign key (classificacao_oxigenacao_id) references classificacao_oxigenacao (id),
    constraint fk_classificacao_reavaliacao_glicemia
        foreign key (classificacao_glicemia_id) references classificacao_glicemia (id),
    constraint fk_classificacao_reavaliacao_antropometria
        foreign key (classificacao_antropometria_id) references classificacao_antropometria (id),
    constraint fk_classificacao_reavaliacao_perfusao
        foreign key (classificacao_perfusao_id) references classificacao_perfusao (id),
    constraint fk_classificacao_reavaliacao_regua_dor
        foreign key (regua_dor_id) references regua_dor (id),
    constraint fk_classificacao_reavaliacao_glasgow_ocular
        foreign key (glasgow_abertura_ocular_id) references glasgow_abertura_ocular (id),
    constraint fk_classificacao_reavaliacao_glasgow_verbal
        foreign key (glasgow_resposta_verbal_id) references glasgow_resposta_verbal (id),
    constraint fk_classificacao_reavaliacao_glasgow_motora
        foreign key (glasgow_resposta_motora_id) references glasgow_resposta_motora (id),
    constraint fk_classificacao_reavaliacao_glasgow_pupilar
        foreign key (glasgow_resposta_pupilar_id) references glasgow_resposta_pupilar (id),
    constraint ck_classificacao_reavaliacao_glasgow
        check (
            (
                glasgow_abertura_ocular_id is null
                and glasgow_resposta_verbal_id is null
                and glasgow_resposta_motora_id is null
                and glasgow_resposta_pupilar_id is null
                and glasgow_total is null
            )
            or (
                glasgow_abertura_ocular_id is not null
                and glasgow_resposta_verbal_id is not null
                and glasgow_resposta_motora_id is not null
                and glasgow_total between 1 and 15
            )
        )
);

create index if not exists idx_classificacao_reavaliacao_classificacao_data
    on classificacao_reavaliacao (classificacao_risco_id, data_hora desc);

create index if not exists idx_classificacao_reavaliacao_data
    on classificacao_reavaliacao (data_hora);
