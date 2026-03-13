create table if not exists tipo_unidade (
    id bigserial primary key,
    codigo varchar(40) not null,
    descricao varchar(100) not null,
    ativo boolean not null default true,
    constraint uq_tipo_unidade_codigo unique (codigo)
);

insert into tipo_unidade (codigo, descricao, ativo)
values ('HOSPITAL_GERAL', 'HOSPITAL GERAL', true)
on conflict (codigo) do update
set descricao = excluded.descricao,
    ativo = true;

insert into tipo_unidade (codigo, descricao, ativo)
select distinct
    case
        when nullif(btrim(u.tipo_estabelecimento), '') is null then 'NAO_INFORMADO'
        else upper(regexp_replace(btrim(u.tipo_estabelecimento), '\s+', '_', 'g'))
    end as codigo,
    case
        when nullif(btrim(u.tipo_estabelecimento), '') is null then 'NAO INFORMADO'
        else upper(replace(btrim(u.tipo_estabelecimento), '_', ' '))
    end as descricao,
    true
from unidade u
on conflict (codigo) do update
set descricao = excluded.descricao,
    ativo = true;

alter table unidade
    add column if not exists tipo_unidade_id bigint;

update unidade u
set tipo_unidade_id = tu.id
from tipo_unidade tu
where tu.codigo = case
    when nullif(btrim(u.tipo_estabelecimento), '') is null then 'NAO_INFORMADO'
    else upper(regexp_replace(btrim(u.tipo_estabelecimento), '\s+', '_', 'g'))
end
and u.tipo_unidade_id is null;

with tipo_padrao as (
    select tu.id
    from tipo_unidade tu
    where tu.ativo = true
    order by tu.id
    fetch first 1 row only
)
update unidade u
set tipo_unidade_id = tp.id
from tipo_padrao tp
where u.tipo_unidade_id is null;

do $$
begin
    if exists (
        select 1
        from unidade
        where tipo_unidade_id is null
    ) then
        raise exception 'Nao foi possivel atribuir tipo_unidade_id para todas as unidades';
    end if;
end $$;

do $$
begin
    if not exists (
        select 1
        from pg_constraint
        where conname = 'fk_unidade_tipo_unidade'
    ) then
        alter table unidade
            add constraint fk_unidade_tipo_unidade
                foreign key (tipo_unidade_id) references tipo_unidade (id);
    end if;
end $$;

create index if not exists idx_unidade_tipo_unidade_id
    on unidade (tipo_unidade_id);

alter table unidade
    alter column tipo_unidade_id set not null;

alter table unidade
    drop column if exists tipo_estabelecimento;
