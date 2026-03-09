create table if not exists status_atendimento (
    id bigserial primary key,
    codigo varchar(80) not null,
    descricao varchar(120) not null,
    ativo boolean not null default true
);

create unique index if not exists uk_status_atendimento_codigo
    on status_atendimento (upper(codigo));

create unique index if not exists uk_status_atendimento_descricao
    on status_atendimento (upper(descricao));
