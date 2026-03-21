alter table classificacao_risco
    add column if not exists area_execucao_id bigint;

do $$
begin
    if not exists (
        select 1
        from pg_constraint
        where conname = 'fk_classificacao_risco_area_execucao'
    ) then
        alter table classificacao_risco
            add constraint fk_classificacao_risco_area_execucao
                foreign key (area_execucao_id) references area (id);
    end if;
end
$$;

create index if not exists idx_classificacao_risco_area_execucao
    on classificacao_risco (area_execucao_id);
