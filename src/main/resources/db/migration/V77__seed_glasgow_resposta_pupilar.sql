insert into glasgow_resposta_pupilar (pontos, descricao, ativo)
values (0, 'NENHUMA PUPILA ARREATIVA', true)
on conflict (pontos) do update
set descricao = excluded.descricao,
    ativo = excluded.ativo;

insert into glasgow_resposta_pupilar (pontos, descricao, ativo)
values (1, 'UMA PUPILA ARREATIVA', true)
on conflict (pontos) do update
set descricao = excluded.descricao,
    ativo = excluded.ativo;

insert into glasgow_resposta_pupilar (pontos, descricao, ativo)
values (2, 'DUAS PUPILAS ARREATIVAS', true)
on conflict (pontos) do update
set descricao = excluded.descricao,
    ativo = excluded.ativo;
