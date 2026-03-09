create table if not exists avc_sinal_alerta (
    id bigserial primary key,
    codigo varchar(60) not null,
    descricao varchar(180) not null,
    ordem_exibicao integer not null default 0,
    ativo boolean not null default true
);

create unique index if not exists uk_avc_sinal_alerta_codigo
    on avc_sinal_alerta (upper(codigo));

create unique index if not exists uk_avc_sinal_alerta_descricao
    on avc_sinal_alerta (upper(descricao));

create index if not exists ix_avc_sinal_alerta_ordem
    on avc_sinal_alerta (ordem_exibicao, descricao);
