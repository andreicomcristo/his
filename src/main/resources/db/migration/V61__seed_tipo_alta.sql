insert into tipo_alta (descricao, ativo)
select 'ATENDIMENTO', true
where not exists (select 1 from tipo_alta where upper(descricao) = 'ATENDIMENTO');

insert into tipo_alta (descricao, ativo)
select 'OBSERVACAO', true
where not exists (select 1 from tipo_alta where upper(descricao) = 'OBSERVACAO');

insert into tipo_alta (descricao, ativo)
select 'INTERNACAO', true
where not exists (select 1 from tipo_alta where upper(descricao) = 'INTERNACAO');
