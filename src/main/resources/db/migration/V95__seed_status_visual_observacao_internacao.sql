insert into status_atendimento (codigo, descricao, ativo, cor)
select 'OBSERVACAO', 'OBSERVACAO', false, '#FFC107'
where not exists (
    select 1
    from status_atendimento
    where upper(codigo) = 'OBSERVACAO'
       or upper(descricao) = 'OBSERVACAO'
);

insert into status_atendimento (codigo, descricao, ativo, cor)
select 'INTERNACAO', 'INTERNACAO', false, '#17A2B8'
where not exists (
    select 1
    from status_atendimento
    where upper(codigo) = 'INTERNACAO'
       or upper(descricao) = 'INTERNACAO'
);

update status_atendimento
set cor = '#FFC107'
where upper(codigo) = 'OBSERVACAO'
  and (cor is null or trim(cor) = '');

update status_atendimento
set cor = '#17A2B8'
where upper(codigo) = 'INTERNACAO'
  and (cor is null or trim(cor) = '');
