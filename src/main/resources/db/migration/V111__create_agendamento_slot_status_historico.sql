create table if not exists agenda_especialidade_slot (
    id bigserial primary key,
    agenda_especialidade_id bigint not null,
    data_hora_inicio timestamp not null,
    data_hora_fim timestamp not null,
    status varchar(20) not null default 'LIVRE',
    criado_em timestamp not null default now(),
    atualizado_em timestamp not null default now(),
    constraint fk_agenda_slot_agenda
        foreign key (agenda_especialidade_id) references agenda_especialidade (id),
    constraint ck_agenda_slot_status
        check (upper(status) in ('LIVRE', 'OCUPADO', 'BLOQUEADO')),
    constraint ck_agenda_slot_intervalo
        check (data_hora_fim > data_hora_inicio),
    constraint uq_agenda_slot_unidade_inicio
        unique (agenda_especialidade_id, data_hora_inicio)
);

create index if not exists idx_agenda_slot_agenda
    on agenda_especialidade_slot (agenda_especialidade_id, data_hora_inicio);

alter table if exists agenda_especialidade_paciente
    add column if not exists agenda_slot_id bigint;

alter table if exists agenda_especialidade_paciente
    add column if not exists status varchar(20) not null default 'PENDENTE';

do $$
begin
    if not exists (
        select 1
        from pg_constraint
        where conname = 'fk_agenda_paciente_slot'
    ) then
        alter table agenda_especialidade_paciente
            add constraint fk_agenda_paciente_slot
                foreign key (agenda_slot_id) references agenda_especialidade_slot (id);
    end if;
end $$;

do $$
begin
    if not exists (
        select 1
        from pg_constraint
        where conname = 'ck_agenda_paciente_status'
    ) then
        alter table agenda_especialidade_paciente
            add constraint ck_agenda_paciente_status
                check (upper(status) in ('PENDENTE', 'CONFIRMADO', 'ATENDIDO', 'FALTOU', 'CANCELADO'));
    end if;
end $$;

alter table if exists agenda_especialidade_paciente
    drop constraint if exists uq_agenda_paciente_horario_unique;

alter table if exists agenda_especialidade_paciente
    drop constraint if exists uq_agenda_paciente_unique;

drop index if exists uq_agenda_paciente_slot_ativo;
create unique index uq_agenda_paciente_slot_ativo
    on agenda_especialidade_paciente (agenda_slot_id)
    where agenda_slot_id is not null and upper(status) <> 'CANCELADO';

drop index if exists uq_agenda_paciente_unico_ativo;
create unique index uq_agenda_paciente_unico_ativo
    on agenda_especialidade_paciente (agenda_especialidade_id, paciente_id)
    where upper(status) <> 'CANCELADO';

insert into agenda_especialidade_slot (
    agenda_especialidade_id,
    data_hora_inicio,
    data_hora_fim,
    status
)
select
    a.id,
    gs as data_hora_inicio,
    gs + make_interval(mins => greatest(a.intervalo_minutos, 1)) as data_hora_fim,
    'LIVRE'
from agenda_especialidade a
join lateral generate_series(
    a.data_agenda::timestamp + a.hora_inicio,
    (a.data_agenda::timestamp + a.hora_fim) - make_interval(mins => greatest(a.intervalo_minutos, 1)),
    make_interval(mins => greatest(a.intervalo_minutos, 1))
) gs on true
on conflict (agenda_especialidade_id, data_hora_inicio) do nothing;

insert into agenda_especialidade_slot (
    agenda_especialidade_id,
    data_hora_inicio,
    data_hora_fim,
    status
)
select
    ap.agenda_especialidade_id,
    a.data_agenda::timestamp + ap.hora_atendimento,
    (a.data_agenda::timestamp + ap.hora_atendimento) + make_interval(mins => greatest(a.intervalo_minutos, 1)),
    'LIVRE'
from agenda_especialidade_paciente ap
join agenda_especialidade a
    on a.id = ap.agenda_especialidade_id
left join agenda_especialidade_slot s
    on s.agenda_especialidade_id = ap.agenda_especialidade_id
   and s.data_hora_inicio = a.data_agenda::timestamp + ap.hora_atendimento
where ap.hora_atendimento is not null
  and s.id is null
on conflict (agenda_especialidade_id, data_hora_inicio) do nothing;

update agenda_especialidade_paciente ap
set agenda_slot_id = s.id
from agenda_especialidade a,
     agenda_especialidade_slot s
where a.id = ap.agenda_especialidade_id
  and s.agenda_especialidade_id = ap.agenda_especialidade_id
  and s.data_hora_inicio = a.data_agenda::timestamp + ap.hora_atendimento
  and ap.agenda_slot_id is null;

update agenda_especialidade_slot s
set status = case
    when exists (
        select 1
        from agenda_especialidade_paciente ap
        where ap.agenda_slot_id = s.id
          and upper(ap.status) <> 'CANCELADO'
    ) then 'OCUPADO'
    else 'LIVRE'
end,
    atualizado_em = now();

alter table if exists agenda_especialidade_paciente
    alter column agenda_slot_id set not null;

create table if not exists agenda_especialidade_paciente_hist (
    id bigserial primary key,
    agenda_especialidade_paciente_id bigint not null,
    agenda_origem_id bigint,
    agenda_destino_id bigint,
    slot_origem_id bigint,
    slot_destino_id bigint,
    horario_origem time,
    horario_destino time,
    status_anterior varchar(20),
    status_novo varchar(20),
    acao varchar(30) not null,
    observacao varchar(255),
    criado_em timestamp not null default now(),
    criado_por varchar(120),
    criado_por_usuario_id bigint,
    constraint fk_agenda_paciente_hist_paciente
        foreign key (agenda_especialidade_paciente_id) references agenda_especialidade_paciente (id),
    constraint fk_agenda_paciente_hist_agenda_origem
        foreign key (agenda_origem_id) references agenda_especialidade (id),
    constraint fk_agenda_paciente_hist_agenda_destino
        foreign key (agenda_destino_id) references agenda_especialidade (id),
    constraint fk_agenda_paciente_hist_slot_origem
        foreign key (slot_origem_id) references agenda_especialidade_slot (id),
    constraint fk_agenda_paciente_hist_slot_destino
        foreign key (slot_destino_id) references agenda_especialidade_slot (id),
    constraint fk_agenda_paciente_hist_criado_por_usuario
        foreign key (criado_por_usuario_id) references usuario (id)
);

create index if not exists idx_agenda_paciente_hist_paciente
    on agenda_especialidade_paciente_hist (agenda_especialidade_paciente_id, criado_em desc);
