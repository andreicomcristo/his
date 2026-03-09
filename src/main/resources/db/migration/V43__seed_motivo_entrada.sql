insert into motivo_entrada (descricao, ativo)
select 'ATENDIMENTO CLINICO', true
where not exists (
    select 1 from motivo_entrada where upper(trim(descricao)) = 'ATENDIMENTO CLINICO'
);

insert into motivo_entrada (descricao, ativo)
select 'ATENDIMENTO CIRURGICO', true
where not exists (
    select 1 from motivo_entrada where upper(trim(descricao)) = 'ATENDIMENTO CIRURGICO'
);

insert into motivo_entrada (descricao, ativo)
select 'TRAUMA', true
where not exists (
    select 1 from motivo_entrada where upper(trim(descricao)) = 'TRAUMA'
);

insert into motivo_entrada (descricao, ativo)
select 'RETORNO', true
where not exists (
    select 1 from motivo_entrada where upper(trim(descricao)) = 'RETORNO'
);

insert into motivo_entrada (descricao, ativo)
select 'TRANSFERENCIA', true
where not exists (
    select 1 from motivo_entrada where upper(trim(descricao)) = 'TRANSFERENCIA'
);

insert into motivo_entrada (descricao, ativo)
select 'OBSERVACAO', true
where not exists (
    select 1 from motivo_entrada where upper(trim(descricao)) = 'OBSERVACAO'
);

insert into motivo_entrada (descricao, ativo)
select 'OUTROS', true
where not exists (
    select 1 from motivo_entrada where upper(trim(descricao)) = 'OUTROS'
);
