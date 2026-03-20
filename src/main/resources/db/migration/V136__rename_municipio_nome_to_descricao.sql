do $$
begin
    if exists (
        select 1
        from information_schema.columns
        where table_name = 'municipio'
          and column_name = 'nome'
    ) and not exists (
        select 1
        from information_schema.columns
        where table_name = 'municipio'
          and column_name = 'descricao'
    ) then
        execute 'alter table municipio rename column nome to descricao';
    end if;
end $$;

alter index if exists idx_municipio_nome
    rename to idx_municipio_descricao;

do $$
begin
    if exists (
        select 1
        from pg_constraint
        where conname = 'uq_municipio_nome_uf'
    ) and not exists (
        select 1
        from pg_constraint
        where conname = 'uq_municipio_descricao_uf'
    ) then
        alter table municipio
            rename constraint uq_municipio_nome_uf to uq_municipio_descricao_uf;
    end if;
end $$;
