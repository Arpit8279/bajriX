CREATE TABLE categories (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(100)  NOT NULL,
    slug        VARCHAR(120)  NOT NULL UNIQUE,
    created_at  TIMESTAMP     NOT NULL DEFAULT NOW()
);
