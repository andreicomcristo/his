do $$
begin
    if exists (
        select 1
        from information_schema.columns
        where table_schema = 'public'
          and table_name = 'entrada'
          and column_name = 'area_id'
    ) and not exists (
        select 1
        from information_schema.columns
        where table_schema = 'public'
          and table_name = 'entrada'
          and column_name = 'area_porta_entrada_id'
    ) then
        alter table entrada
            rename column area_id to area_porta_entrada_id;
    end if;
end
$$;

alter table entrada
    add column if not exists area_execucao_id bigint;

update entrada
set area_execucao_id = area_porta_entrada_id
where area_execucao_id is null
  and area_porta_entrada_id is not null;

do $$
begin
    if exists (
        select 1
        from pg_class
        where relname = 'idx_entrada_area_id'
    ) and not exists (
        select 1
        from pg_class
        where relname = 'idx_entrada_area_porta_entrada_id'
    ) then
        alter index idx_entrada_area_id
            rename to idx_entrada_area_porta_entrada_id;
    end if;
end
$$;

create index if not exists idx_entrada_area_porta_entrada_id
    on entrada (area_porta_entrada_id);

create index if not exists idx_entrada_area_execucao_id
    on entrada (area_execucao_id);

do $$
begin
    if not exists (
        select 1
        from pg_constraint c
        join pg_class t on t.oid = c.conrelid
        join unnest(c.conkey) as col(attnum) on true
        join pg_attribute a on a.attrelid = t.oid and a.attnum = col.attnum
        where t.relname = 'entrada'
          and c.contype = 'f'
          and a.attname = 'area_porta_entrada_id'
    ) then
        alter table entrada
            add constraint fk_entrada_area_porta_entrada
                foreign key (area_porta_entrada_id) references area(id);
    end if;

    if not exists (
        select 1
        from pg_constraint c
        join pg_class t on t.oid = c.conrelid
        join unnest(c.conkey) as col(attnum) on true
        join pg_attribute a on a.attrelid = t.oid and a.attnum = col.attnum
        where t.relname = 'entrada'
          and c.contype = 'f'
          and a.attname = 'area_execucao_id'
    ) then
        alter table entrada
            add constraint fk_entrada_area_execucao
                foreign key (area_execucao_id) references area(id);
    end if;
end
$$;

with unidade_treinamento as (
    select id
    from unidade
    where cnes = 'DEFAULT'
),
tipos as (
    select
        max(case when codigo = 'RECEPCAO' then id end) as tipo_recepcao_id,
        max(case when codigo = 'TRIAGEM' then id end) as tipo_triagem_id
    from tipo_area
),
areas_novas as (
    select
        u.id as unidade_id,
        t.tipo_recepcao_id as tipo_area_id,
        'RECEPCAO 2' as descricao,
        'Porta de entrada secundaria da unidade treinamento' as detalhamento
    from unidade_treinamento u
    cross join tipos t
    where t.tipo_recepcao_id is not null

    union all

    select
        u.id as unidade_id,
        t.tipo_triagem_id as tipo_area_id,
        'CLASSIFICACAO DE RISCO 2' as descricao,
        'Sala secundaria de classificacao de risco da unidade treinamento' as detalhamento
    from unidade_treinamento u
    cross join tipos t
    where t.tipo_triagem_id is not null
)
insert into area (unidade_id, descricao, detalhamento, tipo_area_id, dt_cadastro, dt_atualizacao)
select
    a.unidade_id,
    a.descricao,
    a.detalhamento,
    a.tipo_area_id,
    now(),
    now()
from areas_novas a
where not exists (
    select 1
    from area existente
    where existente.unidade_id = a.unidade_id
      and upper(existente.descricao) = upper(a.descricao)
      and existente.dt_cancelamento is null
);

insert into area_capacidade (area_id, capacidade_area_id)
select a.id, c.id
from area a
join unidade u on u.id = a.unidade_id
join capacidade_area c on upper(c.nome) = 'RECEBE_ENTRADA'
where u.cnes = 'DEFAULT'
  and a.dt_cancelamento is null
  and upper(a.descricao) = 'RECEPCAO 2'
on conflict (area_id, capacidade_area_id) do nothing;

insert into area_capacidade (area_id, capacidade_area_id)
select a.id, c.id
from area a
join unidade u on u.id = a.unidade_id
join capacidade_area c on upper(c.nome) in ('REALIZA_CLASSIFICACAO', 'SALA_CLASSIFICACAO')
where u.cnes = 'DEFAULT'
  and a.dt_cancelamento is null
  and upper(a.descricao) = 'CLASSIFICACAO DE RISCO 2'
on conflict (area_id, capacidade_area_id) do nothing;
