-- V7: Full schema guard for critical entities used during bootstrap

-- Ensure buyer_products table exists with all required columns and FKs
CREATE TABLE IF NOT EXISTS buyer_products (
  id BIGSERIAL PRIMARY KEY
);
ALTER TABLE buyer_products
  ADD COLUMN IF NOT EXISTS name VARCHAR(255),
  ADD COLUMN IF NOT EXISTS description TEXT,
  ADD COLUMN IF NOT EXISTS price NUMERIC(12,2),
  ADD COLUMN IF NOT EXISTS original_price NUMERIC(12,2),
  ADD COLUMN IF NOT EXISTS brand VARCHAR(255),
  ADD COLUMN IF NOT EXISTS model VARCHAR(255),
  ADD COLUMN IF NOT EXISTS sku VARCHAR(255),
  ADD COLUMN IF NOT EXISTS category_id BIGINT,
  ADD COLUMN IF NOT EXISTS micro_category_id BIGINT,
  ADD COLUMN IF NOT EXISTS vendor_id BIGINT,
  ADD COLUMN IF NOT EXISTS stock INTEGER DEFAULT 0,
  ADD COLUMN IF NOT EXISTS min_order_quantity INTEGER DEFAULT 1,
  ADD COLUMN IF NOT EXISTS unit VARCHAR(50),
  ADD COLUMN IF NOT EXISTS image_urls VARCHAR(1000),
  ADD COLUMN IF NOT EXISTS specifications TEXT,
  ADD COLUMN IF NOT EXISTS meta_title VARCHAR(255),
  ADD COLUMN IF NOT EXISTS meta_description VARCHAR(500),
  ADD COLUMN IF NOT EXISTS tags VARCHAR(500),
  ADD COLUMN IF NOT EXISTS is_active BOOLEAN DEFAULT TRUE,
  ADD COLUMN IF NOT EXISTS is_approved BOOLEAN DEFAULT FALSE,
  ADD COLUMN IF NOT EXISTS is_featured BOOLEAN DEFAULT FALSE,
  ADD COLUMN IF NOT EXISTS selected_gst_number VARCHAR(50),
  ADD COLUMN IF NOT EXISTS gst_rate NUMERIC(5,2),
  ADD COLUMN IF NOT EXISTS view_count INTEGER DEFAULT 0,
  ADD COLUMN IF NOT EXISTS order_count INTEGER DEFAULT 0,
  ADD COLUMN IF NOT EXISTS weight NUMERIC(12,3),
  ADD COLUMN IF NOT EXISTS length NUMERIC(12,3),
  ADD COLUMN IF NOT EXISTS width NUMERIC(12,3),
  ADD COLUMN IF NOT EXISTS height NUMERIC(12,3),
  ADD COLUMN IF NOT EXISTS free_shipping BOOLEAN DEFAULT FALSE,
  ADD COLUMN IF NOT EXISTS shipping_charge NUMERIC(12,2),
  ADD COLUMN IF NOT EXISTS created_at TIMESTAMP NULL,
  ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP NULL;

DO $$ BEGIN
  ALTER TABLE buyer_products
    ADD CONSTRAINT fk_buyer_products_vendor
    FOREIGN KEY (vendor_id) REFERENCES legacy_vendors(id) ON DELETE CASCADE;
EXCEPTION WHEN duplicate_object THEN NULL; END $$;

DO $$ BEGIN
  ALTER TABLE buyer_products
    ADD CONSTRAINT fk_buyer_products_micro
    FOREIGN KEY (micro_category_id) REFERENCES buyer_micro_category(id) ON DELETE SET NULL;
EXCEPTION WHEN duplicate_object THEN NULL; END $$;

DO $$ BEGIN
  ALTER TABLE buyer_products
    ADD CONSTRAINT fk_buyer_products_category
    FOREIGN KEY (category_id) REFERENCES buyer_category(id) ON DELETE SET NULL;
EXCEPTION WHEN duplicate_object THEN NULL; END $$;

CREATE INDEX IF NOT EXISTS idx_buyer_products_vendor ON buyer_products(vendor_id);
CREATE INDEX IF NOT EXISTS idx_buyer_products_micro ON buyer_products(micro_category_id);
CREATE INDEX IF NOT EXISTS idx_buyer_products_category ON buyer_products(category_id);
CREATE INDEX IF NOT EXISTS idx_buyer_products_active ON buyer_products(is_active);
