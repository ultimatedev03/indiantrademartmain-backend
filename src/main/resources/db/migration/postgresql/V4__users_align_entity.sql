-- V4: align users table with application entity

-- Switch role to VARCHAR to avoid enum mismatch
DO $$ BEGIN
  ALTER TABLE users ALTER COLUMN role TYPE VARCHAR(50) USING role::text;
EXCEPTION WHEN undefined_table THEN
  -- users table may not exist (safety)
  NULL;
END $$;

-- Add missing columns if not exist
ALTER TABLE IF EXISTS users
  ADD COLUMN IF NOT EXISTS name VARCHAR(255),
  ADD COLUMN IF NOT EXISTS is_verified BOOLEAN DEFAULT FALSE,
  ADD COLUMN IF NOT EXISTS is_active BOOLEAN DEFAULT TRUE,
  ADD COLUMN IF NOT EXISTS profile_image_url VARCHAR(500),
  ADD COLUMN IF NOT EXISTS address VARCHAR(500),
  ADD COLUMN IF NOT EXISTS city VARCHAR(100),
  ADD COLUMN IF NOT EXISTS state VARCHAR(100),
  ADD COLUMN IF NOT EXISTS pincode VARCHAR(10),
  ADD COLUMN IF NOT EXISTS country VARCHAR(100) DEFAULT 'India';
