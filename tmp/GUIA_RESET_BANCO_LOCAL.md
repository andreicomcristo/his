# Guia rapido: resetar banco local do HIS

Este guia apaga o banco local e recria tudo do zero via Flyway.

## Antes de comecar

- Pare a aplicacao HIS.
- Confirme que voce esta no ambiente local (nao homolog/producao).
- O banco atual sera apagado.

## Opcao 1: PostgreSQL local (psql)

Com os parametros atuais do projeto:

- host: `localhost`
- porta: `5432`
- usuario: `postgres`
- senha: `i1o05s`
- banco HIS: `his`

Rode:

```bash
psql "postgresql://postgres:i1o05s@localhost:5432/postgres" -c "DROP DATABASE IF EXISTS his WITH (FORCE);"
psql "postgresql://postgres:i1o05s@localhost:5432/postgres" -c "CREATE DATABASE his;"
```

Depois inicie o HIS. O Flyway vai recriar schema e seeds automaticamente.

## Opcao 2: PostgreSQL em Docker Compose

Se seu banco estiver em container com volume:

```bash
docker compose down -v
docker compose up -d
```

Isso remove os volumes e sobe tudo limpo.

## Verificacao

Ao iniciar o HIS, valide no log:

- Flyway aplicando migrations
- Sem erro de validacao de migration
- Aplicacao subindo normalmente

## Observacao importante

O seed da atuacao adicional do admin depende de existir usuario com `username = admin`.
