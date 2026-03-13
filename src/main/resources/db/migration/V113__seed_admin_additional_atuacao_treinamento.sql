with usuario_admin as (
    select id
    from usuario
    where upper(username) = 'ADMIN'
      and ativo = true
    fetch first 1 row only
),
unidade_treinamento as (
    select id
    from unidade
    where ativo = true
      and (upper(nome) = 'TREINAMENTO' or upper(cnes) = 'DEFAULT')
    order by case when upper(nome) = 'TREINAMENTO' then 0 else 1 end, id
    fetch first 1 row only
)
insert into colaborador_unidade_vinculo (
    colaborador_id,
    unidade_id,
    ativo
)
select
    uc.colaborador_id,
    ut.id,
    true
from usuario_admin ua
join unidade_treinamento ut on true
join usuario_colaborador uc
    on uc.usuario_id = ua.id
   and uc.ativo = true
on conflict (colaborador_id, unidade_id) do update
set ativo = true;

with usuario_admin as (
    select id
    from usuario
    where upper(username) = 'ADMIN'
      and ativo = true
    fetch first 1 row only
),
unidade_treinamento as (
    select id
    from unidade
    where ativo = true
      and (upper(nome) = 'TREINAMENTO' or upper(cnes) = 'DEFAULT')
    order by case when upper(nome) = 'TREINAMENTO' then 0 else 1 end, id
    fetch first 1 row only
)
insert into colaborador_unidade_atuacao (
    colaborador_unidade_vinculo_id,
    funcao_unidade_id,
    especialidade_id,
    perfil_id,
    ativo
)
select
    cuv.id,
    fu.id,
    null,
    p.id,
    true
from usuario_admin ua
join unidade_treinamento ut on true
join usuario_colaborador uc
    on uc.usuario_id = ua.id
   and uc.ativo = true
join colaborador_unidade_vinculo cuv
    on cuv.colaborador_id = uc.colaborador_id
   and cuv.unidade_id = ut.id
   and cuv.ativo = true
join funcao_unidade fu
    on upper(fu.codigo) = 'RECEPCAO'
   and fu.ativo = true
join perfil p
    on upper(p.nome) = 'RECEPCAO_USUARIO'
where not exists (
    select 1
    from colaborador_unidade_atuacao atual
    where atual.colaborador_unidade_vinculo_id = cuv.id
      and atual.funcao_unidade_id = fu.id
      and atual.perfil_id = p.id
      and atual.especialidade_id is null
);
