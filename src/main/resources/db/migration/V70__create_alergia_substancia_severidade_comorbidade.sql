create table if not exists alergia_substancia (
    id bigserial primary key,
    descricao varchar(180) not null,
    ativo boolean not null default true
);

create unique index if not exists uk_alergia_substancia_descricao
    on alergia_substancia (upper(descricao));

create table if not exists alergia_severidade (
    id bigserial primary key,
    descricao varchar(120) not null,
    ativo boolean not null default true
);

create unique index if not exists uk_alergia_severidade_descricao
    on alergia_severidade (upper(descricao));

create table if not exists comorbidade (
    id bigserial primary key,
    descricao varchar(180) not null,
    ativo boolean not null default true
);

create unique index if not exists uk_comorbidade_descricao
    on comorbidade (upper(descricao));
