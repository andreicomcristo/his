create table if not exists classificacao_oxigenacao (
    id bigserial primary key,
    saturacao_o2_com_terapia_o2 integer,
    saturacao_o2_aa integer
);

create table if not exists classificacao_glicemia (
    id bigserial primary key,
    glicemia_capilar numeric(6,2),
    hgt integer
);

create table if not exists classificacao_antropometria (
    id bigserial primary key,
    peso_kg numeric(6,2),
    altura_cm numeric(6,2)
);

create table if not exists classificacao_perfusao (
    id bigserial primary key,
    perfusao_capilar_periferica_seg integer,
    preenchimento_capilar_central_seg integer
);

insert into classificacao_oxigenacao (id, saturacao_o2_com_terapia_o2, saturacao_o2_aa)
select
    csv.id,
    csv.saturacao_o2_com_terapia_o2,
    csv.saturacao_o2_aa
from classificacao_sinais_vitais csv
where not exists (
    select 1
    from classificacao_oxigenacao co
    where co.id = csv.id
);

insert into classificacao_glicemia (id, glicemia_capilar, hgt)
select
    csv.id,
    csv.glicemia_capilar,
    csv.hgt
from classificacao_sinais_vitais csv
where not exists (
    select 1
    from classificacao_glicemia cg
    where cg.id = csv.id
);

insert into classificacao_antropometria (id, peso_kg, altura_cm)
select
    csv.id,
    csv.peso_kg,
    csv.altura_cm
from classificacao_sinais_vitais csv
where not exists (
    select 1
    from classificacao_antropometria ca
    where ca.id = csv.id
);

insert into classificacao_perfusao (id, perfusao_capilar_periferica_seg, preenchimento_capilar_central_seg)
select
    csv.id,
    csv.perfusao_capilar_periferica_seg,
    csv.preenchimento_capilar_central_seg
from classificacao_sinais_vitais csv
where not exists (
    select 1
    from classificacao_perfusao cp
    where cp.id = csv.id
);

select setval(
    pg_get_serial_sequence('classificacao_oxigenacao', 'id'),
    coalesce((select max(id) from classificacao_oxigenacao), 1),
    true
);

select setval(
    pg_get_serial_sequence('classificacao_glicemia', 'id'),
    coalesce((select max(id) from classificacao_glicemia), 1),
    true
);

select setval(
    pg_get_serial_sequence('classificacao_antropometria', 'id'),
    coalesce((select max(id) from classificacao_antropometria), 1),
    true
);

select setval(
    pg_get_serial_sequence('classificacao_perfusao', 'id'),
    coalesce((select max(id) from classificacao_perfusao), 1),
    true
);

alter table classificacao_risco
    add column if not exists classificacao_oxigenacao_id bigint,
    add column if not exists classificacao_glicemia_id bigint,
    add column if not exists classificacao_antropometria_id bigint,
    add column if not exists classificacao_perfusao_id bigint;

update classificacao_risco
set classificacao_oxigenacao_id = classificacao_sinais_vitais_id
where classificacao_oxigenacao_id is null
  and classificacao_sinais_vitais_id is not null;

update classificacao_risco
set classificacao_glicemia_id = classificacao_sinais_vitais_id
where classificacao_glicemia_id is null
  and classificacao_sinais_vitais_id is not null;

update classificacao_risco
set classificacao_antropometria_id = classificacao_sinais_vitais_id
where classificacao_antropometria_id is null
  and classificacao_sinais_vitais_id is not null;

update classificacao_risco
set classificacao_perfusao_id = classificacao_sinais_vitais_id
where classificacao_perfusao_id is null
  and classificacao_sinais_vitais_id is not null;

create index if not exists idx_classificacao_risco_oxigenacao_id
    on classificacao_risco (classificacao_oxigenacao_id);

create index if not exists idx_classificacao_risco_glicemia_id
    on classificacao_risco (classificacao_glicemia_id);

create index if not exists idx_classificacao_risco_antropometria_id
    on classificacao_risco (classificacao_antropometria_id);

create index if not exists idx_classificacao_risco_perfusao_id
    on classificacao_risco (classificacao_perfusao_id);

do
$$
begin
    if not exists (
        select 1
        from pg_constraint
        where conname = 'fk_classificacao_risco_oxigenacao'
    ) then
        alter table classificacao_risco
            add constraint fk_classificacao_risco_oxigenacao
                foreign key (classificacao_oxigenacao_id) references classificacao_oxigenacao (id);
    end if;
end
$$;

do
$$
begin
    if not exists (
        select 1
        from pg_constraint
        where conname = 'fk_classificacao_risco_glicemia'
    ) then
        alter table classificacao_risco
            add constraint fk_classificacao_risco_glicemia
                foreign key (classificacao_glicemia_id) references classificacao_glicemia (id);
    end if;
end
$$;

do
$$
begin
    if not exists (
        select 1
        from pg_constraint
        where conname = 'fk_classificacao_risco_antropometria'
    ) then
        alter table classificacao_risco
            add constraint fk_classificacao_risco_antropometria
                foreign key (classificacao_antropometria_id) references classificacao_antropometria (id);
    end if;
end
$$;

do
$$
begin
    if not exists (
        select 1
        from pg_constraint
        where conname = 'fk_classificacao_risco_perfusao'
    ) then
        alter table classificacao_risco
            add constraint fk_classificacao_risco_perfusao
                foreign key (classificacao_perfusao_id) references classificacao_perfusao (id);
    end if;
end
$$;

alter table classificacao_sinais_vitais
    drop column if exists saturacao_o2_com_terapia_o2,
    drop column if exists saturacao_o2_aa,
    drop column if exists glicemia_capilar,
    drop column if exists peso_kg,
    drop column if exists altura_cm,
    drop column if exists hgt,
    drop column if exists perfusao_capilar_periferica_seg,
    drop column if exists preenchimento_capilar_central_seg;
