create table area (
    id bigserial primary key,
    unidade_id bigint not null references unidade(id),
    nome varchar(150) not null,
    codigo varchar(40),
    descricao varchar(500),
    ordem_exibicao integer,
    ativo boolean not null default true
);

create index idx_area_unidade_id on area(unidade_id);
create index idx_area_nome on area(nome);
