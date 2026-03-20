alter table municipio
    add column if not exists dt_cadastro timestamp,
    add column if not exists dt_atualizacao timestamp,
    add column if not exists cadastro_user_id bigint,
    add column if not exists atualizacao_user_id bigint,
    add column if not exists cancelamento_user_id bigint;

update municipio
set dt_cadastro = coalesce(dt_cadastro, dt_cancelamento, now())
where dt_cadastro is null;

update municipio
set dt_atualizacao = coalesce(dt_atualizacao, dt_cadastro, dt_cancelamento, now())
where dt_atualizacao is null;

update municipio
set atualizacao_user_id = coalesce(atualizacao_user_id, cadastro_user_id)
where atualizacao_user_id is null;

update municipio
set cancelamento_user_id = coalesce(cancelamento_user_id, atualizacao_user_id, cadastro_user_id)
where dt_cancelamento is not null
  and cancelamento_user_id is null;

alter table municipio
    alter column dt_cadastro set not null,
    alter column dt_cadastro set default now(),
    alter column dt_atualizacao set not null,
    alter column dt_atualizacao set default now();

do $$
begin
    if not exists (select 1 from pg_constraint where conname = 'fk_municipio_cadastro_user_id') then
        alter table municipio
            add constraint fk_municipio_cadastro_user_id
                foreign key (cadastro_user_id) references usuario(id);
    end if;

    if not exists (select 1 from pg_constraint where conname = 'fk_municipio_atualizacao_user_id') then
        alter table municipio
            add constraint fk_municipio_atualizacao_user_id
                foreign key (atualizacao_user_id) references usuario(id);
    end if;

    if not exists (select 1 from pg_constraint where conname = 'fk_municipio_cancelamento_user_id') then
        alter table municipio
            add constraint fk_municipio_cancelamento_user_id
                foreign key (cancelamento_user_id) references usuario(id);
    end if;
end $$;

create index if not exists idx_municipio_cadastro_user_id
    on municipio (cadastro_user_id);

create index if not exists idx_municipio_atualizacao_user_id
    on municipio (atualizacao_user_id);

create index if not exists idx_municipio_cancelamento_user_id
    on municipio (cancelamento_user_id);

alter table unidade_federativa
    add column if not exists dt_cadastro timestamp,
    add column if not exists dt_atualizacao timestamp,
    add column if not exists cadastro_user_id bigint,
    add column if not exists atualizacao_user_id bigint,
    add column if not exists cancelamento_user_id bigint;

update unidade_federativa
set dt_cadastro = coalesce(dt_cadastro, dt_cancelamento, now())
where dt_cadastro is null;

update unidade_federativa
set dt_atualizacao = coalesce(dt_atualizacao, dt_cadastro, dt_cancelamento, now())
where dt_atualizacao is null;

update unidade_federativa
set atualizacao_user_id = coalesce(atualizacao_user_id, cadastro_user_id)
where atualizacao_user_id is null;

update unidade_federativa
set cancelamento_user_id = coalesce(cancelamento_user_id, atualizacao_user_id, cadastro_user_id)
where dt_cancelamento is not null
  and cancelamento_user_id is null;

alter table unidade_federativa
    alter column dt_cadastro set not null,
    alter column dt_cadastro set default now(),
    alter column dt_atualizacao set not null,
    alter column dt_atualizacao set default now();

do $$
begin
    if not exists (select 1 from pg_constraint where conname = 'fk_uf_cadastro_user_id') then
        alter table unidade_federativa
            add constraint fk_uf_cadastro_user_id
                foreign key (cadastro_user_id) references usuario(id);
    end if;

    if not exists (select 1 from pg_constraint where conname = 'fk_uf_atualizacao_user_id') then
        alter table unidade_federativa
            add constraint fk_uf_atualizacao_user_id
                foreign key (atualizacao_user_id) references usuario(id);
    end if;

    if not exists (select 1 from pg_constraint where conname = 'fk_uf_cancelamento_user_id') then
        alter table unidade_federativa
            add constraint fk_uf_cancelamento_user_id
                foreign key (cancelamento_user_id) references usuario(id);
    end if;
end $$;

create index if not exists idx_uf_cadastro_user_id
    on unidade_federativa (cadastro_user_id);

create index if not exists idx_uf_atualizacao_user_id
    on unidade_federativa (atualizacao_user_id);

create index if not exists idx_uf_cancelamento_user_id
    on unidade_federativa (cancelamento_user_id);
