alter table unidade
    add column if not exists sigla varchar(20);

create index if not exists idx_unidade_sigla
    on unidade (sigla);
