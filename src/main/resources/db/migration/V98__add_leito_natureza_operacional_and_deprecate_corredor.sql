alter table leito
    add column if not exists natureza_operacional varchar(40);

update leito
set natureza_operacional = 'FIXO_CNES'
where natureza_operacional is null;

with enfermaria as (
    select id
    from tipo_leito
    where upper(descricao) = 'ENFERMARIA'
    limit 1
),
corredor as (
    select id
    from tipo_leito
    where upper(descricao) = 'CORREDOR'
)
update leito l
set tipo_leito_id = e.id,
    natureza_operacional = 'VIRTUAL_SUPERLOTACAO'
from enfermaria e
where l.tipo_leito_id in (select id from corredor);

update tipo_leito
set ativo = false
where upper(descricao) = 'CORREDOR';

alter table leito
    alter column natureza_operacional set not null;

alter table leito
    alter column natureza_operacional set default 'FIXO_CNES';

do
$$
begin
    if not exists (
        select 1
        from pg_constraint
        where conname = 'ck_leito_natureza_operacional'
    ) then
        alter table leito
            add constraint ck_leito_natureza_operacional
                check (natureza_operacional in ('FIXO_CNES', 'FIXO_NAO_CNES', 'VIRTUAL_SUPERLOTACAO'));
    end if;
end
$$;
