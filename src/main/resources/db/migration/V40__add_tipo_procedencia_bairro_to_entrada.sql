alter table entrada
    add column tipo_procedencia_id bigint,
    add column bairro_id bigint;

update entrada e
set tipo_procedencia_id = p.tipo_procedencia_id
from procedencia p
where e.procedencia_id = p.id
  and e.tipo_procedencia_id is null;

alter table entrada
    add constraint fk_entrada_tipo_procedencia
    foreign key (tipo_procedencia_id) references tipo_procedencia(id);

alter table entrada
    add constraint fk_entrada_bairro
    foreign key (bairro_id) references bairro(id);

create index idx_entrada_tipo_procedencia_id on entrada(tipo_procedencia_id);
create index idx_entrada_bairro_id on entrada(bairro_id);
