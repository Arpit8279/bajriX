CREATE TABLE sellers (
    id              BIGSERIAL     PRIMARY KEY,
    business_name   VARCHAR(200)  NOT NULL,
    slug            VARCHAR(220)  NOT NULL UNIQUE,
    contact_email   VARCHAR(255),
    contact_phone   VARCHAR(20),
    address         TEXT,
    city            VARCHAR(100),
    status          VARCHAR(20)   NOT NULL DEFAULT 'PENDING',
    created_at      TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP     NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_seller_status CHECK (status IN ('APPROVED', 'PENDING', 'REJECTED'))
);
