create table situacao_ocupacional (
    id bigserial primary key,
    descricao varchar(150) not null,
    ativo boolean not null default true
);

create index idx_situacao_ocupacional_descricao on situacao_ocupacional(descricao);
