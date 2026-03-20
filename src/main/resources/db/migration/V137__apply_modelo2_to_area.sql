do $$
begin
    if exists (
        select 1
        from information_schema.columns
        where table_name = 'area'
          and column_name = 'descricao'
    ) and exists (
        select 1
        from information_schema.columns
        where table_name = 'area'
          and column_name = 'nome'
    ) and not exists (
        select 1
        from information_schema.columns
        where table_name = 'area'
          and column_name = 'detalhamento'
    ) then
        execute 'alter table area rename column descricao to detalhamento';
    end if;
end $$;

do $$
begin
    if exists (
        select 1
        from information_schema.columns
        where table_name = 'area'
          and column_name = 'nome'
    ) and not exists (
        select 1
        from information_schema.columns
        where table_name = 'area'
          and column_name = 'descricao'
    ) then
        execute 'alter table area rename column nome to descricao';
    end if;
end $$;

alter table area
    add column if not exists dt_cadastro timestamp,
    add column if not exists dt_atualizacao timestamp,
    add column if not exists dt_cancelamento timestamp,
    add column if not exists cadastro_user_id bigint,
    add column if not exists atualizacao_user_id bigint,
    add column if not exists cancelamento_user_id bigint;

update area
set dt_cancelamento = now()
where coalesce(ativo, true) = false
  and dt_cancelamento is null;

update area
set dt_cadastro = coalesce(dt_cadastro, dt_cancelamento, now())
where dt_cadastro is null;

update area
set dt_atualizacao = coalesce(dt_atualizacao, dt_cadastro, dt_cancelamento, now())
where dt_atualizacao is null;

update area
set atualizacao_user_id = coalesce(atualizacao_user_id, cadastro_user_id)
where atualizacao_user_id is null;

update area
set cancelamento_user_id = coalesce(cancelamento_user_id, atualizacao_user_id, cadastro_user_id)
where dt_cancelamento is not null
  and cancelamento_user_id is null;

alter table area
    alter column dt_cadastro set not null,
    alter column dt_cadastro set default now(),
    alter column dt_atualizacao set not null,
    alter column dt_atualizacao set default now();

do $$
begin
    if not exists (select 1 from pg_constraint where conname = 'fk_area_cadastro_user_id') then
        alter table area
            add constraint fk_area_cadastro_user_id
                foreign key (cadastro_user_id) references usuario(id);
    end if;

    if not exists (select 1 from pg_constraint where conname = 'fk_area_atualizacao_user_id') then
        alter table area
            add constraint fk_area_atualizacao_user_id
                foreign key (atualizacao_user_id) references usuario(id);
    end if;

    if not exists (select 1 from pg_constraint where conname = 'fk_area_cancelamento_user_id') then
        alter table area
            add constraint fk_area_cancelamento_user_id
                foreign key (cancelamento_user_id) references usuario(id);
    end if;
end $$;

alter index if exists idx_area_nome
    rename to idx_area_descricao;

create index if not exists idx_area_descricao
    on area (descricao);

create index if not exists idx_area_dt_cancelamento
    on area (dt_cancelamento);

create index if not exists idx_area_cadastro_user_id
    on area (cadastro_user_id);

create index if not exists idx_area_atualizacao_user_id
    on area (atualizacao_user_id);

create index if not exists idx_area_cancelamento_user_id
    on area (cancelamento_user_id);

alter table area
    drop column if exists ativo;
