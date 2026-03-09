alter table entrada
    add column motivo_entrada_id bigint;

update entrada e
set motivo_entrada_id = m.id
from motivo_entrada m
where e.motivo_entrada is not null
  and upper(trim(e.motivo_entrada)) = upper(trim(m.descricao))
  and e.motivo_entrada_id is null;

alter table entrada
    add constraint fk_entrada_motivo_entrada
    foreign key (motivo_entrada_id) references motivo_entrada(id);

create index idx_entrada_motivo_entrada_id on entrada(motivo_entrada_id);

alter table entrada
    drop column motivo_entrada;
