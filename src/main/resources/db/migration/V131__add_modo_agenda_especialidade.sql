alter table if exists agenda_especialidade
    add column if not exists modo_agenda varchar(30);

update agenda_especialidade
set modo_agenda = case
    when coalesce(intervalo_minutos, 0) > 1 then 'HORARIO_SESSAO'
    else 'CAPACIDADE_TURNO'
end
where modo_agenda is null;

update agenda_especialidade
set modo_agenda = 'CAPACIDADE_TURNO'
where upper(coalesce(modo_agenda, '')) not in ('CAPACIDADE_TURNO', 'HORARIO_SESSAO');

alter table if exists agenda_especialidade
    alter column modo_agenda set default 'CAPACIDADE_TURNO';

alter table if exists agenda_especialidade
    alter column modo_agenda set not null;

do $$
begin
    if not exists (
        select 1
        from pg_constraint
        where conname = 'ck_agenda_especialidade_modo_agenda'
    ) then
        alter table agenda_especialidade
            add constraint ck_agenda_especialidade_modo_agenda
                check (upper(modo_agenda) in ('CAPACIDADE_TURNO', 'HORARIO_SESSAO'));
    end if;
end $$;
