alter table unidade
    add column cidade_id bigint;

alter table unidade
    add constraint fk_unidade_cidade
    foreign key (cidade_id) references cidade(id);

create index idx_unidade_cidade_id on unidade(cidade_id);
