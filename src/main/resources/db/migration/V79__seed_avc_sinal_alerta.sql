insert into avc_sinal_alerta (codigo, descricao, ordem_exibicao, ativo)
values ('FACE_ASSIMETRICA', 'ASSIMETRIA FACIAL', 10, true)
on conflict ((upper(codigo))) do update
set descricao = excluded.descricao,
    ativo = excluded.ativo;

insert into avc_sinal_alerta (codigo, descricao, ordem_exibicao, ativo)
values ('FRAQUEZA_MEMBRO_SUPERIOR', 'FRAQUEZA EM MEMBRO SUPERIOR', 20, true)
on conflict ((upper(codigo))) do update
set descricao = excluded.descricao,
    ativo = excluded.ativo;

insert into avc_sinal_alerta (codigo, descricao, ordem_exibicao, ativo)
values ('ALTERACAO_FALA', 'ALTERACAO DE FALA', 30, true)
on conflict ((upper(codigo))) do update
set descricao = excluded.descricao,
    ativo = excluded.ativo;

insert into avc_sinal_alerta (codigo, descricao, ordem_exibicao, ativo)
values ('ALTERACAO_VISUAL_SUBITA', 'ALTERACAO VISUAL SUBITA', 40, true)
on conflict ((upper(codigo))) do update
set descricao = excluded.descricao,
    ativo = excluded.ativo;

insert into avc_sinal_alerta (codigo, descricao, ordem_exibicao, ativo)
values ('CEFALEIA_SUBITA_INTENSA', 'CEFALEIA SUBITA INTENSA', 50, true)
on conflict ((upper(codigo))) do update
set descricao = excluded.descricao,
    ativo = excluded.ativo;

insert into avc_sinal_alerta (codigo, descricao, ordem_exibicao, ativo)
values ('TONTURA_DESEQUILIBRIO_SUBITO', 'TONTURA OU DESEQUILIBRIO SUBITO', 60, true)
on conflict ((upper(codigo))) do update
set descricao = excluded.descricao,
    ativo = excluded.ativo;

insert into avc_sinal_alerta (codigo, descricao, ordem_exibicao, ativo)
values ('REBAIXAMENTO_CONSCIENCIA', 'REBAIXAMENTO DO NIVEL DE CONSCIENCIA', 70, true)
on conflict ((upper(codigo))) do update
set descricao = excluded.descricao,
    ativo = excluded.ativo;

insert into avc_sinal_alerta (codigo, descricao, ordem_exibicao, ativo)
values ('TEMPO_INICIO_SINTOMAS', 'TEMPO DE INICIO DOS SINTOMAS DEFINIDO', 80, true)
on conflict ((upper(codigo))) do update
set descricao = excluded.descricao,
    ativo = excluded.ativo;
