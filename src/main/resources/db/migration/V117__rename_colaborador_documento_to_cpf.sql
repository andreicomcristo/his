do $$
begin
    if exists (
        select 1
        from information_schema.columns
        where table_schema = 'public'
          and table_name = 'colaborador'
          and column_name = 'documento'
    ) and not exists (
        select 1
        from information_schema.columns
        where table_schema = 'public'
          and table_name = 'colaborador'
          and column_name = 'cpf'
    ) then
        alter table colaborador
            rename column documento to cpf;
    end if;
end $$;

drop index if exists uq_colaborador_documento_not_null;

create unique index if not exists uq_colaborador_cpf_not_null
    on colaborador (cpf)
    where cpf is not null and btrim(cpf) <> '';
