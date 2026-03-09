create table if not exists tipo_alta (
    id bigserial primary key,
    descricao varchar(80) not null,
    ativo boolean not null default true
);

create table if not exists motivo_alta (
    id bigserial primary key,
    descricao varchar(100) not null,
    ativo boolean not null default true
);

create table if not exists alta (
    id bigserial primary key,
    atendimento_id bigint not null unique,
    tipo_alta_id bigint not null,
    motivo_alta_id bigint not null,
    data_hora timestamp not null,
    observacao text,
    constraint fk_alta_atendimento foreign key (atendimento_id) references atendimento (id),
    constraint fk_alta_tipo_alta foreign key (tipo_alta_id) references tipo_alta (id),
    constraint fk_alta_motivo_alta foreign key (motivo_alta_id) references motivo_alta (id)
);
