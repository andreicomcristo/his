alter table municipio
    add column if not exists dt_cancelamento timestamp;

do $$
begin
    if exists (
        select 1
        from information_schema.columns
        where table_name = 'municipio'
          and column_name = 'ativo'
    ) then
        execute 'update municipio set dt_cancelamento = now() where ativo = false and dt_cancelamento is null';
    end if;
end $$;

alter table municipio
    drop column if exists ativo;
