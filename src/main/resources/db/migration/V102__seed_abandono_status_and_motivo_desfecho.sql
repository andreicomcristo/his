insert into status_atendimento (codigo, descricao, ativo, cor)
select 'ABANDONO', 'ABANDONO', true, '#6C757D'
where not exists (
    select 1
    from status_atendimento
    where upper(codigo) = 'ABANDONO'
       or upper(descricao) = 'ABANDONO'
);

update status_atendimento
set cor = '#6C757D'
where upper(codigo) = 'ABANDONO'
  and (cor is null or btrim(cor) = '');

insert into motivo_desfecho (descricao, ativo, cor)
select 'ABANDONO', true, '#6C757D'
where not exists (
    select 1
    from motivo_desfecho
    where upper(descricao) = 'ABANDONO'
);

update motivo_desfecho
set cor = '#6C757D'
where upper(descricao) = 'ABANDONO'
  and (cor is null or btrim(cor) = '');
