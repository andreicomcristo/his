insert into alergia_substancia (descricao, ativo)
values ('MEDICACAO', true)
on conflict (upper(descricao)) do update set ativo = excluded.ativo;

insert into alergia_substancia (descricao, ativo)
values ('ALIMENTO', true)
on conflict (upper(descricao)) do update set ativo = excluded.ativo;

insert into alergia_substancia (descricao, ativo)
values ('OUTROS', true)
on conflict (upper(descricao)) do update set ativo = excluded.ativo;

insert into alergia_substancia (descricao, ativo)
values ('NAO RELATA ALERGIA', true)
on conflict (upper(descricao)) do update set ativo = excluded.ativo;

insert into alergia_substancia (descricao, ativo)
values ('REACAO TRANSFUSIONAL AO IMUNIZADO FENOTIPADO', true)
on conflict (upper(descricao)) do update set ativo = excluded.ativo;

insert into alergia_substancia (descricao, ativo)
values ('VACINA', true)
on conflict (upper(descricao)) do update set ativo = excluded.ativo;

insert into alergia_severidade (descricao, ativo)
values ('LEVE', true)
on conflict (upper(descricao)) do update set ativo = excluded.ativo;

insert into alergia_severidade (descricao, ativo)
values ('MODERADA', true)
on conflict (upper(descricao)) do update set ativo = excluded.ativo;

insert into alergia_severidade (descricao, ativo)
values ('GRAVE', true)
on conflict (upper(descricao)) do update set ativo = excluded.ativo;

insert into alergia_severidade (descricao, ativo)
values ('ANAFILAXIA', true)
on conflict (upper(descricao)) do update set ativo = excluded.ativo;

insert into alergia_severidade (descricao, ativo)
values ('NAO INFORMADA', true)
on conflict (upper(descricao)) do update set ativo = excluded.ativo;
