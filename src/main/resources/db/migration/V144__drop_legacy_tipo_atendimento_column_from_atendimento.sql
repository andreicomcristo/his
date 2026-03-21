do $$
begin
    if exists (
        select 1
        from information_schema.columns
        where table_schema = 'public'
          and table_name = 'atendimento'
          and column_name = 'tipo_atendimento'
    ) then
        alter table atendimento
            drop column tipo_atendimento;
    end if;
end
$$;
