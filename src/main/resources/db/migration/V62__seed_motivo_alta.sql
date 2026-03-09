insert into motivo_alta (descricao, ativo)
select 'ALTA_MEDICA', true
where not exists (select 1 from motivo_alta where upper(descricao) = 'ALTA_MEDICA');

insert into motivo_alta (descricao, ativo)
select 'OBITO', true
where not exists (select 1 from motivo_alta where upper(descricao) = 'OBITO');

insert into motivo_alta (descricao, ativo)
select 'EVASAO', true
where not exists (select 1 from motivo_alta where upper(descricao) = 'EVASAO');

insert into motivo_alta (descricao, ativo)
select 'TRANSFERENCIA', true
where not exists (select 1 from motivo_alta where upper(descricao) = 'TRANSFERENCIA');
