do $$
begin
    if exists (
        select 1
        from information_schema.columns
        where table_name = 'bairro'
          and column_name = 'nome'
    ) and not exists (
        select 1
        from information_schema.columns
        where table_name = 'bairro'
          and column_name = 'descricao'
    ) then
        execute 'alter table bairro rename column nome to descricao';
    end if;
end $$;

alter index if exists idx_bairro_nome
    rename to idx_bairro_descricao;
