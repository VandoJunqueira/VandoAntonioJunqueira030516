CREATE TABLE regionais (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(200),
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    external_id INTEGER
);