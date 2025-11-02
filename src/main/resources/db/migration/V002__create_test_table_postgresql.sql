-- PostgreSQL compatible test migration
-- This replaces V001 with proper PostgreSQL syntax

CREATE TABLE IF NOT EXISTS flyway_test_pg (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create index (PostgreSQL syntax)
CREATE INDEX IF NOT EXISTS idx_flyway_test_pg_name ON flyway_test_pg (name);

-- Insert test data
INSERT INTO flyway_test_pg (name) VALUES ('PostgreSQL Flyway Migration Test') 
ON CONFLICT DO NOTHING;

-- Add comment to table
COMMENT ON TABLE flyway_test_pg IS 'PostgreSQL test table to verify Flyway migration';
