alter table if exists unidade_config_fluxo
    add column if not exists permite_agendamento boolean not null default false;

update unidade_config_fluxo ucf
set permite_agendamento = true
from unidade u
where u.id = ucf.unidade_id
  and upper(coalesce(u.cnes, '')) = 'DEFAULT';

create table if not exists especialidade (
    id bigserial primary key,
    codigo varchar(80) not null,
    descricao varchar(120) not null,
    ativo boolean not null default true
);

create unique index if not exists uq_especialidade_codigo_upper
    on especialidade (upper(codigo));

create table if not exists agenda_especialidade (
    id bigserial primary key,
    unidade_id bigint not null,
    especialidade_id bigint not null,
    data_agenda date not null,
    hora_inicio time not null,
    hora_fim time not null,
    vagas_totais integer not null,
    vagas_retorno integer not null default 0,
    observacao varchar(255),
    ativo boolean not null default true,
    criado_em timestamp not null default now(),
    criado_por varchar(120),
    criado_por_usuario_id bigint,
    atualizado_em timestamp not null default now(),
    atualizado_por varchar(120),
    atualizado_por_usuario_id bigint,
    constraint fk_agenda_especialidade_unidade
        foreign key (unidade_id) references unidade (id),
    constraint fk_agenda_especialidade_especialidade
        foreign key (especialidade_id) references especialidade (id),
    constraint fk_agenda_especialidade_criado_por_usuario
        foreign key (criado_por_usuario_id) references usuario (id),
    constraint fk_agenda_especialidade_atualizado_por_usuario
        foreign key (atualizado_por_usuario_id) references usuario (id),
    constraint ck_agenda_especialidade_intervalo
        check (hora_fim > hora_inicio),
    constraint ck_agenda_especialidade_vagas_totais
        check (vagas_totais > 0),
    constraint ck_agenda_especialidade_vagas_retorno
        check (vagas_retorno >= 0 and vagas_retorno <= vagas_totais),
    constraint uq_agenda_especialidade_unidade_esp_data_hora
        unique (unidade_id, especialidade_id, data_agenda, hora_inicio, hora_fim)
);

create index if not exists idx_agenda_especialidade_unidade_data
    on agenda_especialidade (unidade_id, data_agenda);

create index if not exists idx_agenda_especialidade_esp_data
    on agenda_especialidade (especialidade_id, data_agenda);
