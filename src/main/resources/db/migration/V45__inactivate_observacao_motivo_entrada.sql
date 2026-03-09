update motivo_entrada
set ativo = false
where upper(trim(descricao)) = 'OBSERVACAO';
