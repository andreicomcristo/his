create table if not exists classificacao_sinais_vitais (
    id bigserial primary key,
    pressao_arterial varchar(20),
    temperatura numeric(5,2),
    frequencia_cardiaca integer,
    saturacao_o2 integer,
    frequencia_respiratoria integer,
    saturacao_o2_com_terapia_o2 integer,
    saturacao_o2_aa integer,
    glicemia_capilar numeric(6,2),
    peso_kg numeric(6,2),
    altura_cm numeric(6,2),
    hgt integer,
    perfusao_capilar_periferica_seg integer,
    preenchimento_capilar_central_seg integer
);

insert into classificacao_sinais_vitais (
    id,
    pressao_arterial,
    temperatura,
    frequencia_cardiaca,
    saturacao_o2,
    frequencia_respiratoria,
    saturacao_o2_com_terapia_o2,
    saturacao_o2_aa,
    glicemia_capilar,
    peso_kg,
    altura_cm,
    hgt,
    perfusao_capilar_periferica_seg,
    preenchimento_capilar_central_seg
)
select
    cr.id,
    cr.pressao_arterial,
    cr.temperatura,
    cr.frequencia_cardiaca,
    cr.saturacao_o2,
    cr.frequencia_respiratoria,
    cr.saturacao_o2_com_terapia_o2,
    cr.saturacao_o2_aa,
    cr.glicemia_capilar,
    cr.peso_kg,
    cr.altura_cm,
    cr.hgt,
    cr.perfusao_capilar_periferica_seg,
    cr.preenchimento_capilar_central_seg
from classificacao_risco cr
where not exists (
    select 1 from classificacao_sinais_vitais csv where csv.id = cr.id
);

select setval(
    pg_get_serial_sequence('classificacao_sinais_vitais', 'id'),
    coalesce((select max(id) from classificacao_sinais_vitais), 1),
    true
);

alter table classificacao_risco
    add column if not exists classificacao_sinais_vitais_id bigint;

update classificacao_risco
set classificacao_sinais_vitais_id = id
where classificacao_sinais_vitais_id is null;

do $$
begin
    if not exists (
        select 1
        from pg_constraint
        where conname = 'fk_classificacao_risco_sinais_vitais'
    ) then
        alter table classificacao_risco
            add constraint fk_classificacao_risco_sinais_vitais
                foreign key (classificacao_sinais_vitais_id) references classificacao_sinais_vitais (id);
    end if;
end $$;

alter table classificacao_risco
    drop column if exists pressao_arterial,
    drop column if exists temperatura,
    drop column if exists frequencia_cardiaca,
    drop column if exists saturacao_o2,
    drop column if exists frequencia_respiratoria,
    drop column if exists saturacao_o2_com_terapia_o2,
    drop column if exists saturacao_o2_aa,
    drop column if exists glicemia_capilar,
    drop column if exists peso_kg,
    drop column if exists altura_cm,
    drop column if exists hgt,
    drop column if exists perfusao_capilar_periferica_seg,
    drop column if exists preenchimento_capilar_central_seg;
