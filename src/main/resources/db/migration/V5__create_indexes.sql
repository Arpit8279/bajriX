-- Enable trigram extension for ILIKE search performance
CREATE EXTENSION IF NOT EXISTS pg_trgm;

-- Product search (name/brand pattern matching)
CREATE INDEX idx_products_name_trgm   ON products USING gin (name gin_trgm_ops);
CREATE INDEX idx_products_brand       ON products (brand);
CREATE INDEX idx_products_category_id ON products (category_id);

-- Listing lookups
CREATE INDEX idx_listings_product_id  ON seller_listings (product_id);
CREATE INDEX idx_listings_seller_id   ON seller_listings (seller_id);
CREATE INDEX idx_listings_price       ON seller_listings (price);
CREATE INDEX idx_listings_available   ON seller_listings (available);

-- Seller status filtering
CREATE INDEX idx_sellers_status       ON sellers (status);
