alter table if exists agenda_especialidade
    add column if not exists intervalo_minutos integer not null default 15;

do $$
begin
    if not exists (
        select 1
        from pg_constraint
        where conname = 'ck_agenda_especialidade_intervalo_minutos'
    ) then
        alter table agenda_especialidade
            add constraint ck_agenda_especialidade_intervalo_minutos
                check (intervalo_minutos > 0 and intervalo_minutos <= 240);
    end if;
end $$;

alter table if exists agenda_especialidade_paciente
    add column if not exists hora_atendimento time;

with horarios as (
    select ap.id as agenda_paciente_id,
           (a.hora_inicio + ((row_number() over (partition by ap.agenda_especialidade_id order by ap.id) - 1)
               * make_interval(mins => greatest(a.intervalo_minutos, 1))))::time as hora_calculada
    from agenda_especialidade_paciente ap
    join agenda_especialidade a on a.id = ap.agenda_especialidade_id
    where ap.hora_atendimento is null
)
update agenda_especialidade_paciente ap
set hora_atendimento = h.hora_calculada
from horarios h
where h.agenda_paciente_id = ap.id
  and ap.hora_atendimento is null;

update agenda_especialidade_paciente ap
set hora_atendimento = a.hora_inicio
from agenda_especialidade a
where ap.agenda_especialidade_id = a.id
  and ap.hora_atendimento is null;

alter table if exists agenda_especialidade_paciente
    alter column hora_atendimento set not null;

do $$
begin
    if not exists (
        select 1
        from pg_constraint
        where conname = 'uq_agenda_paciente_horario_unique'
    ) then
        alter table agenda_especialidade_paciente
            add constraint uq_agenda_paciente_horario_unique
                unique (agenda_especialidade_id, hora_atendimento);
    end if;
end $$;
