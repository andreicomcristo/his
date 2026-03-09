insert into capacidade_area (nome, descricao, ativo)
select nome, descricao, ativo
from (
    values
        ('RECEBE_ENTRADA', 'Area habilitada para registrar a entrada do paciente', true),
        ('REALIZA_CLASSIFICACAO', 'Area habilitada para realizar classificacao de risco', true),
        ('REALIZA_ATENDIMENTO', 'Area habilitada para atendimento assistencial', true),
        ('PERMITE_OBSERVACAO', 'Area pode manter paciente em observacao', true),
        ('PERMITE_INTERNACAO', 'Area pode receber paciente internado', true),
        ('POSSUI_LEITO', 'Area possui leitos gerenciados pelo sistema', true)
) as seed(nome, descricao, ativo)
where not exists (
    select 1
    from capacidade_area c
    where upper(c.nome) = seed.nome
);
