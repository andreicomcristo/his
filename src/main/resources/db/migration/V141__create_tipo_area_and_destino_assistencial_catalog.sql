create table if not exists tipo_area (
    id bigserial primary key,
    codigo varchar(40) not null,
    descricao varchar(120) not null,
    ordem_exibicao integer not null default 100,
    ativo boolean not null default true,
    constraint uq_tipo_area_codigo unique (codigo)
);

insert into tipo_area (codigo, descricao, ordem_exibicao, ativo)
values
    ('RECEPCAO', 'Recepcao', 10, true),
    ('TRIAGEM', 'Triagem', 20, true),
    ('CONSULTORIO', 'Consultorio', 30, true),
    ('OBSERVACAO', 'Observacao', 40, true),
    ('INTERNACAO', 'Internacao', 50, true),
    ('EXAME', 'Exame', 60, true),
    ('PROCEDIMENTO', 'Procedimento', 70, true),
    ('APOIO', 'Apoio', 999, true)
on conflict (codigo) do update
set descricao = excluded.descricao,
    ordem_exibicao = excluded.ordem_exibicao,
    ativo = true;

alter table area
    add column if not exists tipo_area_id bigint;

update area a
set tipo_area_id = ta.id
from tipo_area ta
where a.tipo_area_id is null
  and ta.codigo = 'TRIAGEM'
  and exists (
      select 1
      from area_capacidade ac
      join capacidade_area ca on ca.id = ac.capacidade_area_id
      where ac.area_id = a.id
        and upper(ca.nome) in ('REALIZA_CLASSIFICACAO', 'SALA_CLASSIFICACAO')
  );

update area a
set tipo_area_id = ta.id
from tipo_area ta
where a.tipo_area_id is null
  and ta.codigo = 'RECEPCAO'
  and exists (
      select 1
      from area_capacidade ac
      join capacidade_area ca on ca.id = ac.capacidade_area_id
      where ac.area_id = a.id
        and upper(ca.nome) = 'RECEBE_ENTRADA'
  );

update area a
set tipo_area_id = ta.id
from tipo_area ta
where a.tipo_area_id is null
  and ta.codigo = 'INTERNACAO'
  and exists (
      select 1
      from area_capacidade ac
      join capacidade_area ca on ca.id = ac.capacidade_area_id
      where ac.area_id = a.id
        and upper(ca.nome) = 'PERMITE_INTERNACAO'
  );

update area a
set tipo_area_id = ta.id
from tipo_area ta
where a.tipo_area_id is null
  and ta.codigo = 'OBSERVACAO'
  and exists (
      select 1
      from area_capacidade ac
      join capacidade_area ca on ca.id = ac.capacidade_area_id
      where ac.area_id = a.id
        and upper(ca.nome) = 'PERMITE_OBSERVACAO'
  );

update area a
set tipo_area_id = ta.id
from tipo_area ta
where a.tipo_area_id is null
  and ta.codigo = 'CONSULTORIO'
  and exists (
      select 1
      from area_capacidade ac
      join capacidade_area ca on ca.id = ac.capacidade_area_id
      where ac.area_id = a.id
        and upper(ca.nome) = 'REALIZA_ATENDIMENTO'
  );

update area a
set tipo_area_id = ta.id
from tipo_area ta
where a.tipo_area_id is null
  and ta.codigo = 'APOIO';

do $$
begin
    if not exists (
        select 1
        from pg_constraint
        where conname = 'fk_area_tipo_area'
    ) then
        alter table area
            add constraint fk_area_tipo_area
                foreign key (tipo_area_id) references tipo_area (id);
    end if;
end
$$;

create index if not exists idx_area_tipo_area_id
    on area (tipo_area_id);

alter table area
    alter column tipo_area_id set not null;

create table if not exists tipo_destino_assistencial (
    id bigserial primary key,
    codigo varchar(40) not null,
    descricao varchar(120) not null,
    ordem_exibicao integer not null default 100,
    ativo boolean not null default true,
    constraint uq_tipo_destino_assistencial_codigo unique (codigo)
);

insert into tipo_destino_assistencial (codigo, descricao, ordem_exibicao, ativo)
values
    ('RECEPCAO', 'Recepcao', 10, true),
    ('TRIAGEM', 'Triagem', 20, true),
    ('CONSULTORIO', 'Consultorio', 30, true),
    ('EXAME_IMAGEM', 'Exame de imagem', 40, true),
    ('EXAME_LABORATORIAL', 'Exame laboratorial', 50, true),
    ('PROCEDIMENTO', 'Procedimento', 60, true),
    ('OBSERVACAO', 'Observacao', 70, true),
    ('INTERNACAO', 'Internacao', 80, true),
    ('APOIO', 'Apoio', 999, true)
on conflict (codigo) do update
set descricao = excluded.descricao,
    ordem_exibicao = excluded.ordem_exibicao,
    ativo = true;

create table if not exists destino_assistencial (
    id bigserial primary key,
    unidade_id bigint not null,
    tipo_destino_assistencial_id bigint not null,
    codigo varchar(60) not null,
    descricao varchar(150) not null,
    observacao varchar(500),
    ordem_exibicao integer not null default 100,
    ativo boolean not null default true,
    constraint fk_destino_assistencial_unidade
        foreign key (unidade_id) references unidade (id),
    constraint fk_destino_assistencial_tipo
        foreign key (tipo_destino_assistencial_id) references tipo_destino_assistencial (id),
    constraint uq_destino_assistencial_unidade_codigo
        unique (unidade_id, codigo)
);

create index if not exists idx_destino_assistencial_unidade
    on destino_assistencial (unidade_id);

create index if not exists idx_destino_assistencial_tipo
    on destino_assistencial (tipo_destino_assistencial_id);

create table if not exists destino_assistencial_area (
    id bigserial primary key,
    destino_assistencial_id bigint not null,
    area_id bigint not null,
    prioridade integer not null default 100,
    ativo boolean not null default true,
    constraint fk_destino_assistencial_area_destino
        foreign key (destino_assistencial_id) references destino_assistencial (id),
    constraint fk_destino_assistencial_area_area
        foreign key (area_id) references area (id),
    constraint uq_destino_assistencial_area
        unique (destino_assistencial_id, area_id)
);

create index if not exists idx_destino_assistencial_area_destino
    on destino_assistencial_area (destino_assistencial_id);

create index if not exists idx_destino_assistencial_area_area
    on destino_assistencial_area (area_id);
