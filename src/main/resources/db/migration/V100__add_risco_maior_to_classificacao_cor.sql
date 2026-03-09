alter table classificacao_cor
    add column if not exists risco_maior boolean not null default false;

update classificacao_cor
set risco_maior = true
where upper(descricao) in ('VERMELHO', 'LARANJA', 'AMARELO');
