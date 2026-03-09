create table sexo (
    id bigserial primary key,
    codigo varchar(10) not null unique,
    descricao varchar(60) not null,
    ativo boolean not null default true
);

create index idx_sexo_codigo on sexo(codigo);
