alter table entrada
    add column forma_chegada_id bigint;

update entrada e
set forma_chegada_id = f.id
from forma_chegada f
where e.forma_chegada is not null
  and upper(trim(e.forma_chegada)) = upper(trim(f.descricao));

alter table entrada
    add constraint fk_entrada_forma_chegada
    foreign key (forma_chegada_id) references forma_chegada(id);

create index idx_entrada_forma_chegada_id on entrada(forma_chegada_id);

alter table entrada
    drop column forma_chegada;
