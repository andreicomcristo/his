insert into forma_chegada (descricao, ativo)
select descricao, ativo
from (
    values
        ('BOMBEIROS', true),
        ('TRANSPORTE MUNICIPAL', true),
        ('SAMU TERRESTRE', true),
        ('TRANSPORTE PROPRIO', true),
        ('AMBULANCIA MUNICIPAL', true),
        ('TRANSPORTE EMPRESA', true),
        ('TRANSPORTE TERCEIROS', true),
        ('AMBULANCIA', true),
        ('POLICIA MILITAR', true),
        ('NAO INFORMADO', true),
        ('FAMILIAR', true),
        ('POLICIA PENAL', true),
        ('POLICIA CIVIL', true),
        ('SAMU AEREO', true),
        ('TRANSPORTE DA ALDEIA', true)
) as seed(descricao, ativo)
where not exists (
    select 1
    from forma_chegada f
    where upper(f.descricao) = seed.descricao
);
