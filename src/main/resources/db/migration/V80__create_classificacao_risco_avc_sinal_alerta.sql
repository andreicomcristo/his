create table if not exists classificacao_risco_avc_sinal_alerta (
    id bigserial primary key,
    classificacao_risco_id bigint not null,
    avc_sinal_alerta_id bigint not null,
    constraint uk_classificacao_risco_avc_sinal_alerta unique (classificacao_risco_id, avc_sinal_alerta_id),
    constraint fk_classificacao_risco_avc foreign key (classificacao_risco_id) references classificacao_risco (id) on delete cascade,
    constraint fk_classificacao_avc_sinal foreign key (avc_sinal_alerta_id) references avc_sinal_alerta (id)
);
