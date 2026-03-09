create table if not exists classificacao_risco_alergia (
    id bigserial primary key,
    classificacao_risco_id bigint not null,
    alergia_substancia_id bigint not null,
    alergia_severidade_id bigint,
    descricao varchar(500),
    constraint fk_classificacao_risco_alergia_classificacao
        foreign key (classificacao_risco_id) references classificacao_risco (id) on delete cascade,
    constraint fk_classificacao_risco_alergia_substancia
        foreign key (alergia_substancia_id) references alergia_substancia (id),
    constraint fk_classificacao_risco_alergia_severidade
        foreign key (alergia_severidade_id) references alergia_severidade (id)
);

create index if not exists idx_classificacao_risco_alergia_classificacao
    on classificacao_risco_alergia (classificacao_risco_id);

create table if not exists classificacao_risco_comorbidade (
    id bigserial primary key,
    classificacao_risco_id bigint not null,
    comorbidade_id bigint not null,
    constraint fk_classificacao_risco_comorbidade_classificacao
        foreign key (classificacao_risco_id) references classificacao_risco (id) on delete cascade,
    constraint fk_classificacao_risco_comorbidade_comorbidade
        foreign key (comorbidade_id) references comorbidade (id),
    constraint uk_classificacao_risco_comorbidade unique (classificacao_risco_id, comorbidade_id)
);

create index if not exists idx_classificacao_risco_comorbidade_classificacao
    on classificacao_risco_comorbidade (classificacao_risco_id);
