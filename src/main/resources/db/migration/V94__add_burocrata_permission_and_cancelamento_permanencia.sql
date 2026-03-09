alter table if exists observacao
    add column if not exists data_hora_cancelamento timestamp;

alter table if exists observacao
    add column if not exists cancelado_por varchar(100);

alter table if exists observacao
    add column if not exists motivo_cancelamento text;

alter table if exists internacao
    add column if not exists data_hora_cancelamento timestamp;

alter table if exists internacao
    add column if not exists cancelado_por varchar(100);

alter table if exists internacao
    add column if not exists motivo_cancelamento text;

insert into permissao (nome)
values ('BUROCRATA_EXECUTAR')
on conflict (nome) do nothing;

insert into perfil (nome)
values ('BUROCRATA_USUARIO')
on conflict (nome) do nothing;

insert into perfil_permissao (perfil_id, permissao_id)
select p.id, pm.id
from perfil p
join permissao pm on pm.nome in ('ATENDIMENTO_ACESSAR', 'PACIENTE_VISUALIZAR', 'BUROCRATA_EXECUTAR')
where p.nome = 'BUROCRATA_USUARIO'
on conflict (perfil_id, permissao_id) do nothing;

insert into perfil_permissao (perfil_id, permissao_id)
select p.id, pm.id
from perfil p
join permissao pm on pm.nome = 'BUROCRATA_EXECUTAR'
where p.nome = 'SUPER_ADMIN'
on conflict (perfil_id, permissao_id) do nothing;
