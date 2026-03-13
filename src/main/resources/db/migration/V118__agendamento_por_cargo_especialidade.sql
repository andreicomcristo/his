alter table cargo_colaborador
    add column if not exists exige_especialidade_agendamento boolean not null default false;

update cargo_colaborador
set exige_especialidade_agendamento = true
where upper(codigo) = 'MEDICO';

create table if not exists cargo_colaborador_especialidade (
    id bigserial primary key,
    cargo_colaborador_id bigint not null,
    especialidade_id bigint not null,
    ativo boolean not null default true,
    constraint fk_cargo_colab_esp_cargo
        foreign key (cargo_colaborador_id) references cargo_colaborador (id),
    constraint fk_cargo_colab_esp_especialidade
        foreign key (especialidade_id) references especialidade (id),
    constraint uq_cargo_colab_esp unique (cargo_colaborador_id, especialidade_id)
);

create index if not exists idx_cargo_colab_esp_cargo
    on cargo_colaborador_especialidade (cargo_colaborador_id);

create index if not exists idx_cargo_colab_esp_especialidade
    on cargo_colaborador_especialidade (especialidade_id);

insert into cargo_colaborador_especialidade (cargo_colaborador_id, especialidade_id, ativo)
select c.id, e.id, true
from cargo_colaborador c
cross join especialidade e
where upper(c.codigo) = 'MEDICO'
on conflict (cargo_colaborador_id, especialidade_id) do update
set ativo = excluded.ativo;

alter table agenda_especialidade
    add column if not exists cargo_colaborador_id bigint;

with cargo_medico as (
    select id
    from cargo_colaborador
    where upper(codigo) = 'MEDICO'
    fetch first 1 row only
)
update agenda_especialidade a
set cargo_colaborador_id = cm.id
from cargo_medico cm
where a.cargo_colaborador_id is null;

with cargo_assistencial as (
    select c.id
    from cargo_colaborador c
    join tipo_cargo tc on tc.id = c.tipo_cargo_id
    where c.ativo = true
      and tc.ativo = true
      and upper(tc.codigo) = 'ASSISTENCIAL'
    order by c.id
    fetch first 1 row only
)
update agenda_especialidade a
set cargo_colaborador_id = ca.id
from cargo_assistencial ca
where a.cargo_colaborador_id is null;

do $$
begin
    if exists (
        select 1
        from agenda_especialidade
        where cargo_colaborador_id is null
    ) then
        raise exception 'Nao foi possivel definir cargo_colaborador_id para todas as agendas existentes';
    end if;
end $$;

alter table agenda_especialidade
    alter column cargo_colaborador_id set not null;

alter table agenda_especialidade
    alter column especialidade_id drop not null;

do $$
begin
    if not exists (
        select 1
        from pg_constraint
        where conname = 'fk_agenda_especialidade_cargo_colaborador'
    ) then
        alter table agenda_especialidade
            add constraint fk_agenda_especialidade_cargo_colaborador
                foreign key (cargo_colaborador_id) references cargo_colaborador (id);
    end if;
end $$;

alter table agenda_especialidade
    drop constraint if exists uq_agenda_especialidade_unidade_esp_data_hora;

create unique index if not exists uq_agenda_especialidade_contexto_horario
    on agenda_especialidade (
        unidade_id,
        cargo_colaborador_id,
        coalesce(especialidade_id, 0),
        data_agenda,
        hora_inicio,
        hora_fim
    );

create index if not exists idx_agenda_especialidade_unidade_cargo_data
    on agenda_especialidade (unidade_id, cargo_colaborador_id, data_agenda);
