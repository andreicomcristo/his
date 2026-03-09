with unidade_hedh as (
    select id
    from unidade
    where cnes = 'HEDH'
),
areas_seed(nome, descricao) as (
    values
        ('AREA LILAS', 'Porta de entrada HEDH'),
        ('AREA VERMELHA', 'Porta de entrada HEDH'),
        ('CONSULTORIO OFTALMOLOGICO', 'Consultorio HEDH'),
        ('CONSULTORIO ORTOPEDICO', 'Consultorio HEDH'),
        ('ALA A', 'Area de internacao HEDH'),
        ('ALA B', 'Area de internacao HEDH'),
        ('ALA C', 'Area de internacao HEDH'),
        ('AREA LARANJA', 'Area de internacao HEDH'),
        ('UTI 1', 'Area de internacao HEDH'),
        ('UTI 2', 'Area de internacao HEDH'),
        ('UTI AVC', 'Area de internacao HEDH'),
        ('UTI GERAL', 'Area de internacao HEDH'),
        ('RECEPCAO EMERGENCIA', 'Recepcao de emergencia HEDH'),
        ('CLASSIFICACAO DE RISCO', 'Classificacao de risco HEDH')
)
insert into area (unidade_id, nome, descricao, ativo)
select u.id, s.nome, s.descricao, true
from unidade_hedh u
join areas_seed s on true
where not exists (
    select 1
    from area a
    where a.unidade_id = u.id
      and upper(a.nome) = s.nome
);

insert into area_capacidade (area_id, capacidade_area_id)
select a.id, c.id
from area a
join unidade u on u.id = a.unidade_id
join capacidade_area c on c.nome = 'RECEBE_ENTRADA'
where u.cnes = 'HEDH'
  and upper(a.nome) in ('AREA LILAS', 'AREA VERMELHA', 'RECEPCAO EMERGENCIA')
on conflict (area_id, capacidade_area_id) do nothing;

insert into area_capacidade (area_id, capacidade_area_id)
select a.id, c.id
from area a
join unidade u on u.id = a.unidade_id
join capacidade_area c on c.nome = 'REALIZA_ATENDIMENTO'
where u.cnes = 'HEDH'
  and upper(a.nome) in ('CONSULTORIO OFTALMOLOGICO', 'CONSULTORIO ORTOPEDICO')
on conflict (area_id, capacidade_area_id) do nothing;

insert into area_capacidade (area_id, capacidade_area_id)
select a.id, c.id
from area a
join unidade u on u.id = a.unidade_id
join capacidade_area c on c.nome in ('PERMITE_INTERNACAO', 'POSSUI_LEITO')
where u.cnes = 'HEDH'
  and upper(a.nome) in ('ALA A', 'ALA B', 'ALA C', 'AREA LARANJA', 'UTI 1', 'UTI 2', 'UTI AVC', 'UTI GERAL')
on conflict (area_id, capacidade_area_id) do nothing;

insert into area_capacidade (area_id, capacidade_area_id)
select a.id, c.id
from area a
join unidade u on u.id = a.unidade_id
join capacidade_area c on c.nome = 'REALIZA_CLASSIFICACAO'
where u.cnes = 'HEDH'
  and upper(a.nome) = 'CLASSIFICACAO DE RISCO'
on conflict (area_id, capacidade_area_id) do nothing;
