update sexo
set descricao = 'MASCULINO'
where upper(codigo) = 'M';

update sexo
set descricao = 'FEMININO'
where upper(codigo) = 'F';

update sexo
set descricao = 'NAO INFORMADO'
where upper(codigo) = 'NI';
