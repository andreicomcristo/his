create table grau_parentesco (
    id bigserial primary key,
    descricao varchar(150) not null,
    ativo boolean not null default true
);

create index idx_grau_parentesco_descricao on grau_parentesco(descricao);
