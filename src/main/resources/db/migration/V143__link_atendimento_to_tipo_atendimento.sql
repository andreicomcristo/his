alter table atendimento
    add column if not exists tipo_atendimento_id bigint;

insert into tipo_atendimento (codigo, descricao, ordem_exibicao, ativo)
select distinct
    upper(trim(a.tipo_atendimento)) as codigo,
    initcap(replace(lower(trim(a.tipo_atendimento)), '_', ' ')) as descricao,
    999 as ordem_exibicao,
    true as ativo
from atendimento a
left join tipo_atendimento ta
    on upper(ta.codigo) = upper(trim(a.tipo_atendimento))
where a.tipo_atendimento is not null
  and trim(a.tipo_atendimento) <> ''
  and ta.id is null;

update atendimento a
set tipo_atendimento_id = ta.id
from tipo_atendimento ta
where a.tipo_atendimento_id is null
  and a.tipo_atendimento is not null
  and upper(ta.codigo) = upper(trim(a.tipo_atendimento));

do $$
begin
    if exists (
        select 1
        from atendimento
        where tipo_atendimento_id is null
    ) then
        raise exception 'Nao foi possivel mapear atendimento.tipo_atendimento para tipo_atendimento.id';
    end if;
end
$$;

alter table atendimento
    alter column tipo_atendimento_id set not null;

do $$
begin
    if not exists (
        select 1
        from pg_constraint
        where conname = 'fk_atendimento_tipo_atendimento'
    ) then
        alter table atendimento
            add constraint fk_atendimento_tipo_atendimento
                foreign key (tipo_atendimento_id) references tipo_atendimento(id);
    end if;
end
$$;

create index if not exists idx_atendimento_tipo_atendimento_id
    on atendimento (tipo_atendimento_id);
