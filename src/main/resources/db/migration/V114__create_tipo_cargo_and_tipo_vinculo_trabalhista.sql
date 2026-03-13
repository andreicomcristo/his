create table if not exists tipo_cargo (
    id bigserial primary key,
    codigo varchar(40) not null,
    descricao varchar(100) not null,
    ativo boolean not null default true
);

create unique index if not exists uq_tipo_cargo_codigo_upper
    on tipo_cargo (upper(codigo));

insert into tipo_cargo (codigo, descricao, ativo)
values
    ('ASSISTENCIAL', 'ASSISTENCIAL', true),
    ('ADMINISTRATIVO', 'ADMINISTRATIVO', true)
on conflict do nothing;

alter table cargo_colaborador
    add column if not exists tipo_cargo_id bigint;

update cargo_colaborador cc
set tipo_cargo_id = tc.id
from tipo_cargo tc
where cc.tipo_cargo_id is null
  and upper(tc.codigo) = upper(cc.tipo_cargo);

do $$
declare
    v_admin_id bigint;
begin
    select id into v_admin_id
    from tipo_cargo
    where upper(codigo) = 'ADMINISTRATIVO'
    fetch first 1 row only;

    if v_admin_id is not null then
        update cargo_colaborador
        set tipo_cargo_id = v_admin_id
        where tipo_cargo_id is null;
    end if;
end $$;

alter table cargo_colaborador
    alter column tipo_cargo_id set not null;

do $$
begin
    if not exists (
        select 1
        from pg_constraint
        where conname = 'fk_cargo_colaborador_tipo_cargo_id'
    ) then
        alter table cargo_colaborador
            add constraint fk_cargo_colaborador_tipo_cargo_id
                foreign key (tipo_cargo_id) references tipo_cargo (id);
    end if;
end $$;

alter table cargo_colaborador
    drop constraint if exists ck_cargo_colaborador_tipo;

alter table cargo_colaborador
    drop column if exists tipo_cargo;

create table if not exists tipo_vinculo_trabalhista (
    id bigserial primary key,
    codigo varchar(60) not null,
    descricao varchar(120) not null,
    ativo boolean not null default true
);

create unique index if not exists uq_tipo_vinculo_trabalhista_codigo_upper
    on tipo_vinculo_trabalhista (upper(codigo));

insert into tipo_vinculo_trabalhista (codigo, descricao, ativo)
values
    ('CLT', 'CLT', true),
    ('EFETIVO', 'EFETIVO', true),
    ('TERCEIRIZADO', 'TERCEIRIZADO', true)
on conflict do nothing;

alter table colaborador_unidade_vinculo
    add column if not exists tipo_vinculo_trabalhista_id bigint;

insert into tipo_vinculo_trabalhista (codigo, descricao, ativo)
select distinct
    regexp_replace(upper(btrim(cuv.tipo_vinculo_trabalhista)), '[^A-Z0-9]+', '_', 'g') as codigo,
    upper(btrim(cuv.tipo_vinculo_trabalhista)) as descricao,
    true
from colaborador_unidade_vinculo cuv
where cuv.tipo_vinculo_trabalhista is not null
  and btrim(cuv.tipo_vinculo_trabalhista) <> ''
on conflict do nothing;

update colaborador_unidade_vinculo cuv
set tipo_vinculo_trabalhista_id = tvt.id
from tipo_vinculo_trabalhista tvt
where cuv.tipo_vinculo_trabalhista_id is null
  and cuv.tipo_vinculo_trabalhista is not null
  and btrim(cuv.tipo_vinculo_trabalhista) <> ''
  and (
        upper(tvt.codigo) = regexp_replace(upper(btrim(cuv.tipo_vinculo_trabalhista)), '[^A-Z0-9]+', '_', 'g')
        or upper(tvt.descricao) = upper(btrim(cuv.tipo_vinculo_trabalhista))
      );

do $$
begin
    if not exists (
        select 1
        from pg_constraint
        where conname = 'fk_colaborador_unidade_vinculo_tipo_vinculo_trabalhista_id'
    ) then
        alter table colaborador_unidade_vinculo
            add constraint fk_colaborador_unidade_vinculo_tipo_vinculo_trabalhista_id
                foreign key (tipo_vinculo_trabalhista_id)
                references tipo_vinculo_trabalhista (id);
    end if;
end $$;

alter table colaborador_unidade_vinculo
    drop column if exists tipo_vinculo_trabalhista;
