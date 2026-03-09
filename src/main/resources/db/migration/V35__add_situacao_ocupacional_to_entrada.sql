alter table entrada
    add column situacao_ocupacional_id bigint;

update entrada e
set situacao_ocupacional_id = s.id
from situacao_ocupacional s
where e.situacao_ocupacional is not null
  and upper(trim(e.situacao_ocupacional)) = upper(trim(s.descricao));

alter table entrada
    add constraint fk_entrada_situacao_ocupacional
    foreign key (situacao_ocupacional_id) references situacao_ocupacional(id);

create index idx_entrada_situacao_ocupacional_id on entrada(situacao_ocupacional_id);

alter table entrada
    drop column situacao_ocupacional;
