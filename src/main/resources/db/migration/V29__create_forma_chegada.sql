create table forma_chegada (
    id bigserial primary key,
    descricao varchar(150) not null,
    ativo boolean not null default true
);

create index idx_forma_chegada_descricao on forma_chegada(descricao);
