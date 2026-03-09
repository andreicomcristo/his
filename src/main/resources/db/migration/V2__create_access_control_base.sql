CREATE TABLE unidade (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    tipo_estabelecimento VARCHAR(80),
    cnes VARCHAR(20),
    ativo BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE UNIQUE INDEX uq_unidade_cnes_not_null
    ON unidade (cnes)
    WHERE cnes IS NOT NULL AND BTRIM(cnes) <> '';

CREATE TABLE usuario (
    id BIGSERIAL PRIMARY KEY,
    keycloak_id VARCHAR(120) NOT NULL,
    username VARCHAR(120) NOT NULL,
    email VARCHAR(180),
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT uq_usuario_keycloak_id UNIQUE (keycloak_id),
    CONSTRAINT uq_usuario_username UNIQUE (username)
);

CREATE TABLE perfil (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(80) NOT NULL,
    CONSTRAINT uq_perfil_nome UNIQUE (nome)
);

CREATE TABLE permissao (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(80) NOT NULL,
    CONSTRAINT uq_permissao_nome UNIQUE (nome)
);

CREATE TABLE perfil_permissao (
    perfil_id BIGINT NOT NULL,
    permissao_id BIGINT NOT NULL,
    CONSTRAINT pk_perfil_permissao PRIMARY KEY (perfil_id, permissao_id),
    CONSTRAINT fk_perfil_permissao_perfil FOREIGN KEY (perfil_id) REFERENCES perfil (id),
    CONSTRAINT fk_perfil_permissao_permissao FOREIGN KEY (permissao_id) REFERENCES permissao (id)
);

CREATE TABLE usuario_unidade_perfil (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    unidade_id BIGINT NOT NULL,
    perfil_id BIGINT NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_uup_usuario FOREIGN KEY (usuario_id) REFERENCES usuario (id),
    CONSTRAINT fk_uup_unidade FOREIGN KEY (unidade_id) REFERENCES unidade (id),
    CONSTRAINT fk_uup_perfil FOREIGN KEY (perfil_id) REFERENCES perfil (id),
    CONSTRAINT uq_uup_tripla UNIQUE (usuario_id, unidade_id, perfil_id)
);
