-- Garante que cada especialidade tenha no maximo um cargo ativo vinculado.
with marcacao as (
    select
        id,
        especialidade_id,
        ativo,
        row_number() over (
            partition by especialidade_id
            order by case when ativo then 0 else 1 end, id
        ) as ordem,
        bool_or(ativo) over (partition by especialidade_id) as possui_ativo
    from cargo_colaborador_especialidade
)
update cargo_colaborador_especialidade cce
set ativo = case
                when m.ordem = 1 then true
                else false
            end
from marcacao m
where cce.id = m.id
  and m.possui_ativo = true;

create unique index if not exists uq_cargo_colab_esp_especialidade_ativa
    on cargo_colaborador_especialidade (especialidade_id)
    where ativo = true;
