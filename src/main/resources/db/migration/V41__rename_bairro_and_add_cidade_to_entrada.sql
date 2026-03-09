alter table entrada
    rename column bairro_id to procedencia_bairro_id;

alter index if exists idx_entrada_bairro_id
    rename to idx_entrada_procedencia_bairro_id;

alter table entrada
    rename constraint fk_entrada_bairro to fk_entrada_procedencia_bairro;

alter table entrada
    add column procedencia_cidade_id bigint;

alter table entrada
    add constraint fk_entrada_procedencia_cidade
    foreign key (procedencia_cidade_id) references cidade(id);

create index idx_entrada_procedencia_cidade_id on entrada(procedencia_cidade_id);
