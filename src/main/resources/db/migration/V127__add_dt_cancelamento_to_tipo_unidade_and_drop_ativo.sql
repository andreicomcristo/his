alter table tipo_unidade
    add column if not exists dt_cancelamento timestamp;

update tipo_unidade
set dt_cancelamento = now()
where ativo = false
  and dt_cancelamento is null;

alter table tipo_unidade
    drop column if exists ativo;
