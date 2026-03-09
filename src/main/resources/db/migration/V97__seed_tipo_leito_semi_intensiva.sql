insert into tipo_leito (descricao, ativo)
select 'SEMI_INTENSIVA', true
where not exists (
    select 1
    from tipo_leito
    where upper(descricao) = 'SEMI_INTENSIVA'
);
