with unidade_treinamento as (
    select id
    from unidade
    where cnes = 'DEFAULT'
),
areas_seed(nome, descricao) as (
    values
        ('RECEPCAO', 'Porta de entrada da unidade treinamento'),
        ('AREA VERMELHA', 'Porta de entrada da unidade treinamento'),
        ('CONSULTORIO OFTALMOLOGICO', 'Consultorio da unidade treinamento'),
        ('CONSULTORIO ORTOPEDICO', 'Consultorio da unidade treinamento'),
        ('ALA A', 'Area de internacao da unidade treinamento'),
        ('ALA B', 'Area de internacao da unidade treinamento'),
        ('ALA C', 'Area de internacao da unidade treinamento'),
        ('AREA LARANJA', 'Area de internacao da unidade treinamento'),
        ('UTI 1', 'Area de internacao da unidade treinamento'),
        ('UTI 2', 'Area de internacao da unidade treinamento'),
        ('UTI AVC', 'Area de internacao da unidade treinamento'),
        ('UTI GERAL', 'Area de internacao da unidade treinamento'),
        ('ALA AMARELA - OBSERVACAO 1', 'Area de observacao da unidade treinamento'),
        ('ALA AMARELA - OBSERVACAO 2', 'Area de observacao da unidade treinamento'),
        ('ALA AMARELA - OBSERVACAO 3', 'Area de observacao da unidade treinamento'),
        ('CLASSIFICACAO DE RISCO', 'Classificacao de risco da unidade treinamento')
)
insert into area (unidade_id, nome, descricao, ativo)
select u.id, s.nome, s.descricao, true
from unidade_treinamento u
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
where u.cnes = 'DEFAULT'
  and upper(a.nome) in ('RECEPCAO', 'AREA VERMELHA')
on conflict (area_id, capacidade_area_id) do nothing;

insert into area_capacidade (area_id, capacidade_area_id)
select a.id, c.id
from area a
join unidade u on u.id = a.unidade_id
join capacidade_area c on c.nome = 'REALIZA_ATENDIMENTO'
where u.cnes = 'DEFAULT'
  and upper(a.nome) in ('CONSULTORIO OFTALMOLOGICO', 'CONSULTORIO ORTOPEDICO')
on conflict (area_id, capacidade_area_id) do nothing;

insert into area_capacidade (area_id, capacidade_area_id)
select a.id, c.id
from area a
join unidade u on u.id = a.unidade_id
join capacidade_area c on c.nome in ('PERMITE_INTERNACAO', 'POSSUI_LEITO')
where u.cnes = 'DEFAULT'
  and upper(a.nome) in ('ALA A', 'ALA B', 'ALA C', 'AREA LARANJA', 'UTI 1', 'UTI 2', 'UTI AVC', 'UTI GERAL')
on conflict (area_id, capacidade_area_id) do nothing;

insert into area_capacidade (area_id, capacidade_area_id)
select a.id, c.id
from area a
join unidade u on u.id = a.unidade_id
join capacidade_area c on c.nome in ('PERMITE_OBSERVACAO', 'PERMITE_INTERNACAO', 'POSSUI_LEITO')
where u.cnes = 'DEFAULT'
  and upper(a.nome) in (
      'ALA AMARELA - OBSERVACAO 1',
      'ALA AMARELA - OBSERVACAO 2',
      'ALA AMARELA - OBSERVACAO 3'
  )
on conflict (area_id, capacidade_area_id) do nothing;

insert into area_capacidade (area_id, capacidade_area_id)
select a.id, c.id
from area a
join unidade u on u.id = a.unidade_id
join capacidade_area c on c.nome = 'REALIZA_CLASSIFICACAO'
where u.cnes = 'DEFAULT'
  and upper(a.nome) = 'CLASSIFICACAO DE RISCO'
on conflict (area_id, capacidade_area_id) do nothing;
