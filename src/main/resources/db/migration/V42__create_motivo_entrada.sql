create table motivo_entrada (
    id bigserial primary key,
    descricao varchar(150) not null,
    ativo boolean not null default true
);

create index idx_motivo_entrada_descricao on motivo_entrada(descricao);
