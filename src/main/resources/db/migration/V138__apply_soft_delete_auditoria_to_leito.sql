alter table leito
    add column if not exists dt_cadastro timestamp,
    add column if not exists dt_atualizacao timestamp,
    add column if not exists dt_cancelamento timestamp,
    add column if not exists cadastro_user_id bigint,
    add column if not exists atualizacao_user_id bigint,
    add column if not exists cancelamento_user_id bigint;

update leito
set dt_cancelamento = now()
where coalesce(ativo, true) = false
  and dt_cancelamento is null;

update leito
set dt_cadastro = coalesce(dt_cadastro, dt_cancelamento, now())
where dt_cadastro is null;

update leito
set dt_atualizacao = coalesce(dt_atualizacao, dt_cadastro, dt_cancelamento, now())
where dt_atualizacao is null;

update leito
set atualizacao_user_id = coalesce(atualizacao_user_id, cadastro_user_id)
where atualizacao_user_id is null;

update leito
set cancelamento_user_id = coalesce(cancelamento_user_id, atualizacao_user_id, cadastro_user_id)
where dt_cancelamento is not null
  and cancelamento_user_id is null;

alter table leito
    alter column dt_cadastro set not null,
    alter column dt_cadastro set default now(),
    alter column dt_atualizacao set not null,
    alter column dt_atualizacao set default now();

do $$
begin
    if not exists (select 1 from pg_constraint where conname = 'fk_leito_cadastro_user_id') then
        alter table leito
            add constraint fk_leito_cadastro_user_id
                foreign key (cadastro_user_id) references usuario(id);
    end if;

    if not exists (select 1 from pg_constraint where conname = 'fk_leito_atualizacao_user_id') then
        alter table leito
            add constraint fk_leito_atualizacao_user_id
                foreign key (atualizacao_user_id) references usuario(id);
    end if;

    if not exists (select 1 from pg_constraint where conname = 'fk_leito_cancelamento_user_id') then
        alter table leito
            add constraint fk_leito_cancelamento_user_id
                foreign key (cancelamento_user_id) references usuario(id);
    end if;
end $$;

create index if not exists idx_leito_dt_cancelamento
    on leito (dt_cancelamento);

create index if not exists idx_leito_cadastro_user_id
    on leito (cadastro_user_id);

create index if not exists idx_leito_atualizacao_user_id
    on leito (atualizacao_user_id);

create index if not exists idx_leito_cancelamento_user_id
    on leito (cancelamento_user_id);

alter table leito
    drop column if exists ativo;
