-- V9: Align categories table with JPA entity used by modules.category
-- Idempotent, safe on existing data

-- Add missing columns expected by JPA entity
ALTER TABLE IF EXISTS categories
  ADD COLUMN IF NOT EXISTS display_order INTEGER DEFAULT 0,
  ADD COLUMN IF NOT EXISTS category_level INTEGER DEFAULT 0,
  ADD COLUMN IF NOT EXISTS visible_to_vendors BOOLEAN DEFAULT TRUE,
  ADD COLUMN IF NOT EXISTS visible_to_customers BOOLEAN DEFAULT TRUE,
  ADD COLUMN IF NOT EXISTS icon_url VARCHAR(500),
  ADD COLUMN IF NOT EXISTS meta_title VARCHAR(255),
  ADD COLUMN IF NOT EXISTS meta_description VARCHAR(500),
  ADD COLUMN IF NOT EXISTS created_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  ADD COLUMN IF NOT EXISTS parent_category_id BIGINT,
  ADD COLUMN IF NOT EXISTS created_by_employee_id BIGINT;

-- Foreign keys (ignore if already exist)
DO $$ BEGIN
  ALTER TABLE categories
    ADD CONSTRAINT fk_categories_parent_category
    FOREIGN KEY (parent_category_id) REFERENCES categories(id) ON DELETE SET NULL;
EXCEPTION WHEN duplicate_object THEN NULL; END $$;

DO $$ BEGIN
  ALTER TABLE categories
    ADD CONSTRAINT fk_categories_created_by_employee
    FOREIGN KEY (created_by_employee_id) REFERENCES users(id) ON DELETE SET NULL;
EXCEPTION WHEN duplicate_object THEN NULL; END $$;

-- Helpful indexes
CREATE INDEX IF NOT EXISTS idx_categories_display_order ON categories(display_order);
CREATE INDEX IF NOT EXISTS idx_categories_visible_vendor ON categories(visible_to_vendors);
CREATE INDEX IF NOT EXISTS idx_categories_visible_customer ON categories(visible_to_customers);
CREATE INDEX IF NOT EXISTS idx_categories_category_level ON categories(category_level);
