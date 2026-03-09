create table if not exists regua_dor (
    id bigserial primary key,
    valor integer not null,
    descricao varchar(120) not null,
    ativo boolean not null default true,
    constraint ck_regua_dor_valor check (valor between 0 and 10)
);

create unique index if not exists uk_regua_dor_valor
    on regua_dor (valor);

create table if not exists glasgow_abertura_ocular (
    id bigserial primary key,
    pontos integer not null,
    descricao varchar(120) not null,
    ativo boolean not null default true,
    constraint ck_glasgow_abertura_ocular_pontos check (pontos between 1 and 4)
);

create unique index if not exists uk_glasgow_abertura_ocular_pontos
    on glasgow_abertura_ocular (pontos);

create unique index if not exists uk_glasgow_abertura_ocular_descricao
    on glasgow_abertura_ocular (upper(descricao));

create table if not exists glasgow_resposta_verbal (
    id bigserial primary key,
    pontos integer not null,
    descricao varchar(120) not null,
    ativo boolean not null default true,
    constraint ck_glasgow_resposta_verbal_pontos check (pontos between 1 and 5)
);

create unique index if not exists uk_glasgow_resposta_verbal_pontos
    on glasgow_resposta_verbal (pontos);

create unique index if not exists uk_glasgow_resposta_verbal_descricao
    on glasgow_resposta_verbal (upper(descricao));

create table if not exists glasgow_resposta_motora (
    id bigserial primary key,
    pontos integer not null,
    descricao varchar(120) not null,
    ativo boolean not null default true,
    constraint ck_glasgow_resposta_motora_pontos check (pontos between 1 and 6)
);

create unique index if not exists uk_glasgow_resposta_motora_pontos
    on glasgow_resposta_motora (pontos);

create unique index if not exists uk_glasgow_resposta_motora_descricao
    on glasgow_resposta_motora (upper(descricao));
