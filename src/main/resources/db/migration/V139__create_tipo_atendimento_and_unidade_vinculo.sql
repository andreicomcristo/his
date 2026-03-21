create table tipo_atendimento (
    id bigserial primary key,
    codigo varchar(40) not null,
    descricao varchar(120) not null,
    ordem_exibicao integer not null default 100,
    ativo boolean not null default true,
    constraint uq_tipo_atendimento_codigo unique (codigo)
);

create table unidade_tipo_atendimento (
    id bigserial primary key,
    unidade_id bigint not null,
    tipo_atendimento_id bigint not null,
    ativo boolean not null default true,
    triagem_obrigatoria boolean not null default false,
    passa_consultorio boolean not null default true,
    permite_agendamento boolean not null default true,
    constraint fk_unidade_tipo_atendimento_unidade
        foreign key (unidade_id) references unidade (id),
    constraint fk_unidade_tipo_atendimento_tipo
        foreign key (tipo_atendimento_id) references tipo_atendimento (id),
    constraint uq_unidade_tipo_atendimento unique (unidade_id, tipo_atendimento_id)
);

create index idx_unidade_tipo_atendimento_unidade
    on unidade_tipo_atendimento (unidade_id);

create index idx_unidade_tipo_atendimento_tipo
    on unidade_tipo_atendimento (tipo_atendimento_id);

insert into tipo_atendimento (codigo, descricao, ordem_exibicao, ativo)
values
    ('URGENCIA', 'Urgencia', 10, true),
    ('AMBULATORIAL', 'Ambulatorial', 20, true),
    ('INTERNACAO_DIRETA', 'Internacao direta', 30, true),
    ('PROCEDIMENTO', 'Procedimento', 40, true)
on conflict (codigo) do update
set descricao = excluded.descricao,
    ordem_exibicao = excluded.ordem_exibicao,
    ativo = true;

insert into unidade_tipo_atendimento (
    unidade_id,
    tipo_atendimento_id,
    ativo,
    triagem_obrigatoria,
    passa_consultorio,
    permite_agendamento
)
select
    u.id,
    ta.id,
    true,
    coalesce(urt.triagem_obrigatoria, false),
    case
        when ta.codigo = 'PROCEDIMENTO' then false
        else true
    end as passa_consultorio,
    case
        when ta.codigo in ('AMBULATORIAL', 'PROCEDIMENTO') then true
        else false
    end as permite_agendamento
from unidade u
join tipo_atendimento ta on ta.codigo in ('URGENCIA', 'AMBULATORIAL', 'INTERNACAO_DIRETA', 'PROCEDIMENTO')
left join unidade_regra_triagem urt
    on urt.unidade_id = u.id
   and upper(urt.tipo_atendimento) = upper(ta.codigo)
where u.ativo = true
on conflict (unidade_id, tipo_atendimento_id) do update
set ativo = excluded.ativo,
    triagem_obrigatoria = excluded.triagem_obrigatoria,
    passa_consultorio = excluded.passa_consultorio,
    permite_agendamento = excluded.permite_agendamento;

alter table atendimento
    drop constraint if exists ck_atendimento_tipo;
