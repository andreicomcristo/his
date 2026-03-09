insert into grau_parentesco (descricao, ativo)
select 'MAE', true
where not exists (
    select 1 from grau_parentesco where upper(trim(descricao)) = 'MAE'
);

insert into grau_parentesco (descricao, ativo)
select 'PAI', true
where not exists (
    select 1 from grau_parentesco where upper(trim(descricao)) = 'PAI'
);

insert into grau_parentesco (descricao, ativo)
select 'FILHO(A)', true
where not exists (
    select 1 from grau_parentesco where upper(trim(descricao)) = 'FILHO(A)'
);

insert into grau_parentesco (descricao, ativo)
select 'CONJUGE', true
where not exists (
    select 1 from grau_parentesco where upper(trim(descricao)) = 'CONJUGE'
);

insert into grau_parentesco (descricao, ativo)
select 'IRMAO(A)', true
where not exists (
    select 1 from grau_parentesco where upper(trim(descricao)) = 'IRMAO(A)'
);

insert into grau_parentesco (descricao, ativo)
select 'OUTRO FAMILIAR', true
where not exists (
    select 1 from grau_parentesco where upper(trim(descricao)) = 'OUTRO FAMILIAR'
);

insert into grau_parentesco (descricao, ativo)
select 'RESPONSAVEL LEGAL', true
where not exists (
    select 1 from grau_parentesco where upper(trim(descricao)) = 'RESPONSAVEL LEGAL'
);

insert into grau_parentesco (descricao, ativo)
select 'CUIDADOR', true
where not exists (
    select 1 from grau_parentesco where upper(trim(descricao)) = 'CUIDADOR'
);

insert into grau_parentesco (descricao, ativo)
select 'VIZINHO', true
where not exists (
    select 1 from grau_parentesco where upper(trim(descricao)) = 'VIZINHO'
);

insert into grau_parentesco (descricao, ativo)
select 'SEM PARENTESCO', true
where not exists (
    select 1 from grau_parentesco where upper(trim(descricao)) = 'SEM PARENTESCO'
);

insert into grau_parentesco (descricao, ativo)
select 'NAO INFORMADO', true
where not exists (
    select 1 from grau_parentesco where upper(trim(descricao)) = 'NAO INFORMADO'
);
