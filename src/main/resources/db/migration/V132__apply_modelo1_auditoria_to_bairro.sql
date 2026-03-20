alter table bairro
    add column if not exists dt_cadastro timestamp,
    add column if not exists dt_atualizacao timestamp,
    add column if not exists cadastro_user_id bigint,
    add column if not exists atualizacao_user_id bigint,
    add column if not exists cancelamento_user_id bigint;

update bairro
set dt_cadastro = coalesce(dt_cadastro, dt_cancelamento, now())
where dt_cadastro is null;

update bairro
set dt_atualizacao = coalesce(dt_atualizacao, dt_cadastro, dt_cancelamento, now())
where dt_atualizacao is null;

update bairro
set atualizacao_user_id = coalesce(atualizacao_user_id, cadastro_user_id)
where atualizacao_user_id is null;

update bairro
set cancelamento_user_id = coalesce(cancelamento_user_id, atualizacao_user_id, cadastro_user_id)
where dt_cancelamento is not null
  and cancelamento_user_id is null;

alter table bairro
    alter column dt_cadastro set not null,
    alter column dt_cadastro set default now(),
    alter column dt_atualizacao set not null,
    alter column dt_atualizacao set default now();

do $$
begin
    if not exists (select 1 from pg_constraint where conname = 'fk_bairro_cadastro_user_id') then
        alter table bairro
            add constraint fk_bairro_cadastro_user_id
                foreign key (cadastro_user_id) references usuario(id);
    end if;

    if not exists (select 1 from pg_constraint where conname = 'fk_bairro_atualizacao_user_id') then
        alter table bairro
            add constraint fk_bairro_atualizacao_user_id
                foreign key (atualizacao_user_id) references usuario(id);
    end if;

    if not exists (select 1 from pg_constraint where conname = 'fk_bairro_cancelamento_user_id') then
        alter table bairro
            add constraint fk_bairro_cancelamento_user_id
                foreign key (cancelamento_user_id) references usuario(id);
    end if;
end $$;

create index if not exists idx_bairro_cadastro_user_id
    on bairro (cadastro_user_id);

create index if not exists idx_bairro_atualizacao_user_id
    on bairro (atualizacao_user_id);

create index if not exists idx_bairro_cancelamento_user_id
    on bairro (cancelamento_user_id);
