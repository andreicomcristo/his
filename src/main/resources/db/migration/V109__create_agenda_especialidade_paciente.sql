create table if not exists agenda_especialidade_paciente (
    id bigserial primary key,
    agenda_especialidade_id bigint not null,
    paciente_id bigint not null,
    tipo_vaga varchar(20) not null default 'NORMAL',
    observacao varchar(255),
    criado_em timestamp not null default now(),
    criado_por varchar(120),
    criado_por_usuario_id bigint,
    constraint fk_agenda_paciente_agenda
        foreign key (agenda_especialidade_id) references agenda_especialidade (id),
    constraint fk_agenda_paciente_paciente
        foreign key (paciente_id) references paciente (id),
    constraint fk_agenda_paciente_criado_por_usuario
        foreign key (criado_por_usuario_id) references usuario (id),
    constraint ck_agenda_paciente_tipo_vaga
        check (upper(tipo_vaga) in ('NORMAL', 'RETORNO')),
    constraint uq_agenda_paciente_unique
        unique (agenda_especialidade_id, paciente_id)
);

create index if not exists idx_agenda_paciente_agenda
    on agenda_especialidade_paciente (agenda_especialidade_id);

create index if not exists idx_agenda_paciente_paciente
    on agenda_especialidade_paciente (paciente_id);
