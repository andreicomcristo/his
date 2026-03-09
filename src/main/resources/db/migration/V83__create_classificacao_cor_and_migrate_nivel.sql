create table if not exists classificacao_cor (
    id bigserial primary key,
    descricao varchar(40) not null,
    cor varchar(20),
    ordem_exibicao integer not null default 0,
    ativo boolean not null default true
);

create unique index if not exists uk_classificacao_cor_descricao
    on classificacao_cor (upper(descricao));

create index if not exists ix_classificacao_cor_ordem
    on classificacao_cor (ordem_exibicao, descricao);

insert into classificacao_cor (descricao, cor, ordem_exibicao, ativo)
values ('VERMELHO', '#D64545', 1, true)
on conflict ((upper(descricao))) do update
set cor = excluded.cor,
    ordem_exibicao = excluded.ordem_exibicao,
    ativo = excluded.ativo;

insert into classificacao_cor (descricao, cor, ordem_exibicao, ativo)
values ('LARANJA', '#E67E22', 2, true)
on conflict ((upper(descricao))) do update
set cor = excluded.cor,
    ordem_exibicao = excluded.ordem_exibicao,
    ativo = excluded.ativo;

insert into classificacao_cor (descricao, cor, ordem_exibicao, ativo)
values ('AMARELO', '#D4AC0D', 3, true)
on conflict ((upper(descricao))) do update
set cor = excluded.cor,
    ordem_exibicao = excluded.ordem_exibicao,
    ativo = excluded.ativo;

insert into classificacao_cor (descricao, cor, ordem_exibicao, ativo)
values ('VERDE', '#118A23', 4, true)
on conflict ((upper(descricao))) do update
set cor = excluded.cor,
    ordem_exibicao = excluded.ordem_exibicao,
    ativo = excluded.ativo;

insert into classificacao_cor (descricao, cor, ordem_exibicao, ativo)
values ('AZUL', '#2471A3', 5, true)
on conflict ((upper(descricao))) do update
set cor = excluded.cor,
    ordem_exibicao = excluded.ordem_exibicao,
    ativo = excluded.ativo;

alter table classificacao_risco
    add column if not exists classificacao_cor_id bigint;

do $$
begin
    if not exists (
        select 1
        from pg_constraint
        where conname = 'fk_classificacao_risco_classificacao_cor'
    ) then
        alter table classificacao_risco
            add constraint fk_classificacao_risco_classificacao_cor
                foreign key (classificacao_cor_id) references classificacao_cor (id);
    end if;
end $$;

update classificacao_risco cr
set classificacao_cor_id = cc.id
from classificacao_cor cc
where cr.classificacao_cor_id is null
  and cr.nivel is not null
  and upper(cc.descricao) = upper(cr.nivel);
