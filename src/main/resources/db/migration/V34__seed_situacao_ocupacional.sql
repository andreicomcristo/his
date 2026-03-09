insert into situacao_ocupacional (descricao, ativo)
select descricao, ativo
from (
    values
        ('EMPREGADO', true),
        ('DESEMPREGADO', true),
        ('AUTONOMO', true),
        ('SERVIDOR PUBLICO', true),
        ('EMPRESARIO', true),
        ('APOSENTADO', true),
        ('PENSIONISTA', true),
        ('ESTUDANTE', true),
        ('DO LAR', true),
        ('TRABALHADOR RURAL', true),
        ('INFORMAL', true),
        ('NAO INFORMADO', true)
) as seed(descricao, ativo)
where not exists (
    select 1
    from situacao_ocupacional s
    where upper(s.descricao) = seed.descricao
);
