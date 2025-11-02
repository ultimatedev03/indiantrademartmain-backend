-- Simple test migration to verify Flyway is working
-- PostgreSQL compatible version

CREATE TABLE flyway_test (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create index (PostgreSQL syntax)
CREATE INDEX idx_flyway_test_name ON flyway_test (name);

-- Insert test data
INSERT INTO flyway_test (name) VALUES ('Flyway Migration Test');

-- Add comment to table
COMMENT ON TABLE flyway_test IS 'Test table to verify Flyway migration';
