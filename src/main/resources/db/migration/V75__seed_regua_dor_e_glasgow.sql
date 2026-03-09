insert into regua_dor (valor, descricao, ativo)
values (0, 'SEM DOR', true)
on conflict (valor) do update
set descricao = excluded.descricao,
    ativo = excluded.ativo;

insert into regua_dor (valor, descricao, ativo)
values (1, 'DOR MINIMA', true)
on conflict (valor) do update
set descricao = excluded.descricao,
    ativo = excluded.ativo;

insert into regua_dor (valor, descricao, ativo)
values (2, 'DOR LEVE 2', true)
on conflict (valor) do update
set descricao = excluded.descricao,
    ativo = excluded.ativo;

insert into regua_dor (valor, descricao, ativo)
values (3, 'DOR LEVE 3', true)
on conflict (valor) do update
set descricao = excluded.descricao,
    ativo = excluded.ativo;

insert into regua_dor (valor, descricao, ativo)
values (4, 'DOR LEVE 4', true)
on conflict (valor) do update
set descricao = excluded.descricao,
    ativo = excluded.ativo;

insert into regua_dor (valor, descricao, ativo)
values (5, 'DOR MODERADA 5', true)
on conflict (valor) do update
set descricao = excluded.descricao,
    ativo = excluded.ativo;

insert into regua_dor (valor, descricao, ativo)
values (6, 'DOR MODERADA 6', true)
on conflict (valor) do update
set descricao = excluded.descricao,
    ativo = excluded.ativo;

insert into regua_dor (valor, descricao, ativo)
values (7, 'DOR MODERADA 7', true)
on conflict (valor) do update
set descricao = excluded.descricao,
    ativo = excluded.ativo;

insert into regua_dor (valor, descricao, ativo)
values (8, 'DOR INTENSA 8', true)
on conflict (valor) do update
set descricao = excluded.descricao,
    ativo = excluded.ativo;

insert into regua_dor (valor, descricao, ativo)
values (9, 'DOR INTENSA 9', true)
on conflict (valor) do update
set descricao = excluded.descricao,
    ativo = excluded.ativo;

insert into regua_dor (valor, descricao, ativo)
values (10, 'PIOR DOR POSSIVEL', true)
on conflict (valor) do update
set descricao = excluded.descricao,
    ativo = excluded.ativo;

insert into glasgow_abertura_ocular (pontos, descricao, ativo)
values (4, 'ESPONTANEA', true)
on conflict (pontos) do update
set descricao = excluded.descricao,
    ativo = excluded.ativo;

insert into glasgow_abertura_ocular (pontos, descricao, ativo)
values (3, 'AO SOM', true)
on conflict (pontos) do update
set descricao = excluded.descricao,
    ativo = excluded.ativo;

insert into glasgow_abertura_ocular (pontos, descricao, ativo)
values (2, 'A DOR', true)
on conflict (pontos) do update
set descricao = excluded.descricao,
    ativo = excluded.ativo;

insert into glasgow_abertura_ocular (pontos, descricao, ativo)
values (1, 'NENHUMA', true)
on conflict (pontos) do update
set descricao = excluded.descricao,
    ativo = excluded.ativo;

insert into glasgow_resposta_verbal (pontos, descricao, ativo)
values (5, 'ORIENTADA', true)
on conflict (pontos) do update
set descricao = excluded.descricao,
    ativo = excluded.ativo;

insert into glasgow_resposta_verbal (pontos, descricao, ativo)
values (4, 'CONFUSA', true)
on conflict (pontos) do update
set descricao = excluded.descricao,
    ativo = excluded.ativo;

insert into glasgow_resposta_verbal (pontos, descricao, ativo)
values (3, 'PALAVRAS INAPROPRIADAS', true)
on conflict (pontos) do update
set descricao = excluded.descricao,
    ativo = excluded.ativo;

insert into glasgow_resposta_verbal (pontos, descricao, ativo)
values (2, 'SONS INCOMPREENSIVEIS', true)
on conflict (pontos) do update
set descricao = excluded.descricao,
    ativo = excluded.ativo;

insert into glasgow_resposta_verbal (pontos, descricao, ativo)
values (1, 'NENHUMA', true)
on conflict (pontos) do update
set descricao = excluded.descricao,
    ativo = excluded.ativo;

insert into glasgow_resposta_motora (pontos, descricao, ativo)
values (6, 'OBEDECE A COMANDOS', true)
on conflict (pontos) do update
set descricao = excluded.descricao,
    ativo = excluded.ativo;

insert into glasgow_resposta_motora (pontos, descricao, ativo)
values (5, 'LOCALIZA DOR', true)
on conflict (pontos) do update
set descricao = excluded.descricao,
    ativo = excluded.ativo;

insert into glasgow_resposta_motora (pontos, descricao, ativo)
values (4, 'RETIRADA A DOR', true)
on conflict (pontos) do update
set descricao = excluded.descricao,
    ativo = excluded.ativo;

insert into glasgow_resposta_motora (pontos, descricao, ativo)
values (3, 'FLEXAO ANORMAL', true)
on conflict (pontos) do update
set descricao = excluded.descricao,
    ativo = excluded.ativo;

insert into glasgow_resposta_motora (pontos, descricao, ativo)
values (2, 'EXTENSAO ANORMAL', true)
on conflict (pontos) do update
set descricao = excluded.descricao,
    ativo = excluded.ativo;

insert into glasgow_resposta_motora (pontos, descricao, ativo)
values (1, 'NENHUMA', true)
on conflict (pontos) do update
set descricao = excluded.descricao,
    ativo = excluded.ativo;
