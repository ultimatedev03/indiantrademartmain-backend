-- V6: Compatibility patch for existing data (align with JPA and sample seeding)

-- buyer_category.user_id may exist and be NOT NULL on older schemas; make it nullable and add FK safely
ALTER TABLE IF EXISTS buyer_category
  ADD COLUMN IF NOT EXISTS user_id BIGINT;

DO $$ BEGIN
  ALTER TABLE buyer_category ALTER COLUMN user_id DROP NOT NULL;
EXCEPTION WHEN undefined_column THEN NULL; END $$;

DO $$ BEGIN
  ALTER TABLE buyer_category
    ADD CONSTRAINT fk_buyer_category_user
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL;
EXCEPTION WHEN duplicate_object THEN NULL; END $$;

-- Ensure created_at/updated_at defaults are not required during inserts
DO $$ BEGIN
  ALTER TABLE buyer_category ALTER COLUMN created_at DROP NOT NULL;
  ALTER TABLE buyer_category ALTER COLUMN updated_at DROP NOT NULL;
EXCEPTION WHEN undefined_column THEN NULL; END $$;
