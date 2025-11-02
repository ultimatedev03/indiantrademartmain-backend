-- V8: Remove legacy NOT NULL/foreign key buyer_category.category_id that conflicts with seeding
DO $$ BEGIN 
  ALTER TABLE buyer_category DROP CONSTRAINT IF EXISTS fk_buyer_category_category; 
EXCEPTION WHEN undefined_object THEN NULL; 
END $$;

ALTER TABLE IF EXISTS buyer_category DROP COLUMN IF EXISTS category_id;