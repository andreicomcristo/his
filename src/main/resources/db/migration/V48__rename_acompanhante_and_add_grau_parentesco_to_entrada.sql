alter table entrada
    rename column acompanhante to comunicante;

alter table entrada
    rename column telefone_acompanhante to telefone_comunicante;

alter table entrada
    add column grau_parentesco_id bigint;

alter table entrada
    add constraint fk_entrada_grau_parentesco
    foreign key (grau_parentesco_id) references grau_parentesco(id);

create index idx_entrada_grau_parentesco_id on entrada(grau_parentesco_id);
