create table if not exists glasgow_resposta_pupilar (
    id bigserial primary key,
    pontos integer not null,
    descricao varchar(120) not null,
    ativo boolean not null default true,
    constraint ck_glasgow_resposta_pupilar_pontos check (pontos between 0 and 2)
);

create unique index if not exists uk_glasgow_resposta_pupilar_pontos
    on glasgow_resposta_pupilar (pontos);

create unique index if not exists uk_glasgow_resposta_pupilar_descricao
    on glasgow_resposta_pupilar (upper(descricao));

alter table classificacao_risco
    add column if not exists regua_dor_id bigint;

do $$
begin
    if not exists (
        select 1
        from pg_constraint
        where conname = 'fk_classificacao_risco_regua_dor'
    ) then
        alter table classificacao_risco
            add constraint fk_classificacao_risco_regua_dor
                foreign key (regua_dor_id) references regua_dor (id);
    end if;
end $$;

create table if not exists classificacao_glasgow (
    id bigserial primary key,
    classificacao_risco_id bigint not null,
    glasgow_abertura_ocular_id bigint not null,
    glasgow_resposta_verbal_id bigint not null,
    glasgow_resposta_motora_id bigint not null,
    glasgow_resposta_pupilar_id bigint,
    total integer not null,
    data_hora timestamp not null default now(),
    constraint uk_classificacao_glasgow_classificacao unique (classificacao_risco_id),
    constraint ck_classificacao_glasgow_total check (total between 1 and 15),
    constraint fk_classificacao_glasgow_classificacao foreign key (classificacao_risco_id) references classificacao_risco (id) on delete cascade,
    constraint fk_classificacao_glasgow_ocular foreign key (glasgow_abertura_ocular_id) references glasgow_abertura_ocular (id),
    constraint fk_classificacao_glasgow_verbal foreign key (glasgow_resposta_verbal_id) references glasgow_resposta_verbal (id),
    constraint fk_classificacao_glasgow_motora foreign key (glasgow_resposta_motora_id) references glasgow_resposta_motora (id),
    constraint fk_classificacao_glasgow_pupilar foreign key (glasgow_resposta_pupilar_id) references glasgow_resposta_pupilar (id)
);
