create table if not exists destino_rede (
    id bigserial primary key,
    descricao varchar(150) not null,
    ativo boolean not null default true
);

create unique index if not exists ux_destino_rede_descricao_upper
    on destino_rede (upper(descricao));

alter table desfecho
    add column if not exists destino_rede_id bigint;

do $$
begin
    if not exists (
        select 1
        from pg_constraint
        where conname = 'fk_desfecho_destino_rede'
          and conrelid = to_regclass('public.desfecho')
    ) then
        alter table desfecho
            add constraint fk_desfecho_destino_rede
                foreign key (destino_rede_id) references destino_rede (id);
    end if;
end
$$;

create index if not exists idx_desfecho_destino_rede
    on desfecho (destino_rede_id);
