insert into status_atendimento (codigo, descricao, ativo)
select 'AGUARDANDO', 'AGUARDANDO', true
where not exists (select 1 from status_atendimento where upper(codigo) = 'AGUARDANDO');

insert into status_atendimento (codigo, descricao, ativo)
select 'EM_TRIAGEM', 'EM TRIAGEM', true
where not exists (select 1 from status_atendimento where upper(codigo) = 'EM_TRIAGEM');

insert into status_atendimento (codigo, descricao, ativo)
select 'AGUARDANDO_RECEPCAO', 'AGUARDANDO RECEPCAO', true
where not exists (select 1 from status_atendimento where upper(codigo) = 'AGUARDANDO_RECEPCAO');

insert into status_atendimento (codigo, descricao, ativo)
select 'AGUARDANDO_TRIAGEM', 'AGUARDANDO TRIAGEM', true
where not exists (select 1 from status_atendimento where upper(codigo) = 'AGUARDANDO_TRIAGEM');

insert into status_atendimento (codigo, descricao, ativo)
select 'AGUARDANDO_MEDICO', 'AGUARDANDO MEDICO', true
where not exists (select 1 from status_atendimento where upper(codigo) = 'AGUARDANDO_MEDICO');

insert into status_atendimento (codigo, descricao, ativo)
select 'EM_ATENDIMENTO', 'EM ATENDIMENTO', true
where not exists (select 1 from status_atendimento where upper(codigo) = 'EM_ATENDIMENTO');

insert into status_atendimento (codigo, descricao, ativo)
select 'FINALIZADO', 'FINALIZADO', true
where not exists (select 1 from status_atendimento where upper(codigo) = 'FINALIZADO');

insert into status_atendimento (codigo, descricao, ativo)
select 'EVADIU', 'EVADIU', true
where not exists (select 1 from status_atendimento where upper(codigo) = 'EVADIU');

insert into status_atendimento (codigo, descricao, ativo)
select 'TRANSFERIDO', 'TRANSFERIDO', true
where not exists (select 1 from status_atendimento where upper(codigo) = 'TRANSFERIDO');
