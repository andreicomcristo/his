alter table entrada
    add column profissao_id bigint null;

alter table entrada
    add constraint fk_entrada_profissao
    foreign key (profissao_id) references profissao (id);
