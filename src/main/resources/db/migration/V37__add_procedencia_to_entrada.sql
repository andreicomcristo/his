alter table entrada
    add column procedencia_id bigint;

update entrada e
set procedencia_id = p.id
from procedencia p
where e.procedencia is not null
  and upper(trim(e.procedencia)) = upper(trim(p.descricao))
  and p.unidade_id is null;

alter table entrada
    add constraint fk_entrada_procedencia
    foreign key (procedencia_id) references procedencia(id);

create index idx_entrada_procedencia_id on entrada(procedencia_id);

alter table entrada
    drop column procedencia;
