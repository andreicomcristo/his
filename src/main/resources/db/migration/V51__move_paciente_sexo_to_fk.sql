alter table paciente
    add column sexo_id bigint;

update paciente p
set sexo_id = s.id
from sexo s
where upper(coalesce(trim(p.sexo), 'NI')) = upper(s.codigo)
  and p.sexo_id is null;

alter table paciente
    add constraint fk_paciente_sexo
    foreign key (sexo_id) references sexo(id);

create index idx_paciente_sexo_id on paciente(sexo_id);

alter table paciente
    drop column sexo;
