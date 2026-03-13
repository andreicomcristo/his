insert into especialidade (codigo, descricao, ativo)
select 'CLINICA_MEDICA', 'CLINICA MEDICA', true
where not exists (
    select 1 from especialidade e where upper(e.codigo) = 'CLINICA_MEDICA'
);

insert into especialidade (codigo, descricao, ativo)
select 'ORTOPEDIA', 'ORTOPEDIA', true
where not exists (
    select 1 from especialidade e where upper(e.codigo) = 'ORTOPEDIA'
);

insert into especialidade (codigo, descricao, ativo)
select 'PEDIATRIA', 'PEDIATRIA', true
where not exists (
    select 1 from especialidade e where upper(e.codigo) = 'PEDIATRIA'
);

insert into especialidade (codigo, descricao, ativo)
select 'CARDIOLOGIA', 'CARDIOLOGIA', true
where not exists (
    select 1 from especialidade e where upper(e.codigo) = 'CARDIOLOGIA'
);

insert into especialidade (codigo, descricao, ativo)
select 'CIRURGIA_GERAL', 'CIRURGIA GERAL', true
where not exists (
    select 1 from especialidade e where upper(e.codigo) = 'CIRURGIA_GERAL'
);

insert into especialidade (codigo, descricao, ativo)
select 'GINECOLOGIA_OBSTETRICIA', 'GINECOLOGIA E OBSTETRICIA', true
where not exists (
    select 1 from especialidade e where upper(e.codigo) = 'GINECOLOGIA_OBSTETRICIA'
);

insert into especialidade (codigo, descricao, ativo)
select 'NEUROLOGIA', 'NEUROLOGIA', true
where not exists (
    select 1 from especialidade e where upper(e.codigo) = 'NEUROLOGIA'
);

insert into especialidade (codigo, descricao, ativo)
select 'UROLOGIA', 'UROLOGIA', true
where not exists (
    select 1 from especialidade e where upper(e.codigo) = 'UROLOGIA'
);

insert into especialidade (codigo, descricao, ativo)
select 'DERMATOLOGIA', 'DERMATOLOGIA', true
where not exists (
    select 1 from especialidade e where upper(e.codigo) = 'DERMATOLOGIA'
);

insert into especialidade (codigo, descricao, ativo)
select 'OFTALMOLOGIA', 'OFTALMOLOGIA', true
where not exists (
    select 1 from especialidade e where upper(e.codigo) = 'OFTALMOLOGIA'
);
