-- ============================================================
-- V7: Create Users table for Authentication and Role-Based Access
-- ============================================================

CREATE TABLE users (
    id              BIGSERIAL     PRIMARY KEY,
    email           VARCHAR(255)  NOT NULL UNIQUE,
    password_hash   VARCHAR(255)  NOT NULL,
    full_name       VARCHAR(150)  NOT NULL,
    role            VARCHAR(50)   NOT NULL,
    seller_id       BIGINT        REFERENCES sellers(id) ON DELETE SET NULL,
    created_at      TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP     NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_user_role CHECK (role IN ('BUYER', 'SELLER'))
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_seller_id ON users(seller_id);
