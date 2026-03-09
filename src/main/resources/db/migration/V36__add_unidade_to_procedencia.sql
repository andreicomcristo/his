alter table procedencia
    add column unidade_id bigint;

alter table procedencia
    add constraint fk_procedencia_unidade
    foreign key (unidade_id) references unidade(id);

create index idx_procedencia_unidade on procedencia(unidade_id);
