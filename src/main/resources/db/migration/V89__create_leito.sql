create table if not exists tipo_leito (
    id bigserial primary key,
    descricao varchar(80) not null,
    ativo boolean not null default true
);

create unique index if not exists ux_tipo_leito_descricao_upper
    on tipo_leito (upper(descricao));

create table if not exists perfil_leito (
    id bigserial primary key,
    descricao varchar(80) not null,
    ativo boolean not null default true
);

create unique index if not exists ux_perfil_leito_descricao_upper
    on perfil_leito (upper(descricao));

create table if not exists leito (
    id bigserial primary key,
    unidade_id bigint not null,
    area_id bigint not null,
    tipo_leito_id bigint not null,
    perfil_leito_id bigint null,
    codigo varchar(50) not null,
    descricao varchar(255),
    recebe_ps boolean not null default false,
    assistencial boolean not null default true,
    ativo boolean not null default true,
    constraint fk_leito_unidade foreign key (unidade_id) references unidade (id),
    constraint fk_leito_area foreign key (area_id) references area (id),
    constraint fk_leito_tipo foreign key (tipo_leito_id) references tipo_leito (id),
    constraint fk_leito_perfil foreign key (perfil_leito_id) references perfil_leito (id)
);

create unique index if not exists ux_leito_unidade_codigo_upper
    on leito (unidade_id, upper(codigo));

create index if not exists idx_leito_unidade
    on leito (unidade_id);

create index if not exists idx_leito_area
    on leito (area_id);

create index if not exists idx_leito_tipo
    on leito (tipo_leito_id);

insert into tipo_leito (descricao, ativo)
select 'ENFERMARIA', true
where not exists (select 1 from tipo_leito where upper(descricao) = 'ENFERMARIA');

insert into tipo_leito (descricao, ativo)
select 'UTI_GERAL', true
where not exists (select 1 from tipo_leito where upper(descricao) = 'UTI_GERAL');

insert into tipo_leito (descricao, ativo)
select 'UTI_ESPECIALIZADA', true
where not exists (select 1 from tipo_leito where upper(descricao) = 'UTI_ESPECIALIZADA');

insert into tipo_leito (descricao, ativo)
select 'RPA', true
where not exists (select 1 from tipo_leito where upper(descricao) = 'RPA');

insert into tipo_leito (descricao, ativo)
select 'OBSERVACAO', true
where not exists (select 1 from tipo_leito where upper(descricao) = 'OBSERVACAO');

insert into tipo_leito (descricao, ativo)
select 'CORREDOR', true
where not exists (select 1 from tipo_leito where upper(descricao) = 'CORREDOR');

insert into perfil_leito (descricao, ativo)
select 'CLINICO', true
where not exists (select 1 from perfil_leito where upper(descricao) = 'CLINICO');

insert into perfil_leito (descricao, ativo)
select 'CIRURGICO', true
where not exists (select 1 from perfil_leito where upper(descricao) = 'CIRURGICO');
