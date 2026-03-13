with tipo_administrativo as (
    select id
    from tipo_cargo
    where upper(codigo) = 'ADMINISTRATIVO'
    fetch first 1 row only
)
insert into cargo_colaborador (codigo, descricao, tipo_cargo_id, ativo)
select
    novos.codigo,
    novos.descricao,
    ta.id,
    true
from tipo_administrativo ta
cross join (
    values
        ('RECEPCIONISTA', 'RECEPCIONISTA'),
        ('FATURISTA', 'FATURISTA'),
        ('REGULADOR', 'REGULADOR'),
        ('AUXILIAR_ADMINISTRATIVO', 'AUXILIAR ADMINISTRATIVO'),
        ('ASSISTENTE_ADMINISTRATIVO', 'ASSISTENTE ADMINISTRATIVO'),
        ('COORDENADOR_ADMINISTRATIVO', 'COORDENADOR ADMINISTRATIVO'),
        ('SUPERVISOR_ADMINISTRATIVO', 'SUPERVISOR ADMINISTRATIVO'),
        ('GESTOR_UNIDADE', 'GESTOR DE UNIDADE'),
        ('SECRETARIO_CLINICO', 'SECRETARIO CLINICO'),
        ('ANALISTA_REGULACAO', 'ANALISTA DE REGULACAO')
) as novos(codigo, descricao)
where not exists (
    select 1
    from cargo_colaborador existente
    where upper(existente.codigo) = upper(novos.codigo)
);
