update motivo_alta
set cor = '#28A745'
where upper(descricao) = 'ALTA_MEDICA'
  and (cor is null or cor = '');

update motivo_alta
set cor = '#DC3545'
where upper(descricao) = 'OBITO'
  and (cor is null or cor = '');

update motivo_alta
set cor = '#6C757D'
where upper(descricao) = 'EVASAO'
  and (cor is null or cor = '');

update motivo_alta
set cor = '#FD7E14'
where upper(descricao) = 'TRANSFERENCIA'
  and (cor is null or cor = '');
