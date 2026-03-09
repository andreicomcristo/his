alter table atendimento
    add column if not exists usuario_criacao_id bigint;

alter table entrada
    add column if not exists atualizado_por_usuario_id bigint;

alter table atendimento_periodo
    add column if not exists usuario_inicio_id bigint,
    add column if not exists usuario_fim_id bigint;

alter table atendimento_evento
    add column if not exists usuario_id bigint;

alter table transferencia_externa
    add column if not exists usuario_solicitacao_id bigint,
    add column if not exists usuario_saida_id bigint,
    add column if not exists usuario_acolhimento_id bigint;

alter table observacao
    add column if not exists cancelado_por_usuario_id bigint;

alter table internacao
    add column if not exists cancelado_por_usuario_id bigint;

alter table classificacao_reavaliacao
    add column if not exists usuario_id bigint;

alter table paciente
    add column if not exists criado_por_usuario_id bigint,
    add column if not exists atualizado_por_usuario_id bigint;

alter table paciente_merge_log
    add column if not exists merged_por_usuario_id bigint;

update atendimento a
set usuario_criacao_id = (
    select uu.id
    from usuario uu
    where uu.keycloak_id = a.usuario_criacao
       or uu.username = a.usuario_criacao
    order by case when uu.keycloak_id = a.usuario_criacao then 0 else 1 end, uu.id
    limit 1
)
where a.usuario_criacao_id is null
  and a.usuario_criacao is not null
  and btrim(a.usuario_criacao) <> ''
  and exists (
      select 1
      from usuario uu
      where uu.keycloak_id = a.usuario_criacao
         or uu.username = a.usuario_criacao
  );

update entrada e
set atualizado_por_usuario_id = (
    select uu.id
    from usuario uu
    where uu.keycloak_id = e.atualizado_por
       or uu.username = e.atualizado_por
    order by case when uu.keycloak_id = e.atualizado_por then 0 else 1 end, uu.id
    limit 1
)
where e.atualizado_por_usuario_id is null
  and e.atualizado_por is not null
  and btrim(e.atualizado_por) <> ''
  and exists (
      select 1
      from usuario uu
      where uu.keycloak_id = e.atualizado_por
         or uu.username = e.atualizado_por
  );

update atendimento_periodo ap
set usuario_inicio_id = (
    select uu.id
    from usuario uu
    where uu.keycloak_id = ap.usuario_inicio
       or uu.username = ap.usuario_inicio
    order by case when uu.keycloak_id = ap.usuario_inicio then 0 else 1 end, uu.id
    limit 1
)
where ap.usuario_inicio_id is null
  and ap.usuario_inicio is not null
  and btrim(ap.usuario_inicio) <> ''
  and exists (
      select 1
      from usuario uu
      where uu.keycloak_id = ap.usuario_inicio
         or uu.username = ap.usuario_inicio
  );

update atendimento_periodo ap
set usuario_fim_id = (
    select uu.id
    from usuario uu
    where uu.keycloak_id = ap.usuario_fim
       or uu.username = ap.usuario_fim
    order by case when uu.keycloak_id = ap.usuario_fim then 0 else 1 end, uu.id
    limit 1
)
where ap.usuario_fim_id is null
  and ap.usuario_fim is not null
  and btrim(ap.usuario_fim) <> ''
  and exists (
      select 1
      from usuario uu
      where uu.keycloak_id = ap.usuario_fim
         or uu.username = ap.usuario_fim
  );

update atendimento_evento ae
set usuario_id = (
    select uu.id
    from usuario uu
    where uu.keycloak_id = ae.usuario
       or uu.username = ae.usuario
    order by case when uu.keycloak_id = ae.usuario then 0 else 1 end, uu.id
    limit 1
)
where ae.usuario_id is null
  and ae.usuario is not null
  and btrim(ae.usuario) <> ''
  and exists (
      select 1
      from usuario uu
      where uu.keycloak_id = ae.usuario
         or uu.username = ae.usuario
  );

update transferencia_externa te
set usuario_solicitacao_id = (
    select uu.id
    from usuario uu
    where uu.keycloak_id = te.usuario_solicitacao
       or uu.username = te.usuario_solicitacao
    order by case when uu.keycloak_id = te.usuario_solicitacao then 0 else 1 end, uu.id
    limit 1
)
where te.usuario_solicitacao_id is null
  and te.usuario_solicitacao is not null
  and btrim(te.usuario_solicitacao) <> ''
  and exists (
      select 1
      from usuario uu
      where uu.keycloak_id = te.usuario_solicitacao
         or uu.username = te.usuario_solicitacao
  );

update transferencia_externa te
set usuario_saida_id = (
    select uu.id
    from usuario uu
    where uu.keycloak_id = te.usuario_saida
       or uu.username = te.usuario_saida
    order by case when uu.keycloak_id = te.usuario_saida then 0 else 1 end, uu.id
    limit 1
)
where te.usuario_saida_id is null
  and te.usuario_saida is not null
  and btrim(te.usuario_saida) <> ''
  and exists (
      select 1
      from usuario uu
      where uu.keycloak_id = te.usuario_saida
         or uu.username = te.usuario_saida
  );

update transferencia_externa te
set usuario_acolhimento_id = (
    select uu.id
    from usuario uu
    where uu.keycloak_id = te.usuario_acolhimento
       or uu.username = te.usuario_acolhimento
    order by case when uu.keycloak_id = te.usuario_acolhimento then 0 else 1 end, uu.id
    limit 1
)
where te.usuario_acolhimento_id is null
  and te.usuario_acolhimento is not null
  and btrim(te.usuario_acolhimento) <> ''
  and exists (
      select 1
      from usuario uu
      where uu.keycloak_id = te.usuario_acolhimento
         or uu.username = te.usuario_acolhimento
  );

update observacao o
set cancelado_por_usuario_id = (
    select uu.id
    from usuario uu
    where uu.keycloak_id = o.cancelado_por
       or uu.username = o.cancelado_por
    order by case when uu.keycloak_id = o.cancelado_por then 0 else 1 end, uu.id
    limit 1
)
where o.cancelado_por_usuario_id is null
  and o.cancelado_por is not null
  and btrim(o.cancelado_por) <> ''
  and exists (
      select 1
      from usuario uu
      where uu.keycloak_id = o.cancelado_por
         or uu.username = o.cancelado_por
  );

update internacao i
set cancelado_por_usuario_id = (
    select uu.id
    from usuario uu
    where uu.keycloak_id = i.cancelado_por
       or uu.username = i.cancelado_por
    order by case when uu.keycloak_id = i.cancelado_por then 0 else 1 end, uu.id
    limit 1
)
where i.cancelado_por_usuario_id is null
  and i.cancelado_por is not null
  and btrim(i.cancelado_por) <> ''
  and exists (
      select 1
      from usuario uu
      where uu.keycloak_id = i.cancelado_por
         or uu.username = i.cancelado_por
  );

update classificacao_reavaliacao cr
set usuario_id = (
    select uu.id
    from usuario uu
    where uu.keycloak_id = cr.usuario
       or uu.username = cr.usuario
    order by case when uu.keycloak_id = cr.usuario then 0 else 1 end, uu.id
    limit 1
)
where cr.usuario_id is null
  and cr.usuario is not null
  and btrim(cr.usuario) <> ''
  and exists (
      select 1
      from usuario uu
      where uu.keycloak_id = cr.usuario
         or uu.username = cr.usuario
  );

update paciente p
set criado_por_usuario_id = (
    select uu.id
    from usuario uu
    where uu.keycloak_id = p.criado_por
       or uu.username = p.criado_por
    order by case when uu.keycloak_id = p.criado_por then 0 else 1 end, uu.id
    limit 1
)
where p.criado_por_usuario_id is null
  and p.criado_por is not null
  and btrim(p.criado_por) <> ''
  and exists (
      select 1
      from usuario uu
      where uu.keycloak_id = p.criado_por
         or uu.username = p.criado_por
  );

update paciente p
set atualizado_por_usuario_id = (
    select uu.id
    from usuario uu
    where uu.keycloak_id = p.atualizado_por
       or uu.username = p.atualizado_por
    order by case when uu.keycloak_id = p.atualizado_por then 0 else 1 end, uu.id
    limit 1
)
where p.atualizado_por_usuario_id is null
  and p.atualizado_por is not null
  and btrim(p.atualizado_por) <> ''
  and exists (
      select 1
      from usuario uu
      where uu.keycloak_id = p.atualizado_por
         or uu.username = p.atualizado_por
  );

update paciente_merge_log pml
set merged_por_usuario_id = (
    select uu.id
    from usuario uu
    where uu.keycloak_id = pml.merged_por
       or uu.username = pml.merged_por
    order by case when uu.keycloak_id = pml.merged_por then 0 else 1 end, uu.id
    limit 1
)
where pml.merged_por_usuario_id is null
  and pml.merged_por is not null
  and btrim(pml.merged_por) <> ''
  and exists (
      select 1
      from usuario uu
      where uu.keycloak_id = pml.merged_por
         or uu.username = pml.merged_por
  );

do $$
begin
    if not exists (select 1 from pg_constraint where conname = 'fk_atendimento_usuario_criacao_id') then
        alter table atendimento
            add constraint fk_atendimento_usuario_criacao_id
                foreign key (usuario_criacao_id) references usuario(id);
    end if;
end $$;

do $$
begin
    if not exists (select 1 from pg_constraint where conname = 'fk_entrada_atualizado_por_usuario_id') then
        alter table entrada
            add constraint fk_entrada_atualizado_por_usuario_id
                foreign key (atualizado_por_usuario_id) references usuario(id);
    end if;
end $$;

do $$
begin
    if not exists (select 1 from pg_constraint where conname = 'fk_atendimento_periodo_usuario_inicio_id') then
        alter table atendimento_periodo
            add constraint fk_atendimento_periodo_usuario_inicio_id
                foreign key (usuario_inicio_id) references usuario(id);
    end if;
end $$;

do $$
begin
    if not exists (select 1 from pg_constraint where conname = 'fk_atendimento_periodo_usuario_fim_id') then
        alter table atendimento_periodo
            add constraint fk_atendimento_periodo_usuario_fim_id
                foreign key (usuario_fim_id) references usuario(id);
    end if;
end $$;

do $$
begin
    if not exists (select 1 from pg_constraint where conname = 'fk_atendimento_evento_usuario_id') then
        alter table atendimento_evento
            add constraint fk_atendimento_evento_usuario_id
                foreign key (usuario_id) references usuario(id);
    end if;
end $$;

do $$
begin
    if not exists (select 1 from pg_constraint where conname = 'fk_transferencia_externa_usuario_solicitacao_id') then
        alter table transferencia_externa
            add constraint fk_transferencia_externa_usuario_solicitacao_id
                foreign key (usuario_solicitacao_id) references usuario(id);
    end if;
end $$;

do $$
begin
    if not exists (select 1 from pg_constraint where conname = 'fk_transferencia_externa_usuario_saida_id') then
        alter table transferencia_externa
            add constraint fk_transferencia_externa_usuario_saida_id
                foreign key (usuario_saida_id) references usuario(id);
    end if;
end $$;

do $$
begin
    if not exists (select 1 from pg_constraint where conname = 'fk_transferencia_externa_usuario_acolhimento_id') then
        alter table transferencia_externa
            add constraint fk_transferencia_externa_usuario_acolhimento_id
                foreign key (usuario_acolhimento_id) references usuario(id);
    end if;
end $$;

do $$
begin
    if not exists (select 1 from pg_constraint where conname = 'fk_observacao_cancelado_por_usuario_id') then
        alter table observacao
            add constraint fk_observacao_cancelado_por_usuario_id
                foreign key (cancelado_por_usuario_id) references usuario(id);
    end if;
end $$;

do $$
begin
    if not exists (select 1 from pg_constraint where conname = 'fk_internacao_cancelado_por_usuario_id') then
        alter table internacao
            add constraint fk_internacao_cancelado_por_usuario_id
                foreign key (cancelado_por_usuario_id) references usuario(id);
    end if;
end $$;

do $$
begin
    if not exists (select 1 from pg_constraint where conname = 'fk_classificacao_reavaliacao_usuario_id') then
        alter table classificacao_reavaliacao
            add constraint fk_classificacao_reavaliacao_usuario_id
                foreign key (usuario_id) references usuario(id);
    end if;
end $$;

do $$
begin
    if not exists (select 1 from pg_constraint where conname = 'fk_paciente_criado_por_usuario_id') then
        alter table paciente
            add constraint fk_paciente_criado_por_usuario_id
                foreign key (criado_por_usuario_id) references usuario(id);
    end if;
end $$;

do $$
begin
    if not exists (select 1 from pg_constraint where conname = 'fk_paciente_atualizado_por_usuario_id') then
        alter table paciente
            add constraint fk_paciente_atualizado_por_usuario_id
                foreign key (atualizado_por_usuario_id) references usuario(id);
    end if;
end $$;

do $$
begin
    if not exists (select 1 from pg_constraint where conname = 'fk_paciente_merge_log_merged_por_usuario_id') then
        alter table paciente_merge_log
            add constraint fk_paciente_merge_log_merged_por_usuario_id
                foreign key (merged_por_usuario_id) references usuario(id);
    end if;
end $$;

create index if not exists idx_atendimento_usuario_criacao_id
    on atendimento (usuario_criacao_id);

create index if not exists idx_entrada_atualizado_por_usuario_id
    on entrada (atualizado_por_usuario_id);

create index if not exists idx_atendimento_periodo_usuario_inicio_id
    on atendimento_periodo (usuario_inicio_id);

create index if not exists idx_atendimento_periodo_usuario_fim_id
    on atendimento_periodo (usuario_fim_id);

create index if not exists idx_atendimento_evento_usuario_id
    on atendimento_evento (usuario_id);

create index if not exists idx_transferencia_externa_usuario_solicitacao_id
    on transferencia_externa (usuario_solicitacao_id);

create index if not exists idx_transferencia_externa_usuario_saida_id
    on transferencia_externa (usuario_saida_id);

create index if not exists idx_transferencia_externa_usuario_acolhimento_id
    on transferencia_externa (usuario_acolhimento_id);

create index if not exists idx_observacao_cancelado_por_usuario_id
    on observacao (cancelado_por_usuario_id);

create index if not exists idx_internacao_cancelado_por_usuario_id
    on internacao (cancelado_por_usuario_id);

create index if not exists idx_classificacao_reavaliacao_usuario_id
    on classificacao_reavaliacao (usuario_id);

create index if not exists idx_paciente_criado_por_usuario_id
    on paciente (criado_por_usuario_id);

create index if not exists idx_paciente_atualizado_por_usuario_id
    on paciente (atualizado_por_usuario_id);

create index if not exists idx_paciente_merge_log_merged_por_usuario_id
    on paciente_merge_log (merged_por_usuario_id);
