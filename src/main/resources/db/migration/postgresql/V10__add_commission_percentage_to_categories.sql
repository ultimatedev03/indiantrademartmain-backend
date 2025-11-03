-- V10: Add commission_percentage column to categories table
-- Aligns with Category JPA entity

ALTER TABLE IF EXISTS categories
  ADD COLUMN IF NOT EXISTS commission_percentage DOUBLE PRECISION DEFAULT 0.0;

-- Update existing rows to have default value
UPDATE categories SET commission_percentage = 0.0 WHERE commission_percentage IS NULL;

-- Add helpful comment
COMMENT ON COLUMN categories.commission_percentage IS 'Commission percentage charged for products in this category';
