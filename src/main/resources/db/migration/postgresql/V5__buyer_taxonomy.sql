-- V5: Buyer taxonomy tables to match JPA entities (idempotent and additive)

-- Ensure buyer_category exists and has all required columns
CREATE TABLE IF NOT EXISTS buyer_category (
    id BIGSERIAL PRIMARY KEY
);
ALTER TABLE buyer_category
  ADD COLUMN IF NOT EXISTS name VARCHAR(255),
  ADD COLUMN IF NOT EXISTS description TEXT,
  ADD COLUMN IF NOT EXISTS display_order INTEGER DEFAULT 0,
  ADD COLUMN IF NOT EXISTS is_active BOOLEAN DEFAULT TRUE,
  ADD COLUMN IF NOT EXISTS meta_title VARCHAR(255),
  ADD COLUMN IF NOT EXISTS meta_description VARCHAR(500),
  ADD COLUMN IF NOT EXISTS slug VARCHAR(255),
  ADD COLUMN IF NOT EXISTS created_at TIMESTAMP NULL,
  ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP NULL;

CREATE INDEX IF NOT EXISTS idx_buyer_category_active ON buyer_category(is_active);
CREATE INDEX IF NOT EXISTS idx_buyer_category_order ON buyer_category(display_order);
CREATE UNIQUE INDEX IF NOT EXISTS uq_buyer_category_slug ON buyer_category(slug);

-- Ensure sub_categories exists and has all required columns
CREATE TABLE IF NOT EXISTS sub_categories (
    id BIGSERIAL PRIMARY KEY
);
ALTER TABLE sub_categories
  ADD COLUMN IF NOT EXISTS name VARCHAR(255),
  ADD COLUMN IF NOT EXISTS description TEXT,
  ADD COLUMN IF NOT EXISTS display_order INTEGER DEFAULT 0,
  ADD COLUMN IF NOT EXISTS is_active BOOLEAN DEFAULT TRUE,
  ADD COLUMN IF NOT EXISTS meta_title VARCHAR(255),
  ADD COLUMN IF NOT EXISTS meta_description VARCHAR(500),
  ADD COLUMN IF NOT EXISTS slug VARCHAR(255),
  ADD COLUMN IF NOT EXISTS created_at TIMESTAMP NULL,
  ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP NULL,
  ADD COLUMN IF NOT EXISTS category_id BIGINT;

DO $$ BEGIN
  ALTER TABLE sub_categories
    ADD CONSTRAINT fk_sub_categories_category
    FOREIGN KEY (category_id) REFERENCES buyer_category(id) ON DELETE CASCADE;
EXCEPTION WHEN duplicate_object THEN NULL; END $$;

CREATE INDEX IF NOT EXISTS idx_sub_categories_category ON sub_categories(category_id);
CREATE INDEX IF NOT EXISTS idx_sub_categories_active ON sub_categories(is_active);

-- Ensure buyer_micro_category exists and has all required columns
CREATE TABLE IF NOT EXISTS buyer_micro_category (
    id BIGSERIAL PRIMARY KEY
);
ALTER TABLE buyer_micro_category
  ADD COLUMN IF NOT EXISTS name VARCHAR(255),
  ADD COLUMN IF NOT EXISTS description TEXT,
  ADD COLUMN IF NOT EXISTS display_order INTEGER DEFAULT 0,
  ADD COLUMN IF NOT EXISTS is_active BOOLEAN DEFAULT TRUE,
  ADD COLUMN IF NOT EXISTS meta_title VARCHAR(255),
  ADD COLUMN IF NOT EXISTS meta_description VARCHAR(500),
  ADD COLUMN IF NOT EXISTS slug VARCHAR(255),
  ADD COLUMN IF NOT EXISTS created_at TIMESTAMP NULL,
  ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP NULL,
  ADD COLUMN IF NOT EXISTS sub_category_id BIGINT;

DO $$ BEGIN
  ALTER TABLE buyer_micro_category
    ADD CONSTRAINT fk_buyer_micro_category_sub
    FOREIGN KEY (sub_category_id) REFERENCES sub_categories(id) ON DELETE CASCADE;
EXCEPTION WHEN duplicate_object THEN NULL; END $$;

CREATE INDEX IF NOT EXISTS idx_buyer_micro_category_sub ON buyer_micro_category(sub_category_id);
CREATE INDEX IF NOT EXISTS idx_buyer_micro_category_active ON buyer_micro_category(is_active);
