CREATE TABLE seller_listings (
    id                  BIGSERIAL     PRIMARY KEY,
    product_id          BIGINT        NOT NULL REFERENCES products(id),
    seller_id           BIGINT        NOT NULL REFERENCES sellers(id),
    price               DECIMAL(12,2) NOT NULL,
    stock_quantity      INTEGER       NOT NULL DEFAULT 0,
    min_order_quantity  INTEGER       NOT NULL DEFAULT 1,
    available           BOOLEAN       NOT NULL DEFAULT TRUE,
    version             INTEGER       NOT NULL DEFAULT 0,
    created_at          TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP     NOT NULL DEFAULT NOW(),

    CONSTRAINT uk_seller_product       UNIQUE (seller_id, product_id),
    CONSTRAINT chk_price_positive      CHECK  (price > 0),
    CONSTRAINT chk_stock_non_negative  CHECK  (stock_quantity >= 0),
    CONSTRAINT chk_moq_positive        CHECK  (min_order_quantity >= 1)
);
