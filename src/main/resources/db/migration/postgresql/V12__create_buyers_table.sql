-- V12: Create buyers table with all necessary fields
-- Aligns with Buyer JPA entity

CREATE TABLE IF NOT EXISTS buyers (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT,
    company_id BIGINT,
    
    -- Account Information
    buyer_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(20) UNIQUE,
    password VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255),
    
    -- Buyer Type and Status
    buyer_type VARCHAR(50) DEFAULT 'INDIVIDUAL',
    buyer_status VARCHAR(50) DEFAULT 'ACTIVE',
    is_verified BOOLEAN DEFAULT FALSE,
    is_premium BOOLEAN DEFAULT FALSE,
    is_email_verified BOOLEAN DEFAULT FALSE,
    is_phone_verified BOOLEAN DEFAULT FALSE,
    is_kyc_verified BOOLEAN DEFAULT FALSE,
    
    -- Personal Information
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    display_name VARCHAR(150),
    job_title VARCHAR(100),
    department VARCHAR(100),
    bio TEXT,
    profile_image_url VARCHAR(500),
    
    -- Contact Information
    secondary_email VARCHAR(100),
    secondary_phone VARCHAR(20),
    linkedin_url VARCHAR(500),
    website_url VARCHAR(500),
    company_name VARCHAR(255),
    
    -- Billing Address
    billing_address_line1 VARCHAR(255),
    billing_address_line2 VARCHAR(255),
    billing_city VARCHAR(50),
    billing_state VARCHAR(50),
    billing_postal_code VARCHAR(10),
    billing_country VARCHAR(50) DEFAULT 'India',
    
    -- Shipping Address
    shipping_address_line1 VARCHAR(255),
    shipping_address_line2 VARCHAR(255),
    shipping_city VARCHAR(50),
    shipping_state VARCHAR(50),
    shipping_postal_code VARCHAR(10),
    shipping_country VARCHAR(50) DEFAULT 'India',
    same_as_billing BOOLEAN DEFAULT TRUE,
    
    -- Business Information
    business_type VARCHAR(50),
    company_size VARCHAR(50),
    annual_budget DECIMAL(15, 2),
    purchasing_authority VARCHAR(50),
    credit_limit DECIMAL(15, 2),
    payment_terms_preference INTEGER,
    
    -- Purchase History
    total_orders BIGINT DEFAULT 0,
    completed_orders BIGINT DEFAULT 0,
    cancelled_orders BIGINT DEFAULT 0,
    total_spent DECIMAL(15, 2) DEFAULT 0.00,
    average_order_value DECIMAL(15, 2) DEFAULT 0.00,
    total_order_value DECIMAL(15, 2) DEFAULT 0.00,
    last_order_date TIMESTAMP,
    favorite_vendors TEXT,
    
    -- Engagement Metrics
    profile_views BIGINT DEFAULT 0,
    product_views BIGINT DEFAULT 0,
    inquiries_sent BIGINT DEFAULT 0,
    quotes_requested BIGINT DEFAULT 0,
    reviews_written INTEGER DEFAULT 0,
    wishlist_items INTEGER DEFAULT 0,
    
    -- Communication Preferences
    email_notifications BOOLEAN DEFAULT TRUE,
    sms_notifications BOOLEAN DEFAULT FALSE,
    marketing_emails BOOLEAN DEFAULT TRUE,
    price_alerts BOOLEAN DEFAULT TRUE,
    new_product_alerts BOOLEAN DEFAULT FALSE,
    order_updates BOOLEAN DEFAULT TRUE,
    
    -- Verification and KYC
    verification_status VARCHAR(50) DEFAULT 'PENDING',
    kyc_status VARCHAR(50) DEFAULT 'NOT_SUBMITTED',
    kyc_submitted BOOLEAN DEFAULT FALSE,
    kyc_approved BOOLEAN DEFAULT FALSE,
    kyc_submitted_at TIMESTAMP,
    kyc_approved_at TIMESTAMP,
    kyc_approved_by VARCHAR(255),
    kyc_rejection_reason TEXT,
    kyc_data TEXT,
    kyc_attempts INTEGER DEFAULT 0,
    kyc_verification_date TIMESTAMP,
    
    -- Email/Phone Verification
    email_verification_token VARCHAR(255),
    email_verification_token_expiry TIMESTAMP,
    email_verification_date TIMESTAMP,
    phone_verification_otp VARCHAR(10),
    phone_verification_otp_expiry TIMESTAMP,
    phone_verification_date TIMESTAMP,
    
    -- Subscription
    subscription_type VARCHAR(50) DEFAULT 'FREE',
    subscription_start_date TIMESTAMP,
    subscription_end_date TIMESTAMP,
    subscription_expiry_date TIMESTAMP,
    
    -- Activity Tracking
    last_login TIMESTAMP,
    last_login_date TIMESTAMP,
    last_activity TIMESTAMP,
    login_count BIGINT DEFAULT 0,
    session_duration_minutes BIGINT DEFAULT 0,
    
    -- Privacy and Security
    profile_visibility VARCHAR(50) DEFAULT 'PUBLIC',
    two_factor_enabled BOOLEAN DEFAULT FALSE,
    email_verified BOOLEAN DEFAULT FALSE,
    phone_verified BOOLEAN DEFAULT FALSE,
    
    -- Status Management
    suspension_end_date TIMESTAMP,
    status_reason TEXT,
    
    -- Audit Fields
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    
    CONSTRAINT fk_buyers_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

-- Related tables for collections
CREATE TABLE IF NOT EXISTS buyer_industries (
    buyer_id BIGINT NOT NULL,
    industry VARCHAR(100),
    CONSTRAINT fk_buyer_industries_buyer FOREIGN KEY (buyer_id) REFERENCES buyers(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS buyer_preferred_categories (
    buyer_id BIGINT NOT NULL,
    category VARCHAR(255),
    CONSTRAINT fk_buyer_categories_buyer FOREIGN KEY (buyer_id) REFERENCES buyers(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS buyer_payment_methods (
    buyer_id BIGINT NOT NULL,
    payment_method VARCHAR(50),
    CONSTRAINT fk_buyer_payments_buyer FOREIGN KEY (buyer_id) REFERENCES buyers(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS buyer_documents (
    buyer_id BIGINT NOT NULL,
    document_url VARCHAR(500),
    CONSTRAINT fk_buyer_documents_buyer FOREIGN KEY (buyer_id) REFERENCES buyers(id) ON DELETE CASCADE
);

-- Indexes for better performance
CREATE INDEX IF NOT EXISTS idx_buyers_email ON buyers(email);
CREATE INDEX IF NOT EXISTS idx_buyers_phone ON buyers(phone);
CREATE INDEX IF NOT EXISTS idx_buyers_user_id ON buyers(user_id);
CREATE INDEX IF NOT EXISTS idx_buyers_company_id ON buyers(company_id);
CREATE INDEX IF NOT EXISTS idx_buyers_buyer_status ON buyers(buyer_status);
CREATE INDEX IF NOT EXISTS idx_buyers_is_verified ON buyers(is_verified);
CREATE INDEX IF NOT EXISTS idx_buyers_buyer_type ON buyers(buyer_type);

-- Comments
COMMENT ON TABLE buyers IS 'B2B marketplace buyers/customers';
