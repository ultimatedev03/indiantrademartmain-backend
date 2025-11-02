-- V3: legacy_vendors table to support DataInitializationService

CREATE TABLE IF NOT EXISTS legacy_vendors (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(50) UNIQUE,
    password VARCHAR(255) NOT NULL,
    verified BOOLEAN DEFAULT FALSE,
    role VARCHAR(50) DEFAULT 'ROLE_VENDOR',
    vendor_type VARCHAR(50) DEFAULT 'BASIC',

    business_name VARCHAR(255),
    business_address VARCHAR(500),
    city VARCHAR(100),
    state VARCHAR(100),
    pincode VARCHAR(20),
    gst_number VARCHAR(50),
    pan_number VARCHAR(50),

    kyc_submitted BOOLEAN DEFAULT FALSE,
    kyc_approved BOOLEAN DEFAULT FALSE,
    kyc_submitted_at TIMESTAMP NULL,
    kyc_approved_at TIMESTAMP NULL,
    approved_by BIGINT NULL REFERENCES users(id) ON DELETE SET NULL,
    rejection_reason TEXT,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL
);

CREATE INDEX IF NOT EXISTS idx_legacy_vendors_email ON legacy_vendors(email);
CREATE INDEX IF NOT EXISTS idx_legacy_vendors_phone ON legacy_vendors(phone);
CREATE INDEX IF NOT EXISTS idx_legacy_vendors_city ON legacy_vendors(city);
CREATE INDEX IF NOT EXISTS idx_legacy_vendors_state ON legacy_vendors(state);
