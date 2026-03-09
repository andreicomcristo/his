alter table leito
    add column if not exists permite_destino_definitivo boolean not null default true;

create table if not exists leito_modalidade_tipo (
    id bigserial primary key,
    codigo varchar(40) not null,
    descricao varchar(120) not null,
    ativo boolean not null default true
);

create unique index if not exists ux_leito_modalidade_tipo_codigo_upper
    on leito_modalidade_tipo (upper(codigo));

create table if not exists leito_modalidade (
    id bigserial primary key,
    leito_id bigint not null references leito(id) on delete cascade,
    modalidade_tipo_id bigint not null references leito_modalidade_tipo(id),
    constraint uk_leito_modalidade unique (leito_id, modalidade_tipo_id)
);

create index if not exists idx_leito_modalidade_leito
    on leito_modalidade (leito_id);

create index if not exists idx_leito_modalidade_tipo
    on leito_modalidade (modalidade_tipo_id);

insert into leito_modalidade_tipo (codigo, descricao, ativo)
select 'OBSERVACAO', 'LEITO PARA OBSERVACAO', true
where not exists (
    select 1 from leito_modalidade_tipo where upper(codigo) = 'OBSERVACAO'
);

insert into leito_modalidade_tipo (codigo, descricao, ativo)
select 'INTERNACAO', 'LEITO PARA INTERNACAO', true
where not exists (
    select 1 from leito_modalidade_tipo where upper(codigo) = 'INTERNACAO'
);

insert into leito_modalidade (leito_id, modalidade_tipo_id)
select distinct l.id, mt.id
from leito l
join area_capacidade ac on ac.area_id = l.area_id
join capacidade_area ca on ca.id = ac.capacidade_area_id
join leito_modalidade_tipo mt on upper(mt.codigo) = 'OBSERVACAO'
where upper(ca.nome) = 'PERMITE_OBSERVACAO'
  and not exists (
      select 1
      from leito_modalidade lm
      where lm.leito_id = l.id
        and lm.modalidade_tipo_id = mt.id
  );

insert into leito_modalidade (leito_id, modalidade_tipo_id)
select distinct l.id, mt.id
from leito l
join area_capacidade ac on ac.area_id = l.area_id
join capacidade_area ca on ca.id = ac.capacidade_area_id
join leito_modalidade_tipo mt on upper(mt.codigo) = 'INTERNACAO'
where upper(ca.nome) = 'PERMITE_INTERNACAO'
  and not exists (
      select 1
      from leito_modalidade lm
      where lm.leito_id = l.id
        and lm.modalidade_tipo_id = mt.id
  );

insert into leito_modalidade (leito_id, modalidade_tipo_id)
select l.id, mt.id
from leito l
join tipo_leito tl on tl.id = l.tipo_leito_id
join leito_modalidade_tipo mt on upper(mt.codigo) = 'OBSERVACAO'
where upper(tl.descricao) = 'OBSERVACAO'
  and not exists (
      select 1 from leito_modalidade lm where lm.leito_id = l.id
  );

insert into leito_modalidade (leito_id, modalidade_tipo_id)
select l.id, mt.id
from leito l
join leito_modalidade_tipo mt on upper(mt.codigo) = 'INTERNACAO'
where not exists (
      select 1 from leito_modalidade lm where lm.leito_id = l.id
);

update leito l
set permite_destino_definitivo = false
where exists (
    select 1
    from area_capacidade ac
    join capacidade_area ca on ca.id = ac.capacidade_area_id
    where ac.area_id = l.area_id
      and upper(ca.nome) = 'PERMITE_OBSERVACAO'
);

create table if not exists internacao_origem_demanda (
    id bigserial primary key,
    codigo varchar(40) not null,
    descricao varchar(120) not null,
    ativo boolean not null default true
);

create unique index if not exists ux_internacao_origem_demanda_codigo_upper
    on internacao_origem_demanda (upper(codigo));

insert into internacao_origem_demanda (codigo, descricao, ativo)
select 'PS', 'PRONTO SOCORRO', true
where not exists (select 1 from internacao_origem_demanda where upper(codigo) = 'PS');

insert into internacao_origem_demanda (codigo, descricao, ativo)
select 'ELETIVA', 'ELETIVA', true
where not exists (select 1 from internacao_origem_demanda where upper(codigo) = 'ELETIVA');

insert into internacao_origem_demanda (codigo, descricao, ativo)
select 'REGULACAO_DIRETA', 'REGULACAO DIRETA', true
where not exists (select 1 from internacao_origem_demanda where upper(codigo) = 'REGULACAO_DIRETA');

insert into internacao_origem_demanda (codigo, descricao, ativo)
select 'TRANSFERENCIA_EXTERNA', 'TRANSFERENCIA EXTERNA', true
where not exists (select 1 from internacao_origem_demanda where upper(codigo) = 'TRANSFERENCIA_EXTERNA');

create table if not exists internacao_perfil (
    id bigserial primary key,
    codigo varchar(40) not null,
    descricao varchar(120) not null,
    ativo boolean not null default true
);

create unique index if not exists ux_internacao_perfil_codigo_upper
    on internacao_perfil (upper(codigo));

insert into internacao_perfil (codigo, descricao, ativo)
select 'CLINICO', 'CLINICO', true
where not exists (select 1 from internacao_perfil where upper(codigo) = 'CLINICO');

insert into internacao_perfil (codigo, descricao, ativo)
select 'CIRURGICO', 'CIRURGICO', true
where not exists (select 1 from internacao_perfil where upper(codigo) = 'CIRURGICO');

create table if not exists observacao (
    id bigserial primary key,
    atendimento_id bigint not null unique references atendimento(id),
    data_hora_inicio timestamp not null,
    data_hora_fim timestamp,
    observacao text,
    constraint ck_observacao_periodo
        check (data_hora_fim is null or data_hora_fim >= data_hora_inicio)
);

create index if not exists idx_observacao_atendimento
    on observacao (atendimento_id);

create table if not exists internacao (
    id bigserial primary key,
    atendimento_id bigint not null unique references atendimento(id),
    origem_demanda_id bigint references internacao_origem_demanda(id),
    perfil_internacao_id bigint references internacao_perfil(id),
    data_hora_decisao_internacao timestamp not null,
    data_hora_inicio_internacao timestamp,
    data_hora_fim_internacao timestamp,
    observacao text,
    constraint ck_internacao_periodo
        check (
            data_hora_fim_internacao is null
            or (
                data_hora_inicio_internacao is not null
                and data_hora_fim_internacao >= data_hora_inicio_internacao
            )
        )
);

create index if not exists idx_internacao_atendimento
    on internacao (atendimento_id);

create table if not exists leito_ocupacao_tipo (
    id bigserial primary key,
    codigo varchar(40) not null,
    descricao varchar(120) not null,
    ativo boolean not null default true
);

create unique index if not exists ux_leito_ocupacao_tipo_codigo_upper
    on leito_ocupacao_tipo (upper(codigo));

insert into leito_ocupacao_tipo (codigo, descricao, ativo)
select 'PROVISORIA', 'OCUPACAO PROVISORIA', true
where not exists (select 1 from leito_ocupacao_tipo where upper(codigo) = 'PROVISORIA');

insert into leito_ocupacao_tipo (codigo, descricao, ativo)
select 'DEFINITIVA', 'OCUPACAO DEFINITIVA', true
where not exists (select 1 from leito_ocupacao_tipo where upper(codigo) = 'DEFINITIVA');

create table if not exists leito_ocupacao (
    id bigserial primary key,
    leito_id bigint not null references leito(id),
    observacao_id bigint references observacao(id) on delete cascade,
    internacao_id bigint references internacao(id) on delete cascade,
    tipo_ocupacao_id bigint not null references leito_ocupacao_tipo(id),
    data_hora_entrada timestamp not null,
    data_hora_saida timestamp,
    observacao text,
    constraint ck_leito_ocupacao_vinculo_exclusivo
        check (
            (observacao_id is not null and internacao_id is null)
            or (observacao_id is null and internacao_id is not null)
        ),
    constraint ck_leito_ocupacao_periodo
        check (data_hora_saida is null or data_hora_saida >= data_hora_entrada)
);

create index if not exists idx_leito_ocupacao_leito
    on leito_ocupacao (leito_id);

create index if not exists idx_leito_ocupacao_observacao
    on leito_ocupacao (observacao_id);

create index if not exists idx_leito_ocupacao_internacao
    on leito_ocupacao (internacao_id);

create unique index if not exists ux_leito_ocupacao_aberta_por_leito
    on leito_ocupacao (leito_id)
    where data_hora_saida is null;
