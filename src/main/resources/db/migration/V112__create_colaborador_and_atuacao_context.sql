create table if not exists cargo_colaborador (
    id bigserial primary key,
    codigo varchar(80) not null,
    descricao varchar(150) not null,
    tipo_cargo varchar(20) not null,
    ativo boolean not null default true,
    constraint ck_cargo_colaborador_tipo
        check (upper(tipo_cargo) in ('ASSISTENCIAL', 'ADMINISTRATIVO'))
);

create unique index if not exists uq_cargo_colaborador_codigo_upper
    on cargo_colaborador (upper(codigo));

create table if not exists funcao_unidade (
    id bigserial primary key,
    codigo varchar(80) not null,
    descricao varchar(150) not null,
    tipo_funcao varchar(20) not null,
    requer_especialidade boolean not null default false,
    ativo boolean not null default true,
    constraint ck_funcao_unidade_tipo
        check (upper(tipo_funcao) in ('ASSISTENCIAL', 'ADMINISTRATIVO'))
);

create unique index if not exists uq_funcao_unidade_codigo_upper
    on funcao_unidade (upper(codigo));

create table if not exists colaborador (
    id bigserial primary key,
    nome varchar(150) not null,
    documento varchar(30),
    cargo_colaborador_id bigint,
    ativo boolean not null default true,
    constraint fk_colaborador_cargo_colaborador_id
        foreign key (cargo_colaborador_id) references cargo_colaborador (id)
);

create unique index if not exists uq_colaborador_documento_not_null
    on colaborador (documento)
    where documento is not null and btrim(documento) <> '';

create table if not exists usuario_colaborador (
    id bigserial primary key,
    usuario_id bigint not null,
    colaborador_id bigint not null,
    ativo boolean not null default true,
    constraint fk_usuario_colaborador_usuario_id
        foreign key (usuario_id) references usuario (id),
    constraint fk_usuario_colaborador_colaborador_id
        foreign key (colaborador_id) references colaborador (id),
    constraint uq_usuario_colaborador_usuario_id unique (usuario_id),
    constraint uq_usuario_colaborador_colaborador_id unique (colaborador_id)
);

create table if not exists colaborador_unidade_vinculo (
    id bigserial primary key,
    colaborador_id bigint not null,
    unidade_id bigint not null,
    tipo_vinculo_trabalhista varchar(40),
    inicio_vigencia date,
    fim_vigencia date,
    ativo boolean not null default true,
    constraint fk_colaborador_unidade_vinculo_colaborador_id
        foreign key (colaborador_id) references colaborador (id),
    constraint fk_colaborador_unidade_vinculo_unidade_id
        foreign key (unidade_id) references unidade (id),
    constraint uq_colaborador_unidade_vinculo_unique
        unique (colaborador_id, unidade_id)
);

create index if not exists idx_colaborador_unidade_vinculo_unidade_id
    on colaborador_unidade_vinculo (unidade_id);

create table if not exists colaborador_unidade_atuacao (
    id bigserial primary key,
    colaborador_unidade_vinculo_id bigint not null,
    funcao_unidade_id bigint not null,
    especialidade_id bigint,
    perfil_id bigint not null,
    inicio_vigencia date,
    fim_vigencia date,
    ativo boolean not null default true,
    constraint fk_colaborador_unidade_atuacao_vinculo_id
        foreign key (colaborador_unidade_vinculo_id) references colaborador_unidade_vinculo (id),
    constraint fk_colaborador_unidade_atuacao_funcao_unidade_id
        foreign key (funcao_unidade_id) references funcao_unidade (id),
    constraint fk_colaborador_unidade_atuacao_especialidade_id
        foreign key (especialidade_id) references especialidade (id),
    constraint fk_colaborador_unidade_atuacao_perfil_id
        foreign key (perfil_id) references perfil (id)
);

create unique index if not exists uq_colaborador_unidade_atuacao_contexto
    on colaborador_unidade_atuacao (
        colaborador_unidade_vinculo_id,
        funcao_unidade_id,
        coalesce(especialidade_id, 0),
        perfil_id
    );

create index if not exists idx_colaborador_unidade_atuacao_perfil_id
    on colaborador_unidade_atuacao (perfil_id);

create index if not exists idx_colaborador_unidade_atuacao_especialidade_id
    on colaborador_unidade_atuacao (especialidade_id);

insert into cargo_colaborador (codigo, descricao, tipo_cargo, ativo)
values
    ('COLABORADOR_GERAL', 'COLABORADOR GERAL', 'ADMINISTRATIVO', true)
on conflict do nothing;

insert into funcao_unidade (codigo, descricao, tipo_funcao, requer_especialidade, ativo)
values
    ('RECEPCAO', 'RECEPCAO', 'ADMINISTRATIVO', false, true),
    ('TRIAGEM', 'TRIAGEM', 'ASSISTENCIAL', false, true),
    ('BUROCRATA', 'BUROCRATA', 'ADMINISTRATIVO', false, true),
    ('ADMINISTRACAO_SISTEMA', 'ADMINISTRACAO SISTEMA', 'ADMINISTRATIVO', false, true),
    ('OPERACIONAL_GERAL', 'OPERACIONAL GERAL', 'ADMINISTRATIVO', false, true)
on conflict do nothing;

insert into colaborador (id, nome, ativo)
select
    u.id,
    upper(coalesce(nullif(btrim(u.username), ''), 'USUARIO ' || u.id::varchar)),
    u.ativo
from usuario u
on conflict (id) do update
set nome = excluded.nome,
    ativo = excluded.ativo;

select setval(
    pg_get_serial_sequence('colaborador', 'id'),
    coalesce((select max(id) from colaborador), 1),
    true
);

insert into usuario_colaborador (usuario_id, colaborador_id, ativo)
select
    u.id,
    c.id,
    true
from usuario u
join colaborador c on c.id = u.id
where not exists (
    select 1
    from usuario_colaborador uc
    where uc.usuario_id = u.id
);

insert into colaborador_unidade_vinculo (colaborador_id, unidade_id, ativo)
select distinct
    uc.colaborador_id,
    uup.unidade_id,
    true
from usuario_unidade_perfil uup
join usuario_colaborador uc on uc.usuario_id = uup.usuario_id and uc.ativo = true
where uup.ativo = true
on conflict (colaborador_id, unidade_id) do update
set ativo = true;

insert into colaborador_unidade_atuacao (
    colaborador_unidade_vinculo_id,
    funcao_unidade_id,
    especialidade_id,
    perfil_id,
    ativo
)
select
    cuv.id,
    fu.id,
    null,
    uup.perfil_id,
    true
from usuario_unidade_perfil uup
join usuario_colaborador uc
    on uc.usuario_id = uup.usuario_id
   and uc.ativo = true
join colaborador_unidade_vinculo cuv
    on cuv.colaborador_id = uc.colaborador_id
   and cuv.unidade_id = uup.unidade_id
join perfil p
    on p.id = uup.perfil_id
join funcao_unidade fu
    on upper(fu.codigo) = case
        when upper(p.nome) = 'RECEPCAO_USUARIO' then 'RECEPCAO'
        when upper(p.nome) = 'CLASSIFICACAO_USUARIO' then 'TRIAGEM'
        when upper(p.nome) = 'BUROCRATA_USUARIO' then 'BUROCRATA'
        when upper(p.nome) = 'SUPER_ADMIN' then 'ADMINISTRACAO_SISTEMA'
        else 'OPERACIONAL_GERAL'
    end
where uup.ativo = true
  and not exists (
      select 1
      from colaborador_unidade_atuacao cua
      where cua.colaborador_unidade_vinculo_id = cuv.id
        and cua.funcao_unidade_id = fu.id
        and cua.perfil_id = uup.perfil_id
        and cua.especialidade_id is null
  );
