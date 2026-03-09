create table if not exists natureza_operacional_leito (
    id bigserial primary key,
    codigo varchar(40) not null,
    descricao varchar(80) not null,
    considera_taxa_nominal boolean not null default true,
    considera_taxa_operacional boolean not null default true,
    virtual_superlotacao boolean not null default false,
    ativo boolean not null default true
);

create unique index if not exists ux_natureza_operacional_leito_codigo_upper
    on natureza_operacional_leito (upper(codigo));

insert into natureza_operacional_leito (codigo, descricao, considera_taxa_nominal, considera_taxa_operacional, virtual_superlotacao, ativo)
select 'FIXO_CNES', 'FIXO CNES', true, true, false, true
where not exists (
    select 1
    from natureza_operacional_leito
    where upper(codigo) = 'FIXO_CNES'
);

insert into natureza_operacional_leito (codigo, descricao, considera_taxa_nominal, considera_taxa_operacional, virtual_superlotacao, ativo)
select 'FIXO_NAO_CNES', 'FIXO NAO CNES', true, true, false, true
where not exists (
    select 1
    from natureza_operacional_leito
    where upper(codigo) = 'FIXO_NAO_CNES'
);

insert into natureza_operacional_leito (codigo, descricao, considera_taxa_nominal, considera_taxa_operacional, virtual_superlotacao, ativo)
select 'VIRTUAL_SUPERLOTACAO', 'VIRTUAL SUPERLOTACAO', false, true, true, true
where not exists (
    select 1
    from natureza_operacional_leito
    where upper(codigo) = 'VIRTUAL_SUPERLOTACAO'
);

alter table leito
    add column if not exists natureza_operacional_id bigint;

update leito l
set natureza_operacional_id = n.id
from natureza_operacional_leito n
where l.natureza_operacional_id is null
  and upper(n.codigo) = upper(coalesce(l.natureza_operacional, 'FIXO_CNES'));

update leito
set natureza_operacional_id = (
    select id
    from natureza_operacional_leito
    where upper(codigo) = 'FIXO_CNES'
    limit 1
)
where natureza_operacional_id is null;

create index if not exists idx_leito_natureza_operacional
    on leito (natureza_operacional_id);

do
$$
begin
    if not exists (
        select 1
        from pg_constraint
        where conname = 'fk_leito_natureza_operacional'
    ) then
        alter table leito
            add constraint fk_leito_natureza_operacional
                foreign key (natureza_operacional_id) references natureza_operacional_leito (id);
    end if;
end
$$;

alter table leito
    alter column natureza_operacional_id set not null;

alter table leito
    drop constraint if exists ck_leito_natureza_operacional;

alter table leito
    drop column if exists natureza_operacional;
