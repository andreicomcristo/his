insert into capacidade_area (nome, descricao, ativo)
select 'SALA_CLASSIFICACAO', 'Area contabilizada como sala de classificacao de risco', true
where not exists (
    select 1
    from capacidade_area
    where upper(nome) = 'SALA_CLASSIFICACAO'
);

insert into area_capacidade (area_id, capacidade_area_id)
select ac.area_id, cap_sala.id
from area_capacidade ac
join area a on a.id = ac.area_id
join capacidade_area cap_origem on cap_origem.id = ac.capacidade_area_id
join capacidade_area cap_sala on upper(cap_sala.nome) = 'SALA_CLASSIFICACAO'
where a.ativo = true
  and upper(cap_origem.nome) = 'REALIZA_CLASSIFICACAO'
on conflict (area_id, capacidade_area_id) do nothing;
