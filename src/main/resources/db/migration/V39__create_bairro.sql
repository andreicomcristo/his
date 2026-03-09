create table bairro (
    id bigserial primary key,
    cidade_id bigint not null,
    nome varchar(100) not null,
    ativo boolean not null default true,
    constraint fk_bairro_cidade
        foreign key (cidade_id) references cidade (id)
);

create index idx_bairro_cidade on bairro (cidade_id);
create index idx_bairro_nome on bairro (nome);
