insert into sexo (codigo, descricao, ativo)
select 'M', 'Masculino', true
where not exists (select 1 from sexo where upper(codigo) = 'M');

insert into sexo (codigo, descricao, ativo)
select 'F', 'Feminino', true
where not exists (select 1 from sexo where upper(codigo) = 'F');

insert into sexo (codigo, descricao, ativo)
select 'NI', 'Nao Informado', true
where not exists (select 1 from sexo where upper(codigo) = 'NI');
