alter table forma_chegada
    add column if not exists perfil_chegada varchar(20);

update forma_chegada
set perfil_chegada = case
    when upper(trim(descricao)) in (
        'BOMBEIROS',
        'TRANSPORTE MUNICIPAL',
        'SAMU TERRESTRE',
        'AMBULANCIA MUNICIPAL',
        'AMBULANCIA',
        'POLICIA MILITAR',
        'POLICIA PENAL',
        'POLICIA CIVIL',
        'SAMU AEREO',
        'TRANSPORTE DA ALDEIA'
    ) then 'HORIZONTAL'
    else 'VERTICAL'
end
where perfil_chegada is null;

alter table forma_chegada
    alter column perfil_chegada set default 'VERTICAL';

alter table forma_chegada
    alter column perfil_chegada set not null;

do $$
begin
    if not exists (
        select 1
        from pg_constraint
        where conname = 'ck_forma_chegada_perfil'
    ) then
        alter table forma_chegada
            add constraint ck_forma_chegada_perfil
                check (upper(perfil_chegada) in ('HORIZONTAL', 'VERTICAL'));
    end if;
end $$;
