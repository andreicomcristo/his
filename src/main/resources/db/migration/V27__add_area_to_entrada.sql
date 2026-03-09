alter table entrada
    add column if not exists area_id bigint references area(id);

create index if not exists idx_entrada_area_id on entrada(area_id);
