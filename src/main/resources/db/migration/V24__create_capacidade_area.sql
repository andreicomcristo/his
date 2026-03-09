create table capacidade_area (
    id bigserial primary key,
    nome varchar(100) not null,
    descricao varchar(500),
    ativo boolean not null default true
);

create table area_capacidade (
    id bigserial primary key,
    area_id bigint not null references area(id) on delete cascade,
    capacidade_area_id bigint not null references capacidade_area(id),
    constraint uk_area_capacidade unique (area_id, capacidade_area_id)
);

create index idx_capacidade_area_nome on capacidade_area(nome);
create index idx_area_capacidade_area_id on area_capacidade(area_id);
create index idx_area_capacidade_capacidade_id on area_capacidade(capacidade_area_id);
