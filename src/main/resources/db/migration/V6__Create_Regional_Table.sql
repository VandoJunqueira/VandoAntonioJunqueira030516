CREATE TABLE regionais (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(200),
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    external_id INTEGER
);