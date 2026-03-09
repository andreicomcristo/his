insert into avc_sinal_alerta (codigo, descricao, ordem_exibicao, ativo)
values ('CEFALEIA_INTENSA_E_SUBITA_SEM_CAUSA_APARENTE', 'CEFALEIA INTENSA E SUBITA SEM CAUSA APARENTE', 10, true)
on conflict ((upper(codigo))) do update
set descricao = excluded.descricao,
    ordem_exibicao = excluded.ordem_exibicao,
    ativo = excluded.ativo;

insert into avc_sinal_alerta (codigo, descricao, ordem_exibicao, ativo)
values ('CONFUSAO_DIFICULDADE_PARA_FALAR_OU_ENTENDER_DE_INICIO_SUBITO', 'CONFUSAO, DIFICULDADE PARA FALAR OU ENTENDER DE INICIO SUBITO', 20, true)
on conflict ((upper(codigo))) do update
set descricao = excluded.descricao,
    ordem_exibicao = excluded.ordem_exibicao,
    ativo = excluded.ativo;

insert into avc_sinal_alerta (codigo, descricao, ordem_exibicao, ativo)
values ('DIFICULDADE_SUBITA_PARA_ANDAR', 'DIFICULDADE SUBITA PARA ANDAR', 30, true)
on conflict ((upper(codigo))) do update
set descricao = excluded.descricao,
    ordem_exibicao = excluded.ordem_exibicao,
    ativo = excluded.ativo;

insert into avc_sinal_alerta (codigo, descricao, ordem_exibicao, ativo)
values ('DIFICULDADE_SUBITA_PARA_ENXERGAR', 'DIFICULDADE SUBITA PARA ENXERGAR', 40, true)
on conflict ((upper(codigo))) do update
set descricao = excluded.descricao,
    ordem_exibicao = excluded.ordem_exibicao,
    ativo = excluded.ativo;

insert into avc_sinal_alerta (codigo, descricao, ordem_exibicao, ativo)
values ('FRAQUEZA_OU_DORMENCIA_SUBITA_EM_UM_LADO_DO_CORPO', 'FRAQUEZA OU DORMENCIA SUBITA EM UM LADO DO CORPO', 50, true)
on conflict ((upper(codigo))) do update
set descricao = excluded.descricao,
    ordem_exibicao = excluded.ordem_exibicao,
    ativo = excluded.ativo;

insert into avc_sinal_alerta (codigo, descricao, ordem_exibicao, ativo)
values ('TONTURA_OU_INCOORDENACAO_DE_INICIO_SUBITO', 'TONTURA OU INCOORDENACAO DE INICIO SUBITO', 60, true)
on conflict ((upper(codigo))) do update
set descricao = excluded.descricao,
    ordem_exibicao = excluded.ordem_exibicao,
    ativo = excluded.ativo;

update avc_sinal_alerta
set ativo = false
where upper(codigo) not in (
    'CEFALEIA_INTENSA_E_SUBITA_SEM_CAUSA_APARENTE',
    'CONFUSAO_DIFICULDADE_PARA_FALAR_OU_ENTENDER_DE_INICIO_SUBITO',
    'DIFICULDADE_SUBITA_PARA_ANDAR',
    'DIFICULDADE_SUBITA_PARA_ENXERGAR',
    'FRAQUEZA_OU_DORMENCIA_SUBITA_EM_UM_LADO_DO_CORPO',
    'TONTURA_OU_INCOORDENACAO_DE_INICIO_SUBITO'
);
