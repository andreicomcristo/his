with areas_com_leito as (
    select distinct
        a.id as area_id,
        a.unidade_id,
        upper(a.nome) as area_nome
    from area a
    join area_capacidade ac on ac.area_id = a.id
    join capacidade_area ca on ca.id = ac.capacidade_area_id
    where a.ativo = true
      and ca.ativo = true
      and upper(ca.nome) = 'POSSUI_LEITO'
),
config as (
    select
        a.area_id,
        a.unidade_id,
        case
            when a.area_nome like 'UTI %' then 5
            when a.area_nome like 'ALA AMARELA - OBSERVACAO %' then 6
            when a.area_nome in ('ALA A', 'ALA B', 'ALA C', 'AREA LARANJA') then 8
            else 4
        end as qtd_leitos,
        case
            when a.area_nome like 'UTI %' then 'UTI_GERAL'
            when a.area_nome like 'ALA AMARELA - OBSERVACAO %' then 'OBSERVACAO'
            else 'ENFERMARIA'
        end as tipo_nome,
        case
            when a.area_nome like 'ALA AMARELA - OBSERVACAO %' then null
            else 'CLINICO'
        end as perfil_nome,
        case
            when a.area_nome like 'ALA AMARELA - OBSERVACAO %' then true
            else false
        end as recebe_ps
    from areas_com_leito a
),
delete_auto as (
    delete from leito l
    using config c
    where l.area_id = c.area_id
      and upper(l.codigo) similar to ('A' || c.area_id::text || '-L[0-9]{2}')
    returning l.id
),
seed as (
    select
        c.unidade_id,
        c.area_id,
        t.id as tipo_leito_id,
        p.id as perfil_leito_id,
        ('A' || c.area_id || '-L' || lpad(gs::text, 2, '0')) as codigo,
        ('LEITO ' || lpad(gs::text, 2, '0')) as descricao,
        c.recebe_ps
    from config c
    join tipo_leito t on upper(t.descricao) = c.tipo_nome
    left join perfil_leito p on c.perfil_nome is not null and upper(p.descricao) = c.perfil_nome
    cross join lateral generate_series(1, c.qtd_leitos) gs
)
insert into leito (
    unidade_id,
    area_id,
    tipo_leito_id,
    perfil_leito_id,
    codigo,
    descricao,
    recebe_ps,
    assistencial,
    ativo
)
select
    s.unidade_id,
    s.area_id,
    s.tipo_leito_id,
    s.perfil_leito_id,
    s.codigo,
    s.descricao,
    s.recebe_ps,
    true,
    true
from seed s
where not exists (
    select 1
    from leito l
    where l.unidade_id = s.unidade_id
      and upper(l.codigo) = upper(s.codigo)
);
