-- Usuario operacional burocrata para unidade TREINAMENTO

insert into usuario (keycloak_id, username, email, ativo)
values ('pending:burocrata', 'burocrata', 'burocrata@his.local', true)
on conflict (username) do update
set email = excluded.email,
    ativo = true;

insert into usuario_unidade_perfil (usuario_id, unidade_id, perfil_id, ativo)
select us.id, un.id, pf.id, true
from usuario us
join unidade un on un.cnes = 'DEFAULT'
join perfil pf on pf.nome = 'BUROCRATA_USUARIO'
where us.username = 'burocrata'
on conflict (usuario_id, unidade_id, perfil_id) do update
set ativo = true;
