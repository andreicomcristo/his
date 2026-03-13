with tipo_assistencial as (
    select id
    from tipo_cargo
    where upper(codigo) = 'ASSISTENCIAL'
    fetch first 1 row only
)
insert into cargo_colaborador (codigo, descricao, tipo_cargo_id, ativo)
select
    novos.codigo,
    novos.descricao,
    ta.id,
    true
from tipo_assistencial ta
cross join (
    values
        ('MEDICO', 'MEDICO'),
        ('ENFERMEIRO', 'ENFERMEIRO'),
        ('TECNICO_ENFERMAGEM', 'TECNICO DE ENFERMAGEM'),
        ('FISIOTERAPEUTA', 'FISIOTERAPEUTA'),
        ('PSICOLOGO', 'PSICOLOGO'),
        ('NUTRICIONISTA', 'NUTRICIONISTA'),
        ('ASSISTENTE_SOCIAL', 'ASSISTENTE SOCIAL'),
        ('FONOAUDIOLOGO', 'FONOAUDIOLOGO'),
        ('TERAPEUTA_OCUPACIONAL', 'TERAPEUTA OCUPACIONAL'),
        ('FARMACEUTICO', 'FARMACEUTICO'),
        ('ODONTOLOGO', 'ODONTOLOGO'),
        ('EDUCADOR_FISICO', 'EDUCADOR FISICO'),
        ('BIOMEDICO', 'BIOMEDICO')
) as novos(codigo, descricao)
where not exists (
    select 1
    from cargo_colaborador existente
    where upper(existente.codigo) = upper(novos.codigo)
);
