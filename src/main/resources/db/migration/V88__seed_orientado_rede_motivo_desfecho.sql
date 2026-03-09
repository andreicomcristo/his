insert into motivo_desfecho (descricao, ativo)
select 'ORIENTADO_REDE', true
where not exists (
    select 1
    from motivo_desfecho
    where upper(descricao) = 'ORIENTADO_REDE'
);

update motivo_desfecho
set cor = '#17A2B8'
where upper(descricao) = 'ORIENTADO_REDE'
  and (cor is null or btrim(cor) = '');
