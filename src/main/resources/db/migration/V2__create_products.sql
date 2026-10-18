CREATE TABLE products (
    id            BIGSERIAL     PRIMARY KEY,
    name          VARCHAR(255)  NOT NULL,
    slug          VARCHAR(300)  NOT NULL UNIQUE,
    description   TEXT,
    brand         VARCHAR(100),
    unit          VARCHAR(50)   NOT NULL DEFAULT 'piece',
    category_id   BIGINT        REFERENCES categories(id),
    image_url     VARCHAR(500),
    active        BOOLEAN       NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMP     NOT NULL DEFAULT NOW()
);
