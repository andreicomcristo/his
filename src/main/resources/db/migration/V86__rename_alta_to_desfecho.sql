alter table if exists tipo_alta rename to tipo_desfecho;
alter table if exists motivo_alta rename to motivo_desfecho;
alter table if exists alta rename to desfecho;

do $$
begin
    if exists (
        select 1
        from information_schema.columns
        where table_schema = 'public'
          and table_name = 'desfecho'
          and column_name = 'tipo_alta_id'
    ) then
        alter table desfecho rename column tipo_alta_id to tipo_desfecho_id;
    end if;
end
$$;

do $$
begin
    if exists (
        select 1
        from information_schema.columns
        where table_schema = 'public'
          and table_name = 'desfecho'
          and column_name = 'motivo_alta_id'
    ) then
        alter table desfecho rename column motivo_alta_id to motivo_desfecho_id;
    end if;
end
$$;

do $$
begin
    if exists (
        select 1
        from pg_constraint
        where conname = 'fk_alta_tipo_alta'
          and conrelid = to_regclass('public.desfecho')
    ) then
        alter table desfecho rename constraint fk_alta_tipo_alta to fk_desfecho_tipo_desfecho;
    end if;
end
$$;

do $$
begin
    if exists (
        select 1
        from pg_constraint
        where conname = 'fk_alta_motivo_alta'
          and conrelid = to_regclass('public.desfecho')
    ) then
        alter table desfecho rename constraint fk_alta_motivo_alta to fk_desfecho_motivo_desfecho;
    end if;
end
$$;
